package com.niujl.bean;

import lombok.Data;

import java.util.Date;

/**
 * 短信验证码实体。
 */
@Data
public class SmsCode {

    private Long id;

    /** 手机号密文（与 users.phone_cipher 同规则） */
    private String phoneCipher;

    private String code;

    private Date expireTime;

    /** 是否已使用：0 未用，1 已用 */
    private Integer used;

    private Date createTime;
}
