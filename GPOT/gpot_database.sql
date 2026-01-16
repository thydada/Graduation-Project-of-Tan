-- GPOT快递管理系统数据库表结构
-- 数据库名: gpot
-- 创建时间: 2025年

-- 使用数据库
USE gpot;

-- ================================
-- 用户管理体系
-- ================================

-- 管理员表
CREATE TABLE admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '管理员表';

-- 员工表
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(50) COMMENT '部门',
    position VARCHAR(50) COMMENT '职位',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '员工表';

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    id_card VARCHAR(18) COMMENT '身份证号',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- ================================
-- 核心业务模块
-- ================================

-- 仓库表
CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '仓库ID',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    warehouse_code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库代码',
    address VARCHAR(200) COMMENT '地址',
    manager_id BIGINT COMMENT '管理员ID',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (manager_id) REFERENCES admin(id)
) COMMENT '仓库表';

-- 货架表
CREATE TABLE shelf (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '货架ID',
    shelf_code VARCHAR(50) NOT NULL UNIQUE COMMENT '货架编码',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    shelf_type VARCHAR(20) COMMENT '货架类型',
    capacity INT DEFAULT 0 COMMENT '容量',
    current_count INT DEFAULT 0 COMMENT '当前数量',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) COMMENT '货架表';

-- 快递包裹表
CREATE TABLE package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '包裹ID',
    tracking_number VARCHAR(100) NOT NULL UNIQUE COMMENT '快递单号',
    sender_name VARCHAR(50) COMMENT '寄件人姓名',
    sender_phone VARCHAR(20) COMMENT '寄件人电话',
    sender_address VARCHAR(200) COMMENT '寄件人地址',
    receiver_name VARCHAR(50) COMMENT '收件人姓名',
    receiver_phone VARCHAR(20) COMMENT '收件人电话',
    receiver_address VARCHAR(200) COMMENT '收件人地址',
    package_type VARCHAR(20) COMMENT '包裹类型',
    weight DECIMAL(10,2) COMMENT '重量(kg)',
    size VARCHAR(50) COMMENT '尺寸',
    status VARCHAR(20) DEFAULT '待入库' COMMENT '状态(待入库、入库中、已入库、待取件、已取件、异常)',
    warehouse_id BIGINT COMMENT '仓库ID',
    shelf_id BIGINT COMMENT '货架ID',
    entry_employee_id BIGINT COMMENT '入库员工ID',
    entry_time DATETIME COMMENT '入库时间',
    user_id BIGINT COMMENT '所属用户ID',
    pickup_deadline DATETIME COMMENT '取件截止时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    FOREIGN KEY (shelf_id) REFERENCES shelf(id),
    FOREIGN KEY (entry_employee_id) REFERENCES employee(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '快递包裹表';

-- 入库记录表
CREATE TABLE package_entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入库记录ID',
    package_id BIGINT NOT NULL COMMENT '包裹ID',
    employee_id BIGINT NOT NULL COMMENT '操作员工ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    shelf_id BIGINT NOT NULL COMMENT '货架ID',
    entry_method VARCHAR(20) COMMENT '入库方式(扫码录入,自动分拣)',
    entry_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    remarks VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (package_id) REFERENCES package(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    FOREIGN KEY (shelf_id) REFERENCES shelf(id)
) COMMENT '入库记录表';

-- 取件码表
CREATE TABLE pickup_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '取件码ID',
    package_id BIGINT NOT NULL COMMENT '包裹ID',
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '取件码',
    status TINYINT DEFAULT 1 COMMENT '状态(1:有效,0:已使用,2:过期)',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (package_id) REFERENCES package(id)
) COMMENT '取件码表';

-- 取件记录表
CREATE TABLE pickup_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '取件记录ID',
    package_id BIGINT NOT NULL COMMENT '包裹ID',
    user_id BIGINT NOT NULL COMMENT '取件用户ID',
    pickup_code VARCHAR(20) COMMENT '取件码',
    pickup_method VARCHAR(20) COMMENT '取件方式',
    pickup_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '取件时间',
    employee_id BIGINT COMMENT '协助员工ID',
    remarks VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (package_id) REFERENCES package(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
) COMMENT '取件记录表';

-- ================================
-- 库存管理模块
-- ================================

