package com.niujl.service.sms;

import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 真实短信实现骨架。
 *
 * <p>TODO 接入具体短信服务商（阿里云/腾讯云等）：
 * 使用 {@code app.sms.access-key-id} / {@code app.sms.access-key-secret} 调用其发送接口。</p>
 */
@Component
@ConditionalOnProperty(name = "app.sms.mock", havingValue = "false")
public class RealSmsSender implements SmsSender {

    @Value("${app.sms.access-key-id:}")
    private String accessKeyId;

    @Value("${app.sms.access-key-secret:}")
    private String accessKeySecret;

    @Override
    public void send(String phone, String code) {
        if (accessKeyId == null || accessKeyId.isEmpty() || accessKeySecret == null || accessKeySecret.isEmpty()) {
            throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "短信服务未配置，请设置 app.sms.access-key-id/secret");
        }
        // TODO 在此调用真实短信服务商 API
        throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "短信服务尚未接入，请实现 RealSmsSender.send");
    }
}
