@echo off
chcp 65001 > nul
echo ========================================
echo    GPOT 前端启动脚本 (绝对路径版本)
echo ========================================
echo.

REM 使用绝对路径 - 请根据实际情况修改
set FRONTEND_DIR=C:\Users\PC\Desktop\Graduation Project of Tan\GPOT-F

echo 前端目录: %FRONTEND_DIR%
cd /d "%FRONTEND_DIR%"

if not exist package.json (
    echo ❌ 未找到前端项目，请检查路径是否正确
    echo 预期路径: %FRONTEND_DIR%
    dir
    pause
    exit /b 1
)

echo ✅ 找到前端项目
echo 检查前端项目依赖...
if not exist node_modules (
    echo 安装前端依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo ❌ npm install 失败
        pause
        exit /b 1
    )
) else (
    echo ✅ 依赖已存在，跳过安装步骤
)

echo.
echo 启动前端开发服务器...
echo 前端地址: http://localhost:5173
echo 后端地址: http://localhost:8080
echo 按 Ctrl+C 停止服务器
echo.

call npm run dev

pause