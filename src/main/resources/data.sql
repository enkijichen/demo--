-- 初始化管理员账号 (密码: admin123)
INSERT INTO user (username, password, email, phone, role)
VALUES ('admin1234', '$2a$10$3jGlxTEHD4j3/uYRYXqOz.4REL4cO.GBpGRijDcvzZGI0F3FX5VXi', 'admin@example.com', '13800000000', 'ADMIN');

-- 初始化测试用户账号 (密码: aka123)
INSERT INTO user (username, password, email, phone, role)
VALUES ('aka1234', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'user@example.com', '13900000000', 'USER');

-- 初始化教材数据
INSERT INTO textbook (name, grade, subject, price, stock) VALUES 
-- 初中教材
('初一数学上册', '初一', '数学', 29.90, 100),
('初一数学下册', '初一', '数学', 29.90, 100),
('初一语文上册', '初一', '语文', 25.90, 100),
('初一语文下册', '初一', '语文', 25.90, 100),
('初一英语上册', '初一', '外语', 27.90, 100),
('初一英语下册', '初一', '外语', 27.90, 100),

('初二数学上册', '初二', '数学', 31.90, 100),
('初二数学下册', '初二', '数学', 31.90, 100),
('初二语文上册', '初二', '语文', 26.90, 100),
('初二语文下册', '初二', '语文', 26.90, 100),
('初二英语上册', '初二', '外语', 28.90, 100),
('初二英语下册', '初二', '外语', 28.90, 100),
('初二物理上册', '初二', '物理', 32.90, 100),
('初二物理下册', '初二', '物理', 32.90, 100),

('初三数学上册', '初三', '数学', 33.90, 100),
('初三数学下册', '初三', '数学', 33.90, 100),
('初三语文上册', '初三', '语文', 27.90, 100),
('初三语文下册', '初三', '语文', 27.90, 100),
('初三英语上册', '初三', '外语', 29.90, 100),
('初三英语下册', '初三', '外语', 29.90, 100),

-- 高中教材
('高一数学上册', '高一', '数学', 38.90, 100),
('高一数学下册', '高一', '数学', 38.90, 100),
('高一语文上册', '高一', '语文', 35.90, 100),
('高一语文下册', '高一', '语文', 35.90, 100),
('高一英语上册', '高一', '外语', 36.90, 100),
('高一英语下册', '高一', '外语', 36.90, 100),
('高一物理上册', '高一', '物理', 39.90, 100),
('高一物理下册', '高一', '物理', 39.90, 100),
('高一化学上册', '高一', '化学', 37.90, 100),
('高一化学下册', '高一', '化学', 37.90, 100),

('高中语文必修一', '高一', '语文', 29.80, 100),
('高中数学必修一', '高一', '数学', 35.50, 100),
('高中英语必修一', '高一', '英语', 32.90, 100),
('高中物理必修一', '高一', '物理', 31.20, 100),
('高中化学必修一', '高一', '化学', 30.60, 100);

-- 初始化一些测试订单
INSERT INTO orders (user_id, textbook_id, quantity, receiver, phone, address, status, create_time)
SELECT 2, id, 1, '测试用户', '13900000000', '测试地址', 'PENDING', NOW()
FROM textbook
WHERE grade = '初一'
LIMIT 3; 