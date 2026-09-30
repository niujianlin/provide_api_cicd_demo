package com.niujl.service.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Mock 短信实现：仅打印日志，不真正发送。
 */
@Component
@ConditionalOnProperty(name = "app.sms.mock", havingValue = "true", matchIfMissing = true)
public class MockSmsSender implements SmsSender {

    private static final Logger log = LoggerFactory.getLogger(MockSmsSender.class);

    @Override
    public void send(String phone, String code) {
        log.info("[MOCK-SMS] 模拟发送验证码，phone={}, code={}", mask(phone), code);
    }

    private String mask(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
