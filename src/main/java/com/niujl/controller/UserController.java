package com.niujl.controller;

import com.niujl.bean.User;
import com.niujl.common.Result;
import com.niujl.context.UserContext;
import com.niujl.dto.UserInfoResponse;
import com.niujl.service.AuthService;
import com.niujl.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户", description = "用户信息与账号注销")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Operation(summary = "当前登录用户信息（手机号脱敏）")
    @GetMapping("/me")
    public Result<UserInfoResponse> me() {
        return Result.ok(userService.getUserInfo(UserContext.requireUserId()));
    }

    @Operation(summary = "注销当前账号（is_deleted = 1）")
    @DeleteMapping("/me")
    public Result<Void> cancelAccount() {
        authService.cancelAccount(UserContext.requireUserId());
        return Result.ok();
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
