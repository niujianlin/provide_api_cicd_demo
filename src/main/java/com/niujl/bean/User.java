package com.niujl.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体。
 *
 * <p>注意：{@code phoneCipher} 为手机号密文，禁止直接对外输出明文。</p>
 */
@Data
public class User {

    private Long id;

    /** 姓名/昵称（兼容旧字段） */
    private String name;

    /** 邮箱（兼容旧字段，允许为空） */
    private String email;

    private String nickname;

    private String avatar;

    /** 手机号密文（确定性 AES），仅内部使用，不对外序列化 */
    @JsonIgnore
    private String phoneCipher;

    /** 微信 openid，仅内部使用，不对外序列化 */
    @JsonIgnore
    private String wechatOpenid;

    /** 苹果用户唯一标识 sub，仅内部使用，不对外序列化 */
    @JsonIgnore
    private String appleSub;

    /** 是否注销：0 正常，1 已注销 */
    private Integer isDeleted;

    private Date createTime;

    private Date updateTime;
}
