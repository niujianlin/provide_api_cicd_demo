package com.niujl.controller;

import com.niujl.common.Result;
import com.niujl.dto.AppleLoginRequest;
import com.niujl.dto.LoginResponse;
import com.niujl.dto.PhoneLoginRequest;
import com.niujl.dto.SmsSendRequest;
import com.niujl.dto.WechatLoginRequest;
import com.niujl.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 登录相关接口。
 */
@Tag(name = "登录", description = "短信验证码与第三方登录")
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@Valid @RequestBody SmsSendRequest request) {
        authService.sendSmsCode(request.getPhone());
        return Result.ok();
    }

    @Operation(summary = "手机号 + 验证码登录")
    @PostMapping("/login/phone")
    public Result<LoginResponse> loginByPhone(@Valid @RequestBody PhoneLoginRequest request) {
        return Result.ok(authService.loginByPhone(request.getPhone(), request.getCode()));
    }

    @Operation(summary = "微信登录")
    @PostMapping("/login/wechat")
    public Result<LoginResponse> loginByWechat(@Valid @RequestBody WechatLoginRequest request) {
        return Result.ok(authService.loginByWechat(request.getCode()));
    }

    @Operation(summary = "苹果登录")
    @PostMapping("/login/apple")
    public Result<LoginResponse> loginByApple(@Valid @RequestBody AppleLoginRequest request) {
        return Result.ok(authService.loginByApple(request.getIdentityToken(), request.getCode(), request.getNickname()));
    }

    @Operation(summary = "登出（客户端丢弃 token 即可）")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }
}
