package com.niujl.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WechatLoginRequest {

    /** 微信客户端登录返回的临时 code */
    @NotBlank(message = "code 不能为空")
    private String code;
}
