package com.niujl.mapper;

import com.niujl.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> findAll();

    User findById(Long id);

    /**
     * 按手机号密文查询（确定性加密，可等值命中唯一索引）。
     */
    User findByPhoneCipher(@Param("phoneCipher") String phoneCipher);

    User findByWechatOpenid(@Param("wechatOpenid") String wechatOpenid);

    User findByAppleSub(@Param("appleSub") String appleSub);

    void insert(User user);

    void update(User user);

    /**
     * 注销：软删除，仅置 is_deleted = 1。
     */
    int softDeleteById(@Param("id") Long id);

    void deleteById(Long id);
}
