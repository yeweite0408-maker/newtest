#!/bin/bash
# GameMall 游戏商城 - 一键启动 (Git Bash / Linux / macOS)

echo "========================================"
echo "  GameMall 游戏商城 - 一键启动"
echo "========================================"
echo ""

# 检测 Java
if ! command -v java &>/dev/null; then
    echo "[错误] 未检测到 Java，请安装 JDK 17+"
    exit 1
fi

# 检测 Node.js
if ! command -v node &>/dev/null; then
    echo "[错误] 未检测到 Node.js，请安装 Node.js 18+"
    exit 1
fi

DIR="$(cd "$(dirname "$0")" && pwd)"

# 启动后端
echo "[1/3] 启动后端服务 (端口 8080)..."
(cd "$DIR/backend" && ./mvnw spring-boot:run) &
BACKEND_PID=$!
echo "后端进程 PID: $BACKEND_PID"

# 等待后端启动
echo "等待后端启动..."
sleep 15

# 安装前端依赖
echo "[2/3] 检查前端依赖..."
if [ ! -d "$DIR/frontend/node_modules" ]; then
    echo "首次运行，安装前端依赖..."
    cd "$DIR/frontend" && npm install
fi

# 启动前端
echo "[3/3] 启动前端服务 (端口 3000)..."
(cd "$DIR/frontend" && npm run dev) &
FRONTEND_PID=$!

echo ""
echo "========================================"
echo "  启动完成！"
echo ""
echo "  前端地址: http://localhost:3000"
echo "  后端地址: http://localhost:8080"
echo "  H2控制台: http://localhost:8080/h2-console"
echo ""
echo "  管理员账号: admin / admin"
echo "  测试账号:   test / 123456"
echo ""
echo "  按 Ctrl+C 停止所有服务"
echo "========================================"

trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; echo '服务已停止'; exit" SIGINT SIGTERM
wait
