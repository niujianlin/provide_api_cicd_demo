package com.niujl.service.oauth;

import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 真实苹果登录实现骨架。
 *
 * <p>TODO 正式接入步骤：
 * 1. 拉取 Apple 公钥 {@code https://appleid.apple.com/auth/keys}（建议缓存）；
 * 2. 校验 identityToken 的签名与 iss/aud/exp；
 * 3. 返回 payload 中的 {@code sub}。</p>
 * 校验参数：{@code app.apple.client-id}（aud）、{@code app.apple.team-id}。
 */
@Component
@ConditionalOnProperty(name = "app.apple.mock", havingValue = "false")
public class RealAppleClient implements AppleClient {

    private static final Logger log = LoggerFactory.getLogger(RealAppleClient.class);

    @Value("${app.apple.client-id:}")
    private String clientId;

    @Value("${app.apple.team-id:}")
    private String teamId;

    @Override
    public String getAppleSub(String identityToken, String code) {
        if (identityToken == null || identityToken.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "identityToken 不能为空");
        }
        log.warn("苹果登录真实校验尚未接入，clientId={}, teamId={}", clientId, teamId);
        // TODO 校验 Apple identityToken 签名并返回 sub
        throw new BusinessException(ResultCode.THIRD_PARTY_ERROR, "苹果登录尚未接入，请实现 RealAppleClient.getAppleSub");
    }
}
