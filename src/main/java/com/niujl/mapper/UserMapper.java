package com.niujl.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.niujl.bean.User;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();
    User findById(Long id);
    void insert(User user);
    void update(User user);
    void deleteById(Long id);
}