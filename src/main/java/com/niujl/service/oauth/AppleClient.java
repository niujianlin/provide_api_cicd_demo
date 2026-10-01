package com.niujl.service.oauth;

/**
 * 苹果登录抽象：校验授权凭证并返回用户唯一标识 sub。
 */
public interface AppleClient {

    String getAppleSub(String identityToken, String code);
}
