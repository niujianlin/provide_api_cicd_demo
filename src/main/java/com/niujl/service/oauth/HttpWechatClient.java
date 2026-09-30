package com.niujl.service.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 真实微信实现：调用 jscode2session 换取 openid。
 * 填入 {@code app.wechat.appid} / {@code app.wechat.secret} 并将 {@code app.wechat.mock} 置为 false 即可启用。
 */
@Component
@ConditionalOnProperty(name = "app.wechat.mock", havingValue = "false")
public class HttpWechatClient implements WechatClient {

    private static final Logger log = LoggerFactory.getLogger(HttpWechatClient.class);

    private static final String URL =
            "https://api.weixin.qq.com/sns/jscode2session"
                    + "?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.wechat.appid:}")
    private String appid;

    @Value("${app.wechat.secret:}")
    private String secret;

    @Override
    public String getOpenid(String code) {
        if (appid.isEmpty() || secret.isEmpty()) {
            throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "微信登录未配置 appid/secret");
        }
        try {
            String body = restTemplate.getForObject(URL, String.class, appid, secret, code);
            JSONObject json = JSON.parseObject(body);
            if (json == null) {
                throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "微信返回为空");
            }
            Integer errcode = json.getInteger("errcode");
            if (errcode != null && errcode != 0) {
                log.warn("微信 jscode2session 失败: {}", json.toJSONString());
                throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "微信登录失败: " + json.getString("errmsg"));
            }
            String openid = json.getString("openid");
            if (openid == null || openid.isEmpty()) {
                throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "微信登录失败: 未获取到 openid");
            }
            return openid;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "微信登录服务异常");
        }
    }
}
