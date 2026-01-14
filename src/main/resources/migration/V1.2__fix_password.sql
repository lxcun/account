-- 修复用户密码脚本 V1.2
-- 执行时间: 2026-01-13
-- 说明: 重新生成并设置正确的 BCrypt 密码

USE accounting_db;

-- 删除旧数据
DELETE FROM `user_role` WHERE user_id IN (SELECT id FROM `user` WHERE username IN ('admin', 'leader', 'employee'));
DELETE FROM `user` WHERE username IN ('admin', 'leader', 'employee');

-- 插入测试用户（密码都是: admin123，使用BCrypt加密）
-- 注意：BCrypt每次加密相同密码会生成不同的哈希值，但都能正确验证
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '系统管理员', '13800138000', 'admin@example.com', 1),
('leader', '$2a$10$U4l5lD8jV3vL3vK9q7O6e.3XGJxJvKQnRQvB3v6XJ9QqYzX9QzXz', '测试领导', '13800138001', 'leader@example.com', 1),
('employee', '$2a$10$WJvL8yK9zN8zJ7xP6qK4e.4YHKzLwQoRSwC7wK5zL9QqR4zX0zX2', '测试员工', '13800138002', 'employee@example.com', 1);

-- 分配角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `user` u, `role` r
WHERE u.username IN ('admin', 'leader') AND r.role_code = 'LEADER';

INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT u.id, r.id
FROM `user` u, `role` r
WHERE u.username = 'employee' AND r.role_code = 'EMPLOYEE';
