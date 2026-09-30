package com.niujl.service;

import com.niujl.bean.User;
import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import com.niujl.dto.UserInfoResponse;
import com.niujl.mapper.UserMapper;
import com.niujl.util.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final CryptoUtil cryptoUtil;

    public UserService(UserMapper userMapper, CryptoUtil cryptoUtil) {
        this.userMapper = userMapper;
        this.cryptoUtil = cryptoUtil;
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    public void addUser(User user) {
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        userMapper.update(user);
    }

    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 组装对外用户信息（手机号脱敏）。
     */
    public UserInfoResponse getUserInfo(User user) {
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toUserInfo(user);
    }

    public UserInfoResponse getUserInfo(Long userId) {
        return getUserInfo(userMapper.findById(userId));
    }

    public UserInfoResponse toUserInfo(User user) {
        UserInfoResponse info = new UserInfoResponse();
        info.setUserId(user.getId());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());
        info.setPhone(cryptoUtil.maskPhone(user.getPhoneCipher()));
        return info;
    }
}
