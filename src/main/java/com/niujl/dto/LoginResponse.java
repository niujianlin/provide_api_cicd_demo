package com.niujl.dto;

import lombok.Data;

@Data
public class LoginResponse {

    /** JWT，客户端需在后续请求的 Authorization 头中携带 */
    private String token;

    private UserInfoResponse user;
}
