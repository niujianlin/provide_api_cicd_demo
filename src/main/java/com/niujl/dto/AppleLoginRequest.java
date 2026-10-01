package com.niujl.dto;

import lombok.Data;

@Data
public class AppleLoginRequest {

    /** 苹果登录返回的 identityToken */
    private String identityToken;

    /** 部分客户端只上报授权 code */
    private String code;

    /** 首次授权时的昵称（可选） */
    private String nickname;
}
