@echo off
TITLE Enterprise Selenium Test Dashboard
cls
echo ====================================================================
echo    Enterprise Selenium E-Commerce Framework - Web Dashboard Launcher
echo ====================================================================
echo.

cd /d "%~dp0\dashboard"

if not exist node_modules (
    echo [INFO] Installing Dashboard Node.js Dependencies...
    call npm install
    echo.
)

echo [INFO] Starting Dashboard Web Server on http://localhost:3000 ...
echo [INFO] Opening default browser...
start http://localhost:3000

node server.js

pause
