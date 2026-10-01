package com.niujl.service;

import com.niujl.bean.SmsCode;
import com.niujl.bean.User;
import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import com.niujl.dto.LoginResponse;
import com.niujl.mapper.SmsCodeMapper;
import com.niujl.mapper.UserMapper;
import com.niujl.service.oauth.AppleClient;
import com.niujl.service.oauth.WechatClient;
import com.niujl.service.sms.SmsSender;
import com.niujl.util.CryptoUtil;
import com.niujl.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

/**
 * 登录 / 登出 / 注销 业务编排。
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserMapper userMapper;
    private final SmsCodeMapper smsCodeMapper;
    private final UserService userService;
    private final CryptoUtil cryptoUtil;
    private final JwtUtil jwtUtil;
    private final SmsSender smsSender;
    private final WechatClient wechatClient;
    private final AppleClient appleClient;

    private final boolean smsMock;
    private final String smsMockCode;
    private final int codeExpireSeconds;
    private final int sendIntervalSeconds;

    public AuthService(UserMapper userMapper,
                       SmsCodeMapper smsCodeMapper,
                       UserService userService,
                       CryptoUtil cryptoUtil,
                       JwtUtil jwtUtil,
                       SmsSender smsSender,
                       WechatClient wechatClient,
                       AppleClient appleClient,
                       @Value("${app.sms.mock:true}") boolean smsMock,
                       @Value("${app.sms.mock-code:123456}") String smsMockCode,
                       @Value("${app.sms.code-expire-seconds:300}") int codeExpireSeconds,
                       @Value("${app.sms.send-interval-seconds:60}") int sendIntervalSeconds) {
        this.userMapper = userMapper;
        this.smsCodeMapper = smsCodeMapper;
        this.userService = userService;
        this.cryptoUtil = cryptoUtil;
        this.jwtUtil = jwtUtil;
        this.smsSender = smsSender;
        this.wechatClient = wechatClient;
        this.appleClient = appleClient;
        this.smsMock = smsMock;
        this.smsMockCode = smsMockCode;
        this.codeExpireSeconds = codeExpireSeconds;
        this.sendIntervalSeconds = sendIntervalSeconds;
    }

    /**
     * 发送短信验证码（含频率限制）。
     */
    public void sendSmsCode(String phone) {
        String phoneCipher = cryptoUtil.encrypt(phone);

        Date since = new Date(System.currentTimeMillis() - sendIntervalSeconds * 1000L);
        if (smsCodeMapper.countSentAfter(phoneCipher, since) > 0) {
            throw new BusinessException(ResultCode.SMS_SEND_TOO_FREQUENT);
        }

        String code = smsMock ? smsMockCode : randomCode();
        SmsCode smsCode = new SmsCode();
        smsCode.setPhoneCipher(phoneCipher);
        smsCode.setCode(code);
        smsCode.setExpireTime(new Date(System.currentTimeMillis() + codeExpireSeconds * 1000L));
        smsCode.setUsed(0);
        smsCodeMapper.insert(smsCode);

        smsSender.send(phone, code);
        log.info("发送验证码成功, phoneCipher={}", prefix(phoneCipher));
    }

    /**
     * 手机号 + 验证码登录，首次登录自动注册。
     */
    public LoginResponse loginByPhone(String phone, String code) {
        String phoneCipher = cryptoUtil.encrypt(phone);
        verifySmsCode(phoneCipher, code);

        User user = userMapper.findByPhoneCipher(phoneCipher);
        if (user == null) {
            user = new User();
            user.setPhoneCipher(phoneCipher);
            user.setNickname(cryptoUtil.maskPhone(phoneCipher));
            user.setIsDeleted(0);
            userMapper.insert(user);
            log.info("手机号首次登录，自动注册 userId={}", user.getId());
        }
        return issue(user);
    }

    /**
     * 微信登录，首次登录自动注册。
     */
    public LoginResponse loginByWechat(String code) {
        String openid = wechatClient.getOpenid(code);
        User user = userMapper.findByWechatOpenid(openid);
        if (user == null) {
            user = new User();
            user.setWechatOpenid(openid);
            user.setNickname("微信用户");
            user.setIsDeleted(0);
            userMapper.insert(user);
            log.info("微信首次登录，自动注册 userId={}", user.getId());
        }
        return issue(user);
    }

    /**
     * 苹果登录，首次登录自动注册。
     */
    public LoginResponse loginByApple(String identityToken, String code, String nickname) {
        if ((identityToken == null || identityToken.isEmpty()) && (code == null || code.isEmpty())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "identityToken 与 code 不能同时为空");
        }
        String sub = appleClient.getAppleSub(identityToken, code);
        User user = userMapper.findByAppleSub(sub);
        if (user == null) {
            user = new User();
            user.setAppleSub(sub);
            user.setNickname(nickname == null || nickname.isEmpty() ? "苹果用户" : nickname);
            user.setIsDeleted(0);
            userMapper.insert(user);
            log.info("苹果首次登录，自动注册 userId={}", user.getId());
        }
        return issue(user);
    }

    /**
     * 登出：JWT 无状态，服务端不持久化 token，由客户端丢弃即可。
     */
    public void logout() {
        // no-op：如后续需要服务端强制失效，可在此引入 token 黑名单/版本号
    }

    /**
     * 注销：is_deleted = 1。
     */
    public void cancelAccount(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.softDeleteById(userId);
        log.info("用户注销成功 userId={}", userId);
    }

    private void verifySmsCode(String phoneCipher, String code) {
        SmsCode latest = smsCodeMapper.findLatestByPhone(phoneCipher);
        if (latest == null
                || code == null
                || !code.equals(latest.getCode())
                || Integer.valueOf(1).equals(latest.getUsed())
                || latest.getExpireTime() == null
                || latest.getExpireTime().before(new Date())) {
            throw new BusinessException(ResultCode.SMS_CODE_ERROR);
        }
        smsCodeMapper.markUsed(latest.getId());
    }

    private LoginResponse issue(User user) {
        if (Integer.valueOf(1).equals(user.getIsDeleted())) {
            throw new BusinessException(ResultCode.USER_CANCELLED);
        }
        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(user.getId()));
        response.setUser(userService.toUserInfo(user));
        return response;
    }

    private String randomCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    private String prefix(String cipher) {
        return cipher == null || cipher.length() <= 8 ? cipher : cipher.substring(0, 8) + "...";
    }
}
