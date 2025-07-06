#!/bin/bash

# 1. 初始化数据库
./init-database.sh

echo "==========================="
echo "2. 启动后端服务"
echo "==========================="
# 在新终端窗口启动后端
if command -v gnome-terminal &> /dev/null; then
    gnome-terminal -- bash -c './start-backend.sh; exec bash'
elif command -v konsole &> /dev/null; then
    konsole -e bash -c './start-backend.sh; exec bash' &
elif command -v x-terminal-emulator &> /dev/null; then
    x-terminal-emulator -e './start-backend.sh'
else
    echo "请手动运行 ./start-backend.sh"
fi

echo "==========================="
echo "3. 启动前端服务"
echo "==========================="
# 在新终端窗口启动前端
if command -v gnome-terminal &> /dev/null; then
    gnome-terminal -- bash -c './start-frontend.sh; exec bash'
elif command -v konsole &> /dev/null; then
    konsole -e bash -c './start-frontend.sh; exec bash' &
elif command -v x-terminal-emulator &> /dev/null; then
    x-terminal-emulator -e './start-frontend.sh'
else
    echo "请手动运行 ./start-frontend.sh"
fi

echo "==========================="
echo "所有服务已启动！"
echo "===========================" 