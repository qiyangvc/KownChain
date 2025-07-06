@echo off
echo ======================================
echo Initializing KnowChain Database...
echo ======================================

echo Please make sure MySQL is running on localhost:3306
echo Default root password will be used: 1234
echo.

set /p confirm="Continue with database initialization? (Y/N): "
if /i "%confirm%" neq "Y" (
    echo Database initialization cancelled.
    pause
    exit /b 0
)

echo.
echo Connecting to MySQL and creating database...
mysql -u root -pyangxy -e "SOURCE %~dp0database\init.sql"

if %ERRORLEVEL% equ 0 (
    echo.
    echo ======================================
    echo Database initialization completed successfully!
    echo ======================================
    echo Database: knowchain
    echo Test Users:
    echo   - admin / admin
    echo   - test / test
    echo ======================================
) else (
    echo.
    echo ERROR: Database initialization failed!
    echo Please check:
    echo 1. MySQL server is running
    echo 2. Root password is correct (1234)
    echo 3. MySQL client is available in PATH
)

pause
