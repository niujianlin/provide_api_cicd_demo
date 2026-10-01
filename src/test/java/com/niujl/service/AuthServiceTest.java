package com.niujl.service;

import com.niujl.bean.User;
import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import com.niujl.dto.LoginResponse;
import com.niujl.mapper.UserMapper;
import com.niujl.util.CryptoUtil;
import com.niujl.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    private String randomPhone() {
        return "138" + String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));
    }

    private String randomCode() {
        return "wx_code_" + ThreadLocalRandom.current().nextInt(1000000);
    }

    @Test
    public void testCryptoIsReversibleAndDeterministic() {
        String phone = "13800138000";
        String cipher1 = cryptoUtil.encrypt(phone);
        String cipher2 = cryptoUtil.encrypt(phone);

        assertEquals(cipher1, cipher2, "确定性加密：同一明文密文应一致");
        assertEquals(phone, cryptoUtil.decrypt(cipher1), "密文应可解密回明文");
        assertEquals("138****8000", cryptoUtil.maskPhone(cipher1));
    }

    @Test
    public void testSendSmsCodeWithIntervalLimit() {
        String phone = randomPhone();
        authService.sendSmsCode(phone);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.sendSmsCode(phone));
        assertEquals(ResultCode.SMS_SEND_TOO_FREQUENT, ex.getResultCode());
    }

    @Test
    public void testLoginByPhoneWithMockCode() {
        String phone = randomPhone();
        authService.sendSmsCode(phone);

        // Mock 模式下验证码固定为 123456
        LoginResponse response = authService.loginByPhone(phone, "123456");
        assertNotNull(response.getToken());
        assertNotNull(response.getUser().getUserId());
        assertEquals("138****" + phone.substring(7), response.getUser().getPhone());

        // token 可解析出同一个 userId
        assertEquals(response.getUser().getUserId(), jwtUtil.parseUserId(response.getToken()));

        // 验证码已失效，二次使用应失败
        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.loginByPhone(phone, "123456"));
        assertEquals(ResultCode.SMS_CODE_ERROR, ex.getResultCode());
    }

    @Test
    public void testLoginByPhoneWithWrongCode() {
        String phone = randomPhone();
        authService.sendSmsCode(phone);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.loginByPhone(phone, "000000"));
        assertEquals(ResultCode.SMS_CODE_ERROR, ex.getResultCode());
    }

    @Test
    public void testLoginByWechatIsIdempotent() {
        String code = randomCode();
        LoginResponse first = authService.loginByWechat(code);
        LoginResponse second = authService.loginByWechat(code);
        assertEquals(first.getUser().getUserId(), second.getUser().getUserId());
    }

    @Test
    public void testLoginByApple() {
        LoginResponse response = authService.loginByApple("apple_token_" + randomCode(), null, null);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser().getUserId());
    }

    @Test
    public void testLoginByAppleWithoutCredential() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.loginByApple(null, null, null));
        assertEquals(ResultCode.PARAM_ERROR, ex.getResultCode());
    }

    @Test
    public void testCancelAccountThenLoginRejected() {
        String code = randomCode();
        LoginResponse response = authService.loginByWechat(code);
        Long userId = response.getUser().getUserId();

        authService.cancelAccount(userId);
        User cancelled = userMapper.findById(userId);
        assertEquals(1, cancelled.getIsDeleted(), "注销后 is_deleted 应为 1");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.loginByWechat(code));
        assertEquals(ResultCode.USER_CANCELLED, ex.getResultCode());
    }

    @Test
    public void testLogoutIsNoOp() {
        assertDoesNotThrow(() -> authService.logout());
    }
}
