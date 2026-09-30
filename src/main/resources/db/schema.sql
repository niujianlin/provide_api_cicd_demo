-- ============================================================
-- 用户中心 · 幂等建表脚本
-- 由 spring.sql.init.mode=always 在应用启动时自动执行
-- 适用于 CI / 本地 / 全新环境；服务器旧表请改用 db/migration/migration_v1.sql
-- ============================================================

-- 用户表（在原 id/name/email 基础上扩展）
CREATE TABLE IF NOT EXISTS `users` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`          VARCHAR(64)  DEFAULT NULL COMMENT '姓名/昵称(兼容旧字段)',
  `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱(兼容旧字段,允许为空)',
  `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
  `avatar`        VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  `phone_cipher`  VARCHAR(255) DEFAULT NULL COMMENT '手机号密文(确定性AES:固定Key+固定IV+pepper)',
  `wechat_openid` VARCHAR(64)  DEFAULT NULL COMMENT '微信openid',
  `apple_sub`     VARCHAR(64)  DEFAULT NULL COMMENT '苹果用户唯一标识sub',
  `is_deleted`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否注销:0正常,1已注销',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone_cipher` (`phone_cipher`),
  UNIQUE KEY `uk_wechat_openid` (`wechat_openid`),
  UNIQUE KEY `uk_apple_sub` (`apple_sub`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 短信验证码表
CREATE TABLE IF NOT EXISTS `user_sms_code` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone_cipher` VARCHAR(255) NOT NULL COMMENT '手机号密文(与users同规则)',
  `code`         VARCHAR(8)   NOT NULL COMMENT '验证码',
  `expire_time`  DATETIME     NOT NULL COMMENT '过期时间',
  `used`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已使用:0未用,1已用',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_phone_cipher` (`phone_cipher`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表';
