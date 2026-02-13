/*
 Navicat Premium Data Transfer

 Source Server         : Conn
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : localhost:3306
 Source Schema         : gpot

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 12/02/2026 13:31:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'thydada', '12345', NULL, NULL, NULL, NULL, 1, '2026-01-12 22:34:37', '2026-01-12 22:34:37');

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '公告内容',
  `priority` tinyint(0) NULL DEFAULT 1 COMMENT '优先级(1:普通,2:重要,3:紧急)',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:发布中,0:已撤销)',
  `publish_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `revoke_time` datetime(0) NULL DEFAULT NULL COMMENT '撤销时间',
  `publisher_id` bigint(0) NULL DEFAULT NULL COMMENT '发布者ID',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `view_count` int(0) NULL DEFAULT 0 COMMENT '查看次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `announcement_ibfk_2` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '部门',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '职位',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, 'thydada', '1234', '谭浩毅', '18862666523', '3053596379@qq.com', 'B', '江苏苏州', 1, 1, '2026-01-19 16:53:50', '2026-02-05 20:21:19');
INSERT INTO `employee` VALUES (2, 'thydada2', '12345', '韩立', '18114523658', '525752231@qq.com', 'B', '江苏徐州', 1, 1, '2026-01-20 16:53:50', '2026-01-29 11:23:00');

-- ----------------------------
-- Table structure for exception_package
-- ----------------------------
DROP TABLE IF EXISTS `exception_package`;
CREATE TABLE `exception_package`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '异常件ID',
  `package_id` bigint(0) NULL DEFAULT NULL COMMENT '包裹ID（正式包裹ID，可为空）',
  `exception_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '异常类型',
  `exception_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异常原因',
  `report_employee_id` bigint(0) NOT NULL COMMENT '报告员工ID',
  `handle_employee_id` bigint(0) NULL DEFAULT NULL COMMENT '处理员工ID',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `handle_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '待处理' COMMENT '处理状态(待处理,处理中,已处理)',
  `handle_result` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '处理结果',
  `report_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报告时间',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `report_employee_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '报告员工姓名',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异常来源（pickup:取件异常, verification:核验异常）',
  `tracking_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '快递单号',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '所属用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  INDEX `report_employee_id`(`report_employee_id`) USING BTREE,
  INDEX `handle_employee_id`(`handle_employee_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `exception_package_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `exception_package_ibfk_2` FOREIGN KEY (`report_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `exception_package_ibfk_3` FOREIGN KEY (`handle_employee_id`) REFERENCES `employee` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `exception_package_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `exception_package_ibfk_5` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '异常件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exception_package
-- ----------------------------
INSERT INTO `exception_package` VALUES (4, NULL, '收件人信息错误', 'd', 1, NULL, NULL, '待处理', NULL, '2026-01-29 03:30:36', NULL, '2026-01-29 03:30:36', '员工1', 'pickup', 'GPOT1769657223392285', 8, 1);
INSERT INTO `exception_package` VALUES (5, NULL, '包裹信息不符', '123', 2, NULL, NULL, '待处理', NULL, '2026-01-29 03:30:53', NULL, '2026-01-29 03:30:53', '员工2', 'verification', 'GPOT1769657214797364', 7, 1);
INSERT INTO `exception_package` VALUES (6, NULL, '收件人信息错误', 'ddddx1', 1, NULL, NULL, '待处理', NULL, '2026-01-30 04:58:40', NULL, '2026-01-30 04:58:40', '员工1', 'pickup', 'GPOT1769749027534032', 17, 5);
INSERT INTO `exception_package` VALUES (7, NULL, '包裹破损', 'sssss', 2, NULL, NULL, '待处理', NULL, '2026-01-30 05:01:01', NULL, '2026-01-30 05:01:01', '员工2', 'verification', 'GPOT1769749021583696', 16, 5);
INSERT INTO `exception_package` VALUES (8, 14, '包裹破损', '2222', 1, NULL, NULL, '待处理', NULL, '2026-02-06 02:39:28', NULL, '2026-02-06 02:39:28', '谭浩毅', 'inbound', 'DBG1770345143004665', 0, NULL);
INSERT INTO `exception_package` VALUES (9, 23, '包裹破损', '2026年2月6日11:22:023', 1, NULL, NULL, '待处理', NULL, '2026-02-06 03:22:05', NULL, '2026-02-06 03:22:05', '谭浩毅', 'inbound', 'DBG1770348016093856', 0, 1);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息类型(取件提醒,促销信息,系统公告)',
  `sender_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发送者类型(admin,employee,system)',
  `sender_id` bigint(0) NULL DEFAULT NULL COMMENT '发送者ID',
  `receiver_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '接收者类型(all,user,employee)',
  `receiver_id` bigint(0) NULL DEFAULT NULL COMMENT '接收者ID',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '未读' COMMENT '状态(未读,已读,已删除)',
  `send_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id`) USING BTREE,
  INDEX `idx_message_receiver`(`receiver_id`, `receiver_type`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_3` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, '取件提醒', '您的快递已入库，请及时取件。\n\n快递单号：DBG1770346255706571\n取件码：1-1-JUKTCG\n货架位置：货架1 - 第1层\n\n请携带有效证件到快递驿站取件。', '取件提醒', 'employee', 1, 'user', 1, 1, '已读', '2026-02-09 03:00:23', '2026-02-09 03:01:24');
INSERT INTO `message` VALUES (2, '取件提醒', '您的快递已入库，请及时取件。\n\n快递单号：DBG1770346255956103\n取件码：1-1-MVFB6K\n货架位置：货架1 - 第1层\n\n请携带有效证件到快递驿站取件。', '取件提醒', 'employee', 1, 'user', 1, 1, '已读', '2026-02-09 03:00:23', '2026-02-09 03:00:39');
INSERT INTO `message` VALUES (3, '取件提醒', '您的快递已入库，请及时取件。\n\n快递单号：DBG1770346256024238\n取件码：1-1-S6ZVGV\n货架位置：货架1 - 第1层\n\n请携带有效证件到快递驿站取件。', '取件提醒', 'employee', 1, 'user', 1, 1, '已读', '2026-02-09 03:00:24', '2026-02-09 03:10:26');
INSERT INTO `message` VALUES (4, '取件提醒', '您的快递已入库，请及时取件。\n\n快递单号：DBG1770346256273468\n取件码：1-1-H8HS5U\n货架位置：货架1 - 第1层\n\n请携带有效证件到快递驿站取件。', '取件提醒', 'employee', 1, 'user', 1, 1, '已读', '2026-02-09 03:00:25', '2026-02-09 03:10:26');
INSERT INTO `message` VALUES (5, '取件提醒', '您的快递已入库，请及时取件。\n\n快递单号：DBG1770346256340302\n取件码：1-1-SYTNEX\n货架位置：货架1 - 第1层\n\n请携带有效证件到快递驿站取件。', '取件提醒', 'employee', 1, 'user', 1, 1, '已读', '2026-02-09 03:00:25', '2026-02-09 03:10:26');
INSERT INTO `message` VALUES (6, '取件提醒', '您的快递已入库，请及时取件。\n\n快递单号：DBG1770346004745971\n取件码：1-1-ENFSQU\n货架位置：货架1 - 第1层\n\n请携带有效证件到快递驿站取件。', '取件提醒', 'employee', 1, 'user', 1, 1, '已读', '2026-02-09 03:00:25', '2026-02-09 03:10:26');
INSERT INTO `message` VALUES (7, '年终福利', '现在寄件有优惠，详情到店咨询', '促销信息', 'employee', 1, 'user', 1, NULL, '已读', '2026-02-09 03:10:11', '2026-02-09 03:10:26');
INSERT INTO `message` VALUES (8, '年终福利', '现在寄件有优惠，详情到店咨询', '促销信息', 'employee', 1, 'user', 2, NULL, '未读', '2026-02-09 03:10:11', NULL);
INSERT INTO `message` VALUES (9, '年终福利', '现在寄件有优惠，详情到店咨询', '促销信息', 'employee', 1, 'user', 3, NULL, '未读', '2026-02-09 03:10:11', NULL);
INSERT INTO `message` VALUES (10, '年终福利', '现在寄件有优惠，详情到店咨询', '促销信息', 'employee', 1, 'user', 4, NULL, '未读', '2026-02-09 03:10:11', NULL);
INSERT INTO `message` VALUES (11, '年终福利', '现在寄件有优惠，详情到店咨询', '促销信息', 'employee', 1, 'user', 5, NULL, '未读', '2026-02-09 03:10:11', NULL);

-- ----------------------------
-- Table structure for package
-- ----------------------------
DROP TABLE IF EXISTS `package`;
CREATE TABLE `package`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '包裹ID',
  `tracking_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '快递单号',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '寄件人姓名',
  `sender_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '寄件人电话',
  `sender_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '寄件人地址',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收件人电话',
  `receiver_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收件人地址',
  `package_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '包裹类型',
  `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '重量(kg)',
  `size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '尺寸',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '待入库' COMMENT '状态(待入库、入库中、已入库、待取件、已取件、异常)',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `shelf_id` bigint(0) NULL DEFAULT NULL COMMENT '货架ID',
  `shelf_layer` int(0) NULL DEFAULT NULL,
  `pickup_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '取件码(货架号-层号-随机码)',
  `entry_employee_id` bigint(0) NULL DEFAULT NULL COMMENT '入库员工ID',
  `entry_time` datetime(0) NULL DEFAULT NULL COMMENT '入库时间',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '所属用户ID',
  `pickup_deadline` datetime(0) NULL DEFAULT NULL COMMENT '取件截止时间',
  `delivery_employee_id` bigint(0) NULL DEFAULT NULL COMMENT '派送员工ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tracking_number`(`tracking_number`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  INDEX `shelf_id`(`shelf_id`) USING BTREE,
  INDEX `entry_employee_id`(`entry_employee_id`) USING BTREE,
  INDEX `idx_package_tracking`(`tracking_number`) USING BTREE,
  INDEX `idx_package_status`(`status`) USING BTREE,
  INDEX `idx_package_user`(`user_id`) USING BTREE,
  INDEX `delivery_employee_id`(`delivery_employee_id`) USING BTREE,
  CONSTRAINT `package_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_2` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_3` FOREIGN KEY (`entry_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_5` FOREIGN KEY (`delivery_employee_id`) REFERENCES `employee` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '快递包裹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of package
-- ----------------------------
INSERT INTO `package` VALUES (3, 'GPOT1768630138407132', '谭浩毅', '19826151596', '测试3', '韩立', '18862666020', '测试4', '食品', 2.00, '30x20x10', '已入库', 1, 1, NULL, NULL, 1, '2026-01-21 05:04:19', 1, NULL, NULL, '2026-01-21 05:04:19', NULL);
INSERT INTO `package` VALUES (4, 'GPOT1769499626646342', '谭浩毅', '19826151596', 'aaa', '韩立', '18862666020', 'bbb', '食品', 2.00, '30x20x50', '待取件', 1, 1, NULL, NULL, 2, '2026-01-28 02:08:57', 4, NULL, 1, '2026-01-28 02:08:57', '2026-01-28 03:23:22');
INSERT INTO `package` VALUES (5, 'GPOT1769657202822876', '谭浩毅', '19826151596', 'BBBD', '韩立', '18862666020', 'FGFF', '文件', 2.00, '30x20x50', '运输中', 1, 1, NULL, NULL, 2, '2026-01-29 03:30:50', 1, NULL, 1, '2026-01-29 03:30:50', '2026-01-29 03:31:03');
INSERT INTO `package` VALUES (6, 'GPOT1769662127764321', '谭浩毅', '19826151596', '测试1sss', '韩立', '18862666020', '测试2dddd', '文件', 1.00, '1x1x1', '已入库', 1, 1, NULL, NULL, 2, '2026-01-29 05:45:55', 1, NULL, NULL, '2026-01-29 05:45:55', NULL);
INSERT INTO `package` VALUES (7, 'GPOT1769666268224384', '谭浩毅', '19826151596', '2026年1月29日13:57:38', '韩立', '18862666020', '2026年1月29日13:57:42', '文件', 1.00, '1x1x1', '已入库', 1, 1, NULL, NULL, 2, '2026-01-29 05:58:21', 1, NULL, NULL, '2026-01-29 05:58:21', NULL);
INSERT INTO `package` VALUES (8, 'GPOT1769666358160585', '谭浩毅', '19826151596', '2026年1月29日13:59:13', '韩立', '18862666020', '2026年1月29日13:59:14', '服装', 1.00, '1x1x1', '已入库', 1, 1, NULL, NULL, 2, '2026-01-29 05:59:34', 1, NULL, NULL, '2026-01-29 05:59:34', NULL);
INSERT INTO `package` VALUES (9, 'GPOT1769666680078987', '谭浩毅', '19826151596', '2026年1月29日14:04:36', '韩立', '18862666020', '2026年1月29日14:04:37', '服装', 1.00, '1x1x1', '已入库', 1, 1, NULL, NULL, 2, '2026-01-29 06:04:55', 1, NULL, NULL, '2026-01-29 06:04:55', NULL);
INSERT INTO `package` VALUES (10, 'GPOT1769666880182662', '谭浩毅', '19826151596', '2026年1月29日14:07:55', '韩立', '18862666020', '2026年1月29日14:07:57', '其他', 1.00, '1x1x1', '已入库', 1, 1, 1, NULL, 2, '2026-01-29 06:08:16', 1, NULL, NULL, '2026-01-29 06:08:16', NULL);
INSERT INTO `package` VALUES (11, 'GPOT1769668646483752', '谭浩毅', '19826151596', '2026年1月29日14:37:20', '韩立', '18862666020', '2026年1月29日14:37:23', '文件', 1.00, '1x1x1', '已入库', 1, 1, 1, '1-1-WLJEYX', 2, '2026-01-29 06:37:44', 2, NULL, NULL, '2026-01-29 06:37:44', NULL);
INSERT INTO `package` VALUES (12, 'GPOT1769749012164783', '谭浩毅', '19826151596', 'CS1', '韩立', '18862666020', 'CS2', '文件', 1.00, '1x1x1', '已入库', 1, 1, 1, '1-1-NQHA2P', 2, '2026-01-30 05:00:39', 5, NULL, NULL, '2026-01-30 05:00:39', NULL);
INSERT INTO `package` VALUES (13, 'DBG1770294756323161', '韩立', '19826151596', '测试1', '谭浩毅', '19826151596', '测试1', '文件', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-Y7MJ3V', 1, '2026-02-05 14:07:37', 1, NULL, NULL, '2026-02-05 12:32:37', '2026-02-06 10:41:40');
INSERT INTO `package` VALUES (14, 'DBG1770345143004665', '韩立', '19826151596', '测试122211', '谭浩毅', '19826151596', '测试1', '文件', 1.00, '30x20x10', '异常', NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2026-02-06 02:32:23', '2026-02-06 10:41:41');
INSERT INTO `package` VALUES (15, 'DBG1770346004745971', '韩立', '19826151596', '测试122211', '谭浩毅', '19826151596', '测试1', '文件', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-ENFSQU', 1, '2026-02-09 03:00:25', 1, NULL, NULL, '2026-02-06 02:46:45', '2026-02-09 03:00:25');
INSERT INTO `package` VALUES (16, 'DBG1770346255706571', '韩立', '19826151596', '测试122211_1770346255400', '谭浩毅', '19826151596', '测试1_1770346255400', '服装', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-JUKTCG', 1, '2026-02-09 03:00:23', 1, NULL, NULL, '2026-02-06 02:50:56', '2026-02-09 03:00:23');
INSERT INTO `package` VALUES (17, 'DBG1770346255956103', '韩立', '19826151596', '测试122211_1770346255718', '谭浩毅', '19826151596', '测试1_1770346255718', '服装', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-MVFB6K', 1, '2026-02-09 03:00:23', 1, NULL, NULL, '2026-02-06 02:50:56', '2026-02-09 03:00:23');
INSERT INTO `package` VALUES (18, 'DBG1770346256024238', '韩立', '19826151596', '测试122211_1770346255970', '谭浩毅', '19826151596', '测试1_1770346255970', '服装', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-S6ZVGV', 1, '2026-02-09 03:00:24', 1, NULL, NULL, '2026-02-06 02:50:56', '2026-02-09 03:00:24');
INSERT INTO `package` VALUES (19, 'DBG1770346256273468', '韩立', '19826151596', '测试122211_1770346256039', '谭浩毅', '19826151596', '测试1_1770346256039', '服装', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-H8HS5U', 1, '2026-02-09 03:00:25', 1, NULL, NULL, '2026-02-06 02:50:56', '2026-02-09 03:00:25');
INSERT INTO `package` VALUES (20, 'DBG1770346256340302', '韩立', '19826151596', '测试122211_1770346256288', '谭浩毅', '19826151596', '测试1_1770346256288', '服装', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-SYTNEX', 1, '2026-02-09 03:00:25', 1, NULL, NULL, '2026-02-06 02:50:56', '2026-02-09 03:00:25');
INSERT INTO `package` VALUES (21, 'DBG1770348015722940', '韩立', '19826151596', '测试2026年2月6日11:20:03_1770348015399', '谭浩毅', '19826151596', '测试12026年2月6日11:20:07_1770348015399', '数码产品', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-NB8ZZZ', 1, '2026-02-06 03:21:12', 1, NULL, NULL, '2026-02-06 03:20:16', '2026-02-06 03:21:12');
INSERT INTO `package` VALUES (22, 'DBG1770348015957599', '韩立', '19826151596', '测试2026年2月6日11:20:03_1770348015789', '谭浩毅', '19826151596', '测试12026年2月6日11:20:07_1770348015789', '数码产品', 1.00, '30x20x10', '已取件', 1, 1, 1, '1-1-RMUZYE', 1, '2026-02-06 03:21:36', 1, NULL, NULL, '2026-02-06 03:20:16', '2026-02-09 02:38:22');
INSERT INTO `package` VALUES (23, 'DBG1770348016093856', '韩立', '19826151596', '测试2026年2月6日11:20:03_1770348015969', '谭浩毅', '19826151596', '测试12026年2月6日11:20:07_1770348015969', '数码产品', 1.00, '30x20x10', '异常', NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2026-02-06 03:20:16', '2026-02-06 03:22:05');
INSERT INTO `package` VALUES (24, 'DBG1770348016272964', '韩立', '19826151596', '测试2026年2月6日11:20:03_1770348016106', '谭浩毅', '19826151596', '测试12026年2月6日11:20:07_1770348016106', '数码产品', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-XPKE5R', 1, '2026-02-06 03:22:17', 1, NULL, NULL, '2026-02-06 03:20:16', '2026-02-06 03:22:17');
INSERT INTO `package` VALUES (25, 'DBG1770348016411633', '韩立', '19826151596', '测试2026年2月6日11:20:03_1770348016284', '谭浩毅', '19826151596', '测试12026年2月6日11:20:07_1770348016284', '数码产品', 1.00, '30x20x10', '已入库', 1, 1, 1, '1-1-76QLGR', 1, '2026-02-06 03:22:17', 1, NULL, NULL, '2026-02-06 03:20:16', '2026-02-06 03:22:17');

-- ----------------------------
-- Table structure for package_entry
-- ----------------------------
DROP TABLE IF EXISTS `package_entry`;
CREATE TABLE `package_entry`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '入库记录ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `employee_id` bigint(0) NOT NULL COMMENT '操作员工ID',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `shelf_id` bigint(0) NOT NULL COMMENT '货架ID',
  `shelf_layer` int(0) NULL DEFAULT NULL COMMENT '货架层数(1-4)',
  `entry_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '入库方式(扫码录入,自动分拣)',
  `entry_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  INDEX `employee_id`(`employee_id`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  INDEX `shelf_id`(`shelf_id`) USING BTREE,
  CONSTRAINT `package_entry_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_entry_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_entry_ibfk_3` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_entry_ibfk_4` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '入库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of package_entry
-- ----------------------------
INSERT INTO `package_entry` VALUES (1, 3, 1, 1, 1, NULL, '扫码录入', '2026-01-21 05:04:20', '核验成功后自动入库', '2026-01-21 05:04:20');
INSERT INTO `package_entry` VALUES (2, 4, 2, 1, 1, NULL, '扫码录入', '2026-01-28 02:08:57', '核验成功后自动入库', '2026-01-28 02:08:57');
INSERT INTO `package_entry` VALUES (3, 5, 2, 1, 1, NULL, '扫码录入', '2026-01-29 03:30:50', '核验成功后自动入库', '2026-01-29 03:30:50');
INSERT INTO `package_entry` VALUES (4, 6, 2, 1, 1, NULL, '扫码录入', '2026-01-29 05:45:55', '核验成功后自动入库', '2026-01-29 05:45:55');
INSERT INTO `package_entry` VALUES (5, 7, 2, 1, 1, NULL, '扫码录入', '2026-01-29 05:58:21', '核验成功后自动入库', '2026-01-29 05:58:21');
INSERT INTO `package_entry` VALUES (6, 8, 2, 1, 1, NULL, '扫码录入', '2026-01-29 05:59:34', '核验成功后自动入库', '2026-01-29 05:59:34');
INSERT INTO `package_entry` VALUES (7, 9, 2, 1, 1, NULL, '扫码录入', '2026-01-29 06:04:55', '核验成功后自动入库', '2026-01-29 06:04:55');
INSERT INTO `package_entry` VALUES (8, 10, 2, 1, 1, 1, '扫码录入', '2026-01-29 06:08:16', '核验成功后自动入库', '2026-01-29 06:08:16');
INSERT INTO `package_entry` VALUES (9, 11, 2, 1, 1, 1, '扫码录入', '2026-01-29 06:37:44', '核验成功后自动入库', '2026-01-29 06:37:44');
INSERT INTO `package_entry` VALUES (10, 12, 2, 1, 1, 1, '扫码录入', '2026-01-30 05:00:39', '核验成功后自动入库', '2026-01-30 05:00:39');
INSERT INTO `package_entry` VALUES (11, 13, 1, 1, 1, 1, '扫码录入', '2026-02-05 14:07:37', '正式包裹入库（待入库 -> 已入库）', '2026-02-05 14:07:37');
INSERT INTO `package_entry` VALUES (12, 21, 1, 1, 1, 1, '扫码录入', '2026-02-06 03:21:12', '正式包裹入库（待入库 -> 已入库）', '2026-02-06 03:21:12');
INSERT INTO `package_entry` VALUES (13, 22, 1, 1, 1, 1, '扫码录入', '2026-02-06 03:21:36', '正式包裹入库（待入库 -> 已入库）', '2026-02-06 03:21:36');
INSERT INTO `package_entry` VALUES (14, 24, 1, 1, 1, 1, '扫码录入', '2026-02-06 03:22:17', '正式包裹入库（待入库 -> 已入库）', '2026-02-06 03:22:17');
INSERT INTO `package_entry` VALUES (15, 25, 1, 1, 1, 1, '扫码录入', '2026-02-06 03:22:17', '正式包裹入库（待入库 -> 已入库）', '2026-02-06 03:22:17');
INSERT INTO `package_entry` VALUES (16, 16, 1, 1, 1, 1, '扫码录入', '2026-02-09 03:00:23', '正式包裹入库（待入库 -> 已入库）', '2026-02-09 03:00:23');
INSERT INTO `package_entry` VALUES (17, 17, 1, 1, 1, 1, '扫码录入', '2026-02-09 03:00:23', '正式包裹入库（待入库 -> 已入库）', '2026-02-09 03:00:23');
INSERT INTO `package_entry` VALUES (18, 18, 1, 1, 1, 1, '扫码录入', '2026-02-09 03:00:24', '正式包裹入库（待入库 -> 已入库）', '2026-02-09 03:00:24');
INSERT INTO `package_entry` VALUES (19, 19, 1, 1, 1, 1, '扫码录入', '2026-02-09 03:00:25', '正式包裹入库（待入库 -> 已入库）', '2026-02-09 03:00:25');
INSERT INTO `package_entry` VALUES (20, 20, 1, 1, 1, 1, '扫码录入', '2026-02-09 03:00:25', '正式包裹入库（待入库 -> 已入库）', '2026-02-09 03:00:25');
INSERT INTO `package_entry` VALUES (21, 15, 1, 1, 1, 1, '扫码录入', '2026-02-09 03:00:25', '正式包裹入库（待入库 -> 已入库）', '2026-02-09 03:00:25');

-- ----------------------------
-- Table structure for package_outbound
-- ----------------------------
DROP TABLE IF EXISTS `package_outbound`;
CREATE TABLE `package_outbound`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '出库记录ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `outbound_employee_id` bigint(0) NOT NULL COMMENT '出库员工ID',
  `delivery_employee_id` bigint(0) NOT NULL COMMENT '派送员工ID',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `outbound_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
  `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  INDEX `outbound_employee_id`(`outbound_employee_id`) USING BTREE,
  INDEX `delivery_employee_id`(`delivery_employee_id`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `package_outbound_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_outbound_ibfk_2` FOREIGN KEY (`outbound_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_outbound_ibfk_3` FOREIGN KEY (`delivery_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_outbound_ibfk_4` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '出库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of package_outbound
-- ----------------------------
INSERT INTO `package_outbound` VALUES (1, 5, 2, 1, NULL, '2026-01-29 03:31:03', NULL, NULL);


-- ----------------------------
-- Table structure for shelf
-- ----------------------------
DROP TABLE IF EXISTS `shelf`;
CREATE TABLE `shelf`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '货架ID',
  `shelf_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '货架编码',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `shelf_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '货架类型',
  `capacity` int(0) NULL DEFAULT 0 COMMENT '容量',
  `current_count` int(0) NULL DEFAULT 0 COMMENT '当前数量',
  `status` tinyint(0) NULL DEFAULT 1,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `shelf_code`(`shelf_code`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `shelf_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '货架表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shelf
-- ----------------------------
INSERT INTO `shelf` VALUES (1, 'SHELF001', 1, '普通货架', 40, 0, 1, '2026-01-21 13:04:08', '2026-01-29 13:43:50');
INSERT INTO `shelf` VALUES (2, 'SHELF002', 1, '普通货架', 40, 0, 1, '2026-01-29 13:20:51', '2026-01-29 13:43:51');
INSERT INTO `shelf` VALUES (3, 'SHELF003', 1, '普通货架', 40, 0, 1, '2026-01-29 13:20:58', '2026-01-29 13:43:52');
INSERT INTO `shelf` VALUES (4, 'SHELF004', 1, '大货架', 20, 0, 1, '2026-01-29 13:21:15', '2026-01-29 13:43:55');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '身份证号',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'thydada', '12345', '谭浩毅', NULL, NULL, NULL, 1, NULL, NULL);
INSERT INTO `user` VALUES (2, 'thydada2', '12345', '韩立', NULL, NULL, NULL, 1, '2026-01-14 02:28:31', NULL);
INSERT INTO `user` VALUES (3, 'thydada3', '12345', '张三', NULL, NULL, NULL, 1, '2026-01-15 05:06:56', NULL);
INSERT INTO `user` VALUES (4, 'thydada4', '123', '微软', NULL, NULL, NULL, 1, '2026-01-27 07:39:58', NULL);
INSERT INTO `user` VALUES (5, 'thydada5', '123', '是的', NULL, NULL, NULL, 1, '2026-01-30 04:56:13', NULL);

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '仓库名称',
  `warehouse_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '仓库代码',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地址',
  `manager_id` bigint(0) NULL DEFAULT NULL COMMENT '管理员ID',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `warehouse_code`(`warehouse_code`) USING BTREE,
  INDEX `manager_id`(`manager_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '仓库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES (1, '仓库1', 'WH001', 'AAA', NULL, 1, '2026-01-21 13:04:08', '2026-01-29 11:21:15');

SET FOREIGN_KEY_CHECKS = 1;
