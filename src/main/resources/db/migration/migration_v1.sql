-- ============================================================
-- 用户中心 · 服务器旧表迁移脚本（一次性执行）
-- 场景：服务器 users 表已存在且含 name/email 数据，需要保留数据并扩展字段
-- 注意：MySQL 不支持 ADD COLUMN IF NOT EXISTS，本脚本只能执行一次
-- 执行前建议先确认目标列不存在：
--   SELECT COLUMN_NAME FROM information_schema.COLUMNS
--    WHERE TABLE_SCHEMA='apiprovide' AND TABLE_NAME='users';
-- 执行方式（示例）：
--   mysql -h<host> -u<user> -p apiprovide < migration_v1.sql
-- 本脚本不参与 spring.sql.init 自动执行，请勿放入 db/schema.sql
-- ============================================================

ALTER TABLE `users`
  MODIFY COLUMN `name`  VARCHAR(64)  NULL COMMENT '姓名/昵称(兼容旧字段)',
  MODIFY COLUMN `email` VARCHAR(128) NULL COMMENT '邮箱(兼容旧字段,允许为空)',
  ADD COLUMN `nickname`      VARCHAR(64)  NULL COMMENT '昵称' AFTER `email`,
  ADD COLUMN `avatar`        VARCHAR(512) NULL COMMENT '头像URL' AFTER `nickname`,
  ADD COLUMN `phone_cipher`  VARCHAR(255) NULL COMMENT '手机号密文' AFTER `avatar`,
  ADD COLUMN `wechat_openid` VARCHAR(64)  NULL COMMENT '微信openid' AFTER `phone_cipher`,
  ADD COLUMN `apple_sub`     VARCHAR(64)  NULL COMMENT '苹果sub' AFTER `wechat_openid`,
  ADD COLUMN `is_deleted`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否注销' AFTER `apple_sub`,
  ADD COLUMN `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `is_deleted`,
  ADD COLUMN `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
  ADD UNIQUE KEY `uk_phone_cipher` (`phone_cipher`),
  ADD UNIQUE KEY `uk_wechat_openid` (`wechat_openid`),
  ADD UNIQUE KEY `uk_apple_sub` (`apple_sub`);

-- 验证码表：直接执行 db/schema.sql 中的 CREATE TABLE IF NOT EXISTS `user_sms_code` 语句即可
