package com.niujl.service.oauth;

/**
 * 微信登录抽象：用客户端 code 换取 openid。
 */
public interface WechatClient {

    String getOpenid(String code);
}
