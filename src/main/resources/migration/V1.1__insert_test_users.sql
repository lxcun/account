-- 测试用户数据插入脚本 V1.1
-- 执行时间: 2026-01-13
-- 说明: 插入测试用户数据，方便测试权限控制

USE accounting_db;

-- 删除旧的测试用户数据（如果存在）
DELETE FROM `user_role` WHERE user_id IN (SELECT id FROM `user` WHERE username IN ('admin', 'leader', 'employee'));
DELETE FROM `user` WHERE username IN ('admin', 'leader', 'employee');

-- 1. 插入管理员用户（用户名：admin，密码：admin123）
-- BCrypt加密后的密码: admin123
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 'admin@example.com', 1);

-- 2. 插入领导角色测试用户（用户名：leader，密码：leader123）
-- BCrypt加密后的密码: leader123
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('leader', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试领导', '13800138001', 'leader@example.com', 1);

-- 3. 插入员工角色测试用户（用户名：employee，密码：employee123）
-- BCrypt加密后的密码: employee123
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('employee', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试员工', '13800138002', 'employee@example.com', 1);

-- 4. 为admin用户分配领导角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `user` u, `role` r
WHERE u.username = 'admin' AND r.role_code = 'LEADER';

-- 5. 为leader用户分配领导角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `user` u, `role` r
WHERE u.username = 'leader' AND r.role_code = 'LEADER';

-- 6. 为employee用户分配员工角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `user` u, `role` r
WHERE u.username = 'employee' AND r.role_code = 'EMPLOYEE';
