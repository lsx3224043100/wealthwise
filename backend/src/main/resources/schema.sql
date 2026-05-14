-- 创建数据库
CREATE DATABASE IF NOT EXISTS `wealthwise` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `wealthwise`;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    `password` VARCHAR(32) NOT NULL COMMENT 'MD5加密后的密码',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 类型表 (消费分类)
CREATE TABLE `category` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '所属用户',
    `name` VARCHAR(30) NOT NULL COMMENT '类型名称',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类别: 1=正常消费, 2=不必要消费, 3=可优化消费',
    `icon` VARCHAR(20) DEFAULT '' COMMENT '图标标识',
    `direction` TINYINT NOT NULL DEFAULT 2 COMMENT '方向: 1=收入, 2=支出',
    FOREIGN KEY (`user_id`) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 账单表
CREATE TABLE `bill` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '所属用户',
    `category_id` INT NOT NULL COMMENT '关联类型',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `direction` TINYINT NOT NULL DEFAULT 2 COMMENT '方向: 1=收入, 2=支出',
    `remark` VARCHAR(200) DEFAULT '' COMMENT '备注',
    `bill_date` DATE NOT NULL COMMENT '记账日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `consumption_type` TINYINT DEFAULT 1 COMMENT '消费类型: 1=正常, 2=可优化, 3=冲动',
    `payment_method` VARCHAR(20) DEFAULT '微信' COMMENT '支付方式: 微信/支付宝/现金/银行卡',
    `bill_time` VARCHAR(5) DEFAULT '' COMMENT '账单时间: HH:mm',
    FOREIGN KEY (`user_id`) REFERENCES `user`(id) ON DELETE CASCADE,
    FOREIGN KEY (`category_id`) REFERENCES `category`(id) ON DELETE RESTRICT,
    INDEX idx_user_date (`user_id`, `bill_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认消费类型（需要先创建至少一个用户）
-- 以下SQL应在创建用户后手动执行，或使用存储过程自动关联
-- INSERT INTO `category` (`user_id`, `name`, `type`, `icon`) VALUES
-- (1, '餐饮', 1, 'food'),
-- (1, '交通', 1, 'transport'),
-- (1, '住房', 1, 'house'),
-- (1, '娱乐', 2, 'game'),
-- (1, '购物', 3, 'shop'),
-- (1, '工资', 1, 'salary');