-- 库存盘点表
CREATE TABLE inventory_check (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '盘点ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    employee_id BIGINT NOT NULL COMMENT '盘点员工ID',
    check_date DATE NOT NULL COMMENT '盘点日期',
    total_packages INT DEFAULT 0 COMMENT '应有包裹总数',
    actual_packages INT DEFAULT 0 COMMENT '实际包裹总数',
    discrepancies INT DEFAULT 0 COMMENT '差异数量',
    status VARCHAR(20) DEFAULT '进行中' COMMENT '状态(进行中,已完成)',
    check_result TEXT COMMENT '盘点结果',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
) COMMENT '库存盘点表';

-- 异常件表
CREATE TABLE exception_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '异常件ID',
    package_id BIGINT NOT NULL COMMENT '包裹ID',
    exception_type VARCHAR(50) NOT NULL COMMENT '异常类型',
    exception_reason VARCHAR(200) COMMENT '异常原因',
    report_employee_id BIGINT NOT NULL COMMENT '报告员工ID',
    handle_employee_id BIGINT COMMENT '处理员工ID',
    handle_status VARCHAR(20) DEFAULT '待处理' COMMENT '处理状态(待处理,处理中,已处理)',
    handle_result VARCHAR(200) COMMENT '处理结果',
    report_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报告时间',
    handle_time DATETIME COMMENT '处理时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (package_id) REFERENCES package(id),
    FOREIGN KEY (report_employee_id) REFERENCES employee(id),
    FOREIGN KEY (handle_employee_id) REFERENCES employee(id)
) COMMENT '异常件表';

-- 逾期件表
CREATE TABLE overdue_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '逾期件ID',
    package_id BIGINT NOT NULL COMMENT '包裹ID',
    overdue_days INT DEFAULT 0 COMMENT '逾期天数',
    reminder_count INT DEFAULT 0 COMMENT '提醒次数',
    last_reminder_time DATETIME COMMENT '最后提醒时间',
    status VARCHAR(20) DEFAULT '逾期中' COMMENT '状态(逾期中,已提醒,已取走)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (package_id) REFERENCES package(id)
) COMMENT '逾期件表';

-- ================================
-- 智能运维与数据分析模块
-- ================================

-- 运营统计表
CREATE TABLE operation_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    stats_date DATE NOT NULL COMMENT '统计日期',
    warehouse_id BIGINT COMMENT '仓库ID',
    total_packages INT DEFAULT 0 COMMENT '总包裹数',
    entry_packages INT DEFAULT 0 COMMENT '入库包裹数',
    pickup_packages INT DEFAULT 0 COMMENT '取件包裹数',
    exception_packages INT DEFAULT 0 COMMENT '异常包裹数',
    overdue_packages INT DEFAULT 0 COMMENT '逾期包裹数',
    user_registrations INT DEFAULT 0 COMMENT '用户注册数',
    revenue DECIMAL(10,2) DEFAULT 0 COMMENT '营收',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    UNIQUE KEY uk_stats_date_warehouse (stats_date, warehouse_id)
) COMMENT '运营统计表';

-- 消息表
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型(取件提醒,促销信息,系统公告)',
    sender_type VARCHAR(20) COMMENT '发送者类型(admin,employee,system)',
    sender_id BIGINT COMMENT '发送者ID',
    receiver_type VARCHAR(20) COMMENT '接收者类型(all,user,employee)',
    receiver_id BIGINT COMMENT '接收者ID',
    status VARCHAR(20) DEFAULT '未读' COMMENT '状态(未读,已读,已删除)',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    read_time DATETIME COMMENT '阅读时间',
    FOREIGN KEY (sender_id) REFERENCES admin(id),
    FOREIGN KEY (receiver_id) REFERENCES user(id)
) COMMENT '消息表';

-- 公告表
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    priority TINYINT DEFAULT 1 COMMENT '优先级(1:普通,2:重要,3:紧急)',
    status TINYINT DEFAULT 1 COMMENT '状态(1:发布中,0:已撤销)',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    revoke_time DATETIME COMMENT '撤销时间',
    publisher_id BIGINT COMMENT '发布者ID',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    FOREIGN KEY (publisher_id) REFERENCES admin(id)
) COMMENT '公告表';

-- ================================
-- 初始化数据
-- ================================

-- 创建索引以提升查询性能
CREATE INDEX idx_package_tracking ON package(tracking_number);
CREATE INDEX idx_package_status ON package(status);
CREATE INDEX idx_package_user ON package(user_id);
CREATE INDEX idx_pickup_code_code ON pickup_code(code);
CREATE INDEX idx_message_receiver ON message(receiver_id, receiver_type);
CREATE INDEX idx_operation_stats_date ON operation_stats(stats_date);