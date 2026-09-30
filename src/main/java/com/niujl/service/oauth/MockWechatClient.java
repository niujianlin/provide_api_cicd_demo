package com.niujl.service.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Mock 微信实现：由 code 稳定派生一个模拟 openid，便于本地与 CI 联调。
 */
@Component
@ConditionalOnProperty(name = "app.wechat.mock", havingValue = "true", matchIfMissing = true)
public class MockWechatClient implements WechatClient {

    private static final Logger log = LoggerFactory.getLogger(MockWechatClient.class);

    @Override
    public String getOpenid(String code) {
        String openid = "mock_wx_" + md5(code).substring(0, 16);
        log.info("[MOCK-WECHAT] code={} -> openid={}", code, openid);
        return openid;
    }

    private String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("MD5 计算失败", e);
        }
    }
}
