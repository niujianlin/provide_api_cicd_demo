package com.niujl.service.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Mock 苹果实现：由 identityToken/code 稳定派生一个模拟 sub。
 */
@Component
@ConditionalOnProperty(name = "app.apple.mock", havingValue = "true", matchIfMissing = true)
public class MockAppleClient implements AppleClient {

    private static final Logger log = LoggerFactory.getLogger(MockAppleClient.class);

    @Override
    public String getAppleSub(String identityToken, String code) {
        String source = identityToken != null && !identityToken.isEmpty() ? identityToken : code;
        String sub = "mock_apple_" + md5(String.valueOf(source)).substring(0, 16);
        log.info("[MOCK-APPLE] -> sub={}", sub);
        return sub;
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
