-- =============================================
-- 迁移：为已有用户的分类设置 direction 方向
-- 并补充新分类
-- =============================================

-- 1. 将已有分类的 direction 设为默认值 2（支出）
--    只处理 direction 为 NULL 的记录
UPDATE `category` SET `direction` = 2 WHERE `direction` IS NULL;

-- 2. 将"工资"设为收入方向
UPDATE `category` SET `direction` = 1 WHERE `name` = '工资' AND `direction` != 1;

-- 3. 为每个用户补充缺少的新分类
--    需要的支出分类：交通、住房、医疗、教育、零食、饮料、餐饮、游戏充值、订阅会员
--    需要的收入分类：工资、生活费

-- 为每个已有用户添加"饮料"（如果还没有）
INSERT INTO `category` (`user_id`, `name`, `type`, `icon`, `direction`)
SELECT u.id, '饮料', 2, 'drink', 2
FROM `user` u
WHERE NOT EXISTS (
    SELECT 1 FROM `category` c WHERE c.user_id = u.id AND c.name = '饮料'
);

-- 为每个已有用户添加"生活费"（如果还没有）
INSERT INTO `category` (`user_id`, `name`, `type`, `icon`, `direction`)
SELECT u.id, '生活费', 1, 'living', 1
FROM `user` u
WHERE NOT EXISTS (
    SELECT 1 FROM `category` c WHERE c.user_id = u.id AND c.name = '生活费'
);
