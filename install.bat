@echo off
echo ===========================
echo 1. 初始化数据库
echo ===========================
call init-database.bat

echo.
echo ===========================
echo 2. 启动后端服务
echo ===========================
start cmd /k call start-backend.bat

echo.
echo ===========================
echo 3. 启动前端服务
echo ===========================
start cmd /k call start-frontend.bat

echo.
echo ===========================
echo 所有服务已启动！
echo ===========================
pause