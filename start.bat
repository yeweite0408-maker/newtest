@echo off
cd /d "%~dp0"
title GameMall 游戏商城

echo ========================================
echo   GameMall 游戏商城 - 一键启动
echo ========================================
echo.

:: Java 环境配置
set JAVA_HOME=C:\Program Files\Java\jdk-26.0.1
set MAVEN_HOME=%USERPROFILE%\Desktop\apache-maven-3.9.6
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

:: 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Java！
    echo 请安装 JDK 17+
    echo.
    pause
    exit /b 1
)

:: 检查 Maven
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [错误] 未检测到 Maven！
    pause
    exit /b 1
)

:: 检查 Node.js
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Node.js！
    echo 请安装 Node.js 18+
    echo.
    pause
    exit /b 1
)

:: 启动后端
echo [1/2] 启动后端服务...
echo 后端启动需要一些时间（首次需下载依赖），请耐心等待...
echo.
cd backend
start "GameMall-Backend" cmd /c "mvn.cmd spring-boot:run"
cd ..
echo 后端窗口已打开，等待启动完成（约15-30秒）...
timeout /t 12 /nobreak >nul

:: 安装前端依赖
echo [2/2] 启动前端服务...
if not exist "frontend\node_modules" (
    echo 首次运行，安装前端依赖...
    cd frontend
    call npm install
    cd ..
    if %errorlevel% neq 0 (
        echo [错误] 前端依赖安装失败！
        pause
        exit /b 1
    )
)
cd frontend
start "GameMall-Frontend" cmd /c "npm run dev -- --host 0.0.0.0"
cd ..
echo 前端窗口已打开。

echo.
echo ========================================
echo   启动完成！
echo.
echo   前端地址: http://localhost:3000
echo   后端地址: http://localhost:8080
echo.
echo   管理员账号: admin / admin
echo   测试账号:   test / 123456
echo.
echo   按任意键打开浏览器...
echo ========================================
pause >nul

start http://localhost:3000
