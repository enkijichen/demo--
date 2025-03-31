-- 按照正确的顺序删除表（先删除引用表，再删除被引用表）
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS textbook;
DROP TABLE IF EXISTS user;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,  -- 确保字段长度足够
    INDEX idx_username (username)
);

-- 创建教材表
CREATE TABLE IF NOT EXISTS textbook (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(20) NOT NULL,
    subject VARCHAR(20) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    image_url VARCHAR(255)
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    textbook_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    receiver VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    address VARCHAR(255) NOT NULL COMMENT '收货地址',
    status VARCHAR(20) NOT NULL COMMENT '订单状态：UNPAID-待支付,PAID-已支付,PENDING-待处理,SHIPPED-已发货,RECEIVED-已收货,REJECTED-已拒绝,CANCELLED-已撤回',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NULL COMMENT '更新时间',
    payment_time DATETIME NULL COMMENT '支付时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (textbook_id) REFERENCES textbook(id)
);

-- 创建索引
ALTER TABLE textbook ADD INDEX idx_textbook_grade(grade);
ALTER TABLE textbook ADD INDEX idx_textbook_subject(subject);
ALTER TABLE orders ADD INDEX idx_order_user(user_id);
ALTER TABLE orders ADD INDEX idx_order_status(status);
ALTER TABLE orders ADD INDEX idx_order_create_time(create_time);