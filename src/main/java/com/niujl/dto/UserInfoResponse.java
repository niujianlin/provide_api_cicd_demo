package com.niujl.dto;

import lombok.Data;

/**
 * 对外用户信息（手机号已脱敏，不含密文与密钥）。
 */
@Data
public class UserInfoResponse {

    private Long userId;

    private String nickname;

    private String avatar;

    /** 脱敏手机号，如 138****8000；未绑定时为 null */
    private String phone;
}
