# GPOT快递管理系统 - 登录系统

## 项目简介

这是一个基于Spring Boot + MySQL + Vue.js + Lombok的快递管理系统登录模块，支持管理员、员工和普通用户的登录验证。

## 功能特性

- ✅ 三种用户角色登录（管理员、员工、用户）
- ✅ 用户名密码验证
- ✅ 登录成功页面显示用户信息
- ✅ 响应式设计，美观的用户界面
- ✅ 三角色登录验证

## 技术栈

- **后端**: Spring Boot 3.x
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA
- **模板引擎**: Thymeleaf
- **前端框架**: Vue.js 3.x
- **UI组件库**: Element Plus
- **代码简化**: Lombok
- **构建工具**: Maven

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE gpot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 运行数据库脚本：
```bash
mysql -u root -p gpot < gpot_database.sql
```

### 3. 配置文件

编辑 `src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/gpot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf配置
spring.thymeleaf.cache=false
spring.thymeleaf.suffix=.html
spring.thymeleaf.prefix=classpath:/templates/
```

### 4. 运行项目

```bash
mvn clean compile
mvn spring-boot:run
```

### 5. 访问系统

打开浏览器访问：http://localhost:8080

- **Vue.js版本**（推荐）：现代化的单页应用体验
- **传统版本**：访问 http://localhost:8080/login-traditional 查看Thymeleaf版本

## 测试账号

请在数据库中手动创建以下测试账号（或根据需要创建其他账号）：

**管理员账号示例：**
```sql
INSERT INTO admin (username, password, real_name, phone, email, status) VALUES
('admin', 'admin123', '系统管理员', '13800000000', 'admin@gpot.com', 1);
```

**员工账号示例：**
```sql
INSERT INTO employee (username, password, real_name, phone, email, department, position, status) VALUES
('employee', 'emp123', '张三', '13900000000', 'employee@gpot.com', '快递部', '快递员', 1);
```

**用户账号示例：**
```sql
INSERT INTO user (username, password, real_name, phone, email, id_card, status) VALUES
('user', 'user123', '李四', '13700000000', 'user@gpot.com', '110101199001011234', 1);
```

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员，拥有所有权限 |
| 员工 | employee | emp123 | 快递员，可以管理包裹 |
| 用户 | user | user123 | 普通用户，可以查看快递信息 |

## 项目结构

```
src/
├── main/
│   ├── java/com/example/gpot/
│   │   ├── config/                       # 配置类目录（可选）
│   │   ├── controller/
│   │   │   └── AuthController.java       # 登录控制器（支持REST API）
│   │   ├── dto/
│   │   │   ├── ApiResponse.java          # 通用API响应
│   │   │   ├── LoginRequest.java         # 登录请求DTO
│   │   │   └── LoginResponse.java        # 登录响应DTO
│   │   ├── entity/
│   │   │   ├── Admin.java                # 管理员实体（Lombok简化）
│   │   │   ├── Employee.java             # 员工实体（Lombok简化）
│   │   │   └── User.java                 # 用户实体（Lombok简化）
│   │   ├── repository/
│   │   │   ├── AdminRepository.java      # 管理员仓库
│   │   │   ├── EmployeeRepository.java   # 员工仓库
│   │   │   └── UserRepository.java       # 用户仓库
│   │   ├── service/
│   │   │   └── AuthService.java          # 认证服务
│   │   └── GpotApplication.java          # 主启动类
│   └── resources/
│       ├── static/                       # 静态资源
│       │   ├── css/
│       │   │   └── style.css             # 全局样式
│       │   └── js/
│       │       └── app.js                # 前端工具函数
│       └── templates/
│           ├── index.html                # Vue.js主页面
│           ├── login.html                # 传统登录页面
│           └── login-success.html        # 登录成功页面
│       └── application.properties        # 配置文件
├── test/
└── gpot_database.sql                     # 数据库脚本
```

## 登录流程

1. 用户访问首页 `/`
2. 选择用户类型（管理员/员工/用户）
3. 输入用户名和密码
4. 点击登录按钮
5. 系统验证身份
6. 成功后跳转到成功页面显示用户信息

## 扩展功能

该登录系统为后续功能开发奠定了基础：

- 📦 **快递入库管理**: 扫码录入、自动分拣、货架分配
- 👤 **用户取件管理**: 身份验证、取件码生成、出库记录
- 📊 **库存管理**: 库存盘点、异常件处理、逾期件提醒
- 📈 **数据分析**: 运营统计、用户行为分析、营收报表
- 📱 **消息推送**: 取件提醒、促销信息、系统公告
- 📊 **可视化看板**: 多维度数据展示与导出

## 注意事项

1. 确保MySQL服务正在运行
2. 数据库连接配置正确
3. 手动创建测试账号（见下方测试账号部分）
4. 生产环境请修改数据库密码和相关配置
5. 建议在生产环境中使用HTTPS和密码加密

## 特性说明

### Lombok代码简化

使用Lombok注解大幅简化Java代码：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 自动生成getter、setter、toString、equals、hashCode等方法
}
```

### Vue.js前端特性

- **响应式设计**：支持移动端和桌面端
- **组件化开发**：可复用的Vue组件
- **现代化UI**：使用Element Plus组件库
- **REST API**：前后端分离架构
- **用户体验**：流畅的交互动画和反馈

## 常见问题

### Q: 数据库连接失败
A: 检查MySQL服务是否启动，数据库是否存在，连接配置是否正确。

### Q: 页面无法访问
A: 确保应用已成功启动，端口8080未被占用。

### Q: 登录失败
A: 检查用户名密码是否正确，数据库中是否有对应的用户数据。

### Q: Lombok注解不生效
A: 确保IDE已安装Lombok插件，Maven编译时会自动处理注解。

### Q: Vue.js页面加载慢
A: 检查网络连接，CDN资源可能需要科学上网。

### Q: 如何手动创建测试账号
A: 请参考上面的SQL示例，在数据库中手动插入测试账号数据。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 许可证

本项目仅用于学习和演示目的。