-- 操作日志表变更脚本 V1.3
-- 执行时间: 2026-01-13
-- 说明: 添加操作日志表,记录所有系统操作

USE accounting_db;

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '操作人ID',
  `username` VARCHAR(50) NOT NULL COMMENT '操作人用户名',
  `real_name` VARCHAR(50) COMMENT '操作人真实姓名',
  `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型: CREATE, UPDATE, DELETE',
  `module` VARCHAR(20) NOT NULL COMMENT '模块: ASSET, LIABILITY, TRANSACTION',
  `operation_name` VARCHAR(100) NOT NULL COMMENT '操作名称: 创建资产, 更新资产等',
  `description` VARCHAR(500) COMMENT '操作描述',
  `target_id` BIGINT COMMENT '目标记录ID',
  `target_name` VARCHAR(100) COMMENT '目标记录名称(便于查看)',
  `request_data` TEXT COMMENT '请求数据(JSON格式)',
  `result_data` TEXT COMMENT '结果数据(JSON格式)',
  `ip` VARCHAR(50) COMMENT '操作IP',
  `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '状态: SUCCESS, FAILED',
  `error_msg` VARCHAR(1000) COMMENT '错误信息',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_user_module` (`user_id`, `module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
