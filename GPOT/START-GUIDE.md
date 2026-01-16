# 🚀 GPOT 项目启动指南

## 快速启动方式

### 方法1：使用启动脚本（推荐）

1. **启动后端**
   ```bash
   cd GPOT
   mvn spring-boot:run
   ```

2. **启动前端**
   - 双击 `GPOT/src/main/java/com/example/gpot/start-frontend-absolute.bat` 文件

### 方法2：手动启动

1. **启动后端**
   ```bash
   cd GPOT
   mvn spring-boot:run
   ```

2. **启动前端**
   ```bash
   cd GPOT-F
   npm install  # 首次运行需要
   npm run dev
   ```

## 📋 启动脚本说明

### `GPOT/src/main/java/com/example/gpot/start-frontend-absolute.bat`
- Windows批处理脚本（绝对路径版本）
- 使用硬编码绝对路径，无需路径计算
- 自动检查并安装依赖
- 启动前端开发服务器

## 🌐 访问地址

- **前端界面**: http://localhost:5173
- **后端API**: http://localhost:8080

## ✅ 启动成功标志

- 后端控制台显示: `Started GpotApplication`
- 前端控制台显示: `Local: http://localhost:5173/`

## ⚠️ 故障排除

1. **端口占用**: 确保8080和5173端口未被其他程序占用
2. **依赖问题**: 删除 `GPOT-F/node_modules` 文件夹重新安装
3. **权限问题**: 以管理员身份运行脚本
4. **路径问题**: 如果启动脚本无法找到前端项目，请编辑 `start-frontend-absolute.bat` 中的 `FRONTEND_DIR` 变量
5. **中文乱码**: 脚本已设置UTF-8编码，如仍有乱码请检查控制台编码设置

## 🎯 使用流程

1. 运行后端：`mvn spring-boot:run`
2. 双击 `GPOT/src/main/java/com/example/gpot/start-frontend-absolute.bat` 启动前端
3. 浏览器访问 http://localhost:5173
4. 开始使用系统！

---

**提示**: 脚本会在首次运行时自动安装依赖，后续运行会跳过此步骤以加快启动速度。如需修改前端路径，请编辑脚本中的 `FRONTEND_DIR` 变量。