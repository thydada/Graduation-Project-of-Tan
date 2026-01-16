# GPOT 前端项目

这是一个基于 Vue 3 的前端应用，用于 GPOT 快递管理系统。

## 功能特性

- ✅ 用户登录（目前只支持用户类型）
- ✅ 用户注册
- ✅ 登录后显示欢迎页面
- 🔄 管理员和员工登录（预留接口，功能待开发）

## 技术栈

- Vue 3 (Composition API)
- Vue Router 4
- Axios (HTTP客户端)
- Vite (构建工具)

## 快速开始

### 环境要求

- Node.js >= 16
- npm >= 8

### 安装依赖

```bash
npm install
```

### 开发模式运行

```bash
npm run dev
```

项目将在 `http://localhost:5173` 启动。

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
GPOT-F/
├── src/
│   ├── views/           # 页面组件
│   │   ├── LoginView.vue    # 登录页面
│   │   └── WelcomeView.vue  # 欢迎页面
│   ├── router/          # 路由配置
│   ├── services/        # API服务
│   ├── App.vue          # 根组件
│   └── main.js          # 应用入口
├── public/              # 静态资源
├── index.html           # HTML模板
├── vite.config.js       # Vite配置
└── package.json         # 项目配置
```

## API接口

项目通过代理调用后端API：

- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册

## 注意事项

1. 确保后端服务 (GPOT) 正在运行在 `http://localhost:8080`
2. 前端通过 Vite 代理将 `/api` 请求转发到后端
3. 目前只实现了用户类型的登录，管理员和员工登录功能预留

## 开发说明

- 登录页面集成了登录和注册功能
- 注册成功后会显示弹窗，用户需要手动点击按钮返回登录
- 登录成功后跳转到欢迎页面
- 欢迎页面展示了系统的主要功能模块（预览）