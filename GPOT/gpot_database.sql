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

 Date: 21/01/2026 18:15:24
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
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'thydada', '12345', NULL, NULL, NULL, 1, '2026-01-12 22:34:37', '2026-01-12 22:34:37');

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
  `view_count` int(0) NULL DEFAULT 0 COMMENT '查看次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id`) USING BTREE,
  CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
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
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, 'thydada', '1234', '谭浩毅', '18862666523', '3053596379@qq.com', 'A', '江苏苏州', 1, '2026-01-19 16:53:50', '2026-01-19 16:53:50');
INSERT INTO `employee` VALUES (2, 'thydada2', '12345', '韩立', '18114523658', '525752231@qq.com', 'B', '江苏徐州', 1, '2026-01-20 16:53:50', '2026-01-20 16:53:50');

-- ----------------------------
-- Table structure for exception_package
-- ----------------------------
DROP TABLE IF EXISTS `exception_package`;
CREATE TABLE `exception_package`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '异常件ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `exception_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '异常类型',
  `exception_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异常原因',
  `report_employee_id` bigint(0) NOT NULL COMMENT '报告员工ID',
  `handle_employee_id` bigint(0) NULL DEFAULT NULL COMMENT '处理员工ID',
  `handle_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '待处理' COMMENT '处理状态(待处理,处理中,已处理)',
  `handle_result` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '处理结果',
  `report_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报告时间',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `report_employee_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '报告员工姓名',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '异常来源（pickup:取件异常, verification:核验异常）',
  `tracking_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '快递单号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  INDEX `report_employee_id`(`report_employee_id`) USING BTREE,
  INDEX `handle_employee_id`(`handle_employee_id`) USING BTREE,
  CONSTRAINT `exception_package_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `exception_package_ibfk_2` FOREIGN KEY (`report_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `exception_package_ibfk_3` FOREIGN KEY (`handle_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '异常件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for inventory_check
-- ----------------------------
DROP TABLE IF EXISTS `inventory_check`;
CREATE TABLE `inventory_check`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
  `warehouse_id` bigint(0) NOT NULL COMMENT '仓库ID',
  `employee_id` bigint(0) NOT NULL COMMENT '盘点员工ID',
  `check_date` date NOT NULL COMMENT '盘点日期',
  `total_packages` int(0) NULL DEFAULT 0 COMMENT '应有包裹总数',
  `actual_packages` int(0) NULL DEFAULT 0 COMMENT '实际包裹总数',
  `discrepancies` int(0) NULL DEFAULT 0 COMMENT '差异数量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '进行中' COMMENT '状态(进行中,已完成)',
  `check_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '盘点结果',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  INDEX `employee_id`(`employee_id`) USING BTREE,
  CONSTRAINT `inventory_check_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `inventory_check_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '库存盘点表' ROW_FORMAT = Dynamic;

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
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '未读' COMMENT '状态(未读,已读,已删除)',
  `send_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id`) USING BTREE,
  INDEX `idx_message_receiver`(`receiver_id`, `receiver_type`) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for operation_stats
-- ----------------------------
DROP TABLE IF EXISTS `operation_stats`;
CREATE TABLE `operation_stats`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stats_date` date NOT NULL COMMENT '统计日期',
  `warehouse_id` bigint(0) NULL DEFAULT NULL COMMENT '仓库ID',
  `total_packages` int(0) NULL DEFAULT 0 COMMENT '总包裹数',
  `entry_packages` int(0) NULL DEFAULT 0 COMMENT '入库包裹数',
  `pickup_packages` int(0) NULL DEFAULT 0 COMMENT '取件包裹数',
  `exception_packages` int(0) NULL DEFAULT 0 COMMENT '异常包裹数',
  `overdue_packages` int(0) NULL DEFAULT 0 COMMENT '逾期包裹数',
  `user_registrations` int(0) NULL DEFAULT 0 COMMENT '用户注册数',
  `revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '营收',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stats_date_warehouse`(`stats_date`, `warehouse_id`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  INDEX `idx_operation_stats_date`(`stats_date`) USING BTREE,
  CONSTRAINT `operation_stats_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '运营统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for overdue_package
-- ----------------------------
DROP TABLE IF EXISTS `overdue_package`;
CREATE TABLE `overdue_package`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '逾期件ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `overdue_days` int(0) NULL DEFAULT 0 COMMENT '逾期天数',
  `reminder_count` int(0) NULL DEFAULT 0 COMMENT '提醒次数',
  `last_reminder_time` datetime(0) NULL DEFAULT NULL COMMENT '最后提醒时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '逾期中' COMMENT '状态(逾期中,已提醒,已取走)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  CONSTRAINT `overdue_package_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '逾期件表' ROW_FORMAT = Dynamic;

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
  `entry_employee_id` bigint(0) NULL DEFAULT NULL COMMENT '入库员工ID',
  `entry_time` datetime(0) NULL DEFAULT NULL COMMENT '入库时间',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '所属用户ID',
  `pickup_deadline` datetime(0) NULL DEFAULT NULL COMMENT '取件截止时间',
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
  CONSTRAINT `package_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_2` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_3` FOREIGN KEY (`entry_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '快递包裹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of package
-- ----------------------------
INSERT INTO `package` VALUES (3, 'GPOT1768630138407132', '谭浩毅', '19826151596', '测试3', '韩立', '18862666020', '测试4', '食品', 2.00, '30x20x10', '已入库', 1, 1, 1, '2026-01-21 05:04:19', 1, NULL, '2026-01-21 05:04:19', NULL);

-- ----------------------------
-- Table structure for package_entry
-- ----------------------------
DROP TABLE IF EXISTS `package_entry`;
CREATE TABLE `package_entry`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '入库记录ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `employee_id` bigint(0) NOT NULL COMMENT '操作员工ID',
  `warehouse_id` bigint(0) NOT NULL COMMENT '仓库ID',
  `shelf_id` bigint(0) NOT NULL COMMENT '货架ID',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '入库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of package_entry
-- ----------------------------
INSERT INTO `package_entry` VALUES (1, 3, 1, 1, 1, '扫码录入', '2026-01-21 05:04:20', '核验成功后自动入库', '2026-01-21 05:04:20');

-- ----------------------------
-- Table structure for package_temp
-- ----------------------------
DROP TABLE IF EXISTS `package_temp`;
CREATE TABLE `package_temp`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '临时包裹ID',
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
  `entry_employee_id` bigint(0) NULL DEFAULT NULL COMMENT '入库员工ID',
  `entry_time` datetime(0) NULL DEFAULT NULL COMMENT '入库时间',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '所属用户ID',
  `pickup_deadline` datetime(0) NULL DEFAULT NULL COMMENT '取件截止时间',
  `pickup_success` tinyint(0) NULL DEFAULT 0,
  `verification_success` tinyint(0) NULL DEFAULT 0,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tracking_number`(`tracking_number`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  INDEX `shelf_id`(`shelf_id`) USING BTREE,
  INDEX `entry_employee_id`(`entry_employee_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `package_temp_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_temp_ibfk_2` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_temp_ibfk_3` FOREIGN KEY (`entry_employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `package_temp_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '临时包裹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of package_temp
-- ----------------------------
INSERT INTO `package_temp` VALUES (2, 'GPOT1768974623161772', '谭浩毅', '19826151596', '测试5555', '韩立', '18862666020', '测试6666', '文件', 2.00, '30x20x50', '待入库', NULL, NULL, NULL, NULL, 1, NULL, 0, 0, '2026-01-21 05:50:23', NULL);

-- ----------------------------
-- Table structure for pickup_code
-- ----------------------------
DROP TABLE IF EXISTS `pickup_code`;
CREATE TABLE `pickup_code`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '取件码ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '取件码',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:有效,0:已使用,2:过期)',
  `expire_time` datetime(0) NOT NULL COMMENT '过期时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  INDEX `idx_pickup_code_code`(`code`) USING BTREE,
  CONSTRAINT `pickup_code_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '取件码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pickup_record
-- ----------------------------
DROP TABLE IF EXISTS `pickup_record`;
CREATE TABLE `pickup_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '取件记录ID',
  `package_id` bigint(0) NOT NULL COMMENT '包裹ID',
  `user_id` bigint(0) NOT NULL COMMENT '取件用户ID',
  `pickup_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '取件码',
  `pickup_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '取件方式',
  `pickup_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '取件时间',
  `employee_id` bigint(0) NULL DEFAULT NULL COMMENT '协助员工ID',
  `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `package_id`(`package_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `employee_id`(`employee_id`) USING BTREE,
  CONSTRAINT `pickup_record_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `package` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `pickup_record_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `pickup_record_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '取件记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for shelf
-- ----------------------------
DROP TABLE IF EXISTS `shelf`;
CREATE TABLE `shelf`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '货架ID',
  `shelf_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '货架编码',
  `warehouse_id` bigint(0) NOT NULL COMMENT '仓库ID',
  `shelf_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '货架类型',
  `capacity` int(0) NULL DEFAULT 0 COMMENT '容量',
  `current_count` int(0) NULL DEFAULT 0 COMMENT '当前数量',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `shelf_code`(`shelf_code`) USING BTREE,
  INDEX `warehouse_id`(`warehouse_id`) USING BTREE,
  CONSTRAINT `shelf_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '货架表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shelf
-- ----------------------------
INSERT INTO `shelf` VALUES (1, 'SHELF001', 1, '普通货架', 100, 0, 1, '2026-01-21 13:04:08', '2026-01-21 13:04:08');

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'thydada', '12345', '谭浩毅', NULL, NULL, NULL, 1, NULL, NULL);
INSERT INTO `user` VALUES (2, 'thydada2', '12345', '韩立', NULL, NULL, NULL, 1, '2026-01-14 02:28:31', NULL);
INSERT INTO `user` VALUES (3, 'thydada3', '12345', '张三', NULL, NULL, NULL, 1, '2026-01-15 05:06:56', NULL);

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
  INDEX `manager_id`(`manager_id`) USING BTREE,
  CONSTRAINT `warehouse_ibfk_1` FOREIGN KEY (`manager_id`) REFERENCES `admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '仓库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES (1, '默认仓库', 'WH001', '默认地址', NULL, 1, '2026-01-21 13:04:08', '2026-01-21 13:04:08');

SET FOREIGN_KEY_CHECKS = 1;
