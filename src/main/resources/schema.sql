CREATE DATABASE IF NOT EXISTS accounting_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE accounting_db;

-- 资产表
CREATE TABLE IF NOT EXISTS `asset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '资产名称，如：现金、银行卡、支付宝、股票等',
  `type` VARCHAR(20) NOT NULL COMMENT '资产类型：CASH(现金)、BANK(银行存款)、ALIPAY(支付宝)、WECHAT(微信)、STOCK(股票)、FUND(基金)、OTHER(其他)',
  `balance` DECIMAL(15, 2) NOT NULL DEFAULT 0 COMMENT '当前余额',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产表';

-- 负债表
CREATE TABLE IF NOT EXISTS `liability` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '负债名称，如：信用卡、房贷、车贷等',
  `type` VARCHAR(20) NOT NULL COMMENT '负债类型：CREDIT_CARD(信用卡)、MORTGAGE(房贷)、CAR_LOAN(车贷)、PERSONAL_LOAN(个人借款)、OTHER(其他)',
  `balance` DECIMAL(15, 2) NOT NULL DEFAULT 0 COMMENT '当前欠款余额',
  `description` VARCHAR(500) COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='负债表';

-- 交易记录表
CREATE TABLE IF NOT EXISTS `transaction_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` VARCHAR(10) NOT NULL COMMENT '类型: INCOME(收入) / EXPENSE(支出)',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '金额',
  `category` VARCHAR(50) NOT NULL COMMENT '分类',
  `description` VARCHAR(500) COMMENT '描述',
  `transaction_date` DATETIME NOT NULL COMMENT '交易日期',
  `asset_id` BIGINT COMMENT '关联的资产ID（收入增加到哪个资产，或支出从哪个资产扣除）',
  `liability_id` BIGINT COMMENT '关联的负债ID（还款减少负债）',
  `account_change_type` VARCHAR(20) COMMENT '账户变动类型：ASSET_INCREASE(资产增加)、ASSET_DECREASE(资产减少)、LIABILITY_INCREASE(负债增加)、LIABILITY_DECREASE(负债减少)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_liability_id` (`liability_id`),
  KEY `idx_transaction_date` (`transaction_date`),
  CONSTRAINT `fk_transaction_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_transaction_liability` FOREIGN KEY (`liability_id`) REFERENCES `liability` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';
