# 临时包裹表功能说明

## 概述
根据新的业务逻辑，用户寄包裹时，包裹信息将先保存到临时包裹表（`package_temp`），而不是直接保存到正式的包裹表（`package`）。临时表增加了两个状态字段，用于跟踪包裹的取件和核验状态。

## 数据库变更

### 新增表：package_temp
- 结构与 `package` 表完全相同
- 新增字段：
  - `pickup_success` TINYINT DEFAULT 0：是否取件成功（0:未取件, 1:取件成功）
  - `verification_success` TINYINT DEFAULT 0：是否核验成功（0:未核验, 1:核验成功）

### 创建临时表的SQL脚本
提供了独立的SQL脚本文件：`create_package_temp_table.sql`，可用于手动创建临时表。

## 代码变更

### 新增文件
1. `PackageTemp.java` - 临时包裹实体类
2. `PackageTempRepository.java` - 临时包裹数据访问层

### 修改文件
1. `gpot_database.sql` - 添加临时表结构和索引
2. `PackageService.java` - 修改寄件逻辑，新增临时表相关方法

## 业务流程变更

### 原流程
用户填写包裹信息 → 直接保存到 `package` 表

### 新流程
用户填写包裹信息 → 保存到 `package_temp` 表 → 等待取件和核验 → 取件和核验都成功后 → 转移到 `package` 表

## API变更

### PackageService 新增方法
- `getTempPackagesByUserId(Long userId)` - 根据用户ID查询临时包裹
- `getTempPackageByTrackingNumber(String trackingNumber)` - 根据快递单号查询临时包裹
- `transferTempPackageToFormal(Long tempPackageId)` - 将临时包裹转移到正式表
- `updatePickupSuccess(Long tempPackageId, boolean success)` - 更新取件状态
- `updateVerificationSuccess(Long tempPackageId, boolean success)` - 更新核验状态

### 修改方法
- `sendPackage(SendPackageRequest request)` - 现在保存到临时表而不是正式表
- `generateTrackingNumber()` - 现在同时检查临时表和正式表的快递单号唯一性

## 使用说明

### 1. 创建临时表
运行以下SQL脚本之一：
- 在 `gpot_database.sql` 中执行临时表创建部分
- 或单独执行 `create_package_temp_table.sql` 脚本

### 2. 业务流程
1. 用户寄件时，调用 `sendPackage()` 方法，数据保存到临时表
2. 快递员取件后，调用 `updatePickupSuccess()` 更新取件状态
3. 系统核验后，调用 `updateVerificationSuccess()` 更新核验状态
4. 当取件和核验都成功时，调用 `transferTempPackageToFormal()` 将数据转移到正式表

### 3. 查询临时包裹
- 使用 `getTempPackagesByUserId()` 查询用户的临时包裹
- 使用 `getTempPackageByTrackingNumber()` 根据快递单号查询

## 注意事项
1. 临时表的快递单号必须在临时表和正式表中都唯一
2. 只有当 `pickup_success = 1` 且 `verification_success = 1` 时，才能将临时包裹转移到正式表
3. 转移成功后，临时表中的对应记录将被删除