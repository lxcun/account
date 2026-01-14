-- 用户登录模块变更脚本 V1.0
-- 执行时间: 2026-01-13
-- 说明: 添加用户、角色、权限相关表，并修改现有表支持审计字段

USE accounting_db;

-- 1. 创建角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` VARCHAR(20) NOT NULL COMMENT '角色编码：LEADER(领导)、EMPLOYEE(员工)',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(200) COMMENT '角色描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 2. 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 3. 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 4. 修改资产表，添加审计字段
ALTER TABLE `asset`
ADD COLUMN `create_by` BIGINT COMMENT '创建人ID',
ADD COLUMN `update_by` BIGINT COMMENT '更新人ID',
ADD KEY `idx_create_by` (`create_by`),
ADD KEY `idx_update_by` (`update_by`);

-- 5. 修改负债表，添加审计字段
ALTER TABLE `liability`
ADD COLUMN `create_by` BIGINT COMMENT '创建人ID',
ADD COLUMN `update_by` BIGINT COMMENT '更新人ID',
ADD KEY `idx_create_by` (`create_by`),
ADD KEY `idx_update_by` (`update_by`);

-- 6. 修改交易记录表，添加审计字段
ALTER TABLE `transaction_record`
ADD COLUMN `create_by` BIGINT COMMENT '创建人ID',
ADD COLUMN `update_by` BIGINT COMMENT '更新人ID',
ADD KEY `idx_create_by` (`create_by`),
ADD KEY `idx_update_by` (`update_by`);

-- 7. 初始化角色数据
INSERT INTO `role` (`role_code`, `role_name`, `description`) VALUES
('LEADER', '领导', '领导角色，拥有数据的增删改查权限'),
('EMPLOYEE', '员工', '普通员工角色，仅拥有数据查询权限')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), description=VALUES(description);
