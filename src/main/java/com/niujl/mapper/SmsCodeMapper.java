package com.niujl.mapper;

import com.niujl.bean.SmsCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface SmsCodeMapper {

    void insert(SmsCode smsCode);

    /**
     * 查询该手机号最近一条验证码记录。
     */
    SmsCode findLatestByPhone(@Param("phoneCipher") String phoneCipher);

    /**
     * 统计该手机号在指定时间之后发送的次数，用于频率限制。
     */
    int countSentAfter(@Param("phoneCipher") String phoneCipher, @Param("since") Date since);

    /**
     * 标记验证码已使用。
     */
    int markUsed(@Param("id") Long id);
}
