@echo off
title Hava Durumu Geri Bildirim Sunucusu
cd /d "%~dp0"
echo ===================================================
echo   Hava Durumu Geri Bildirim Backend Baslatiliyor
echo ===================================================
node server.js
if %errorlevel% neq 0 (
    echo.
    echo [HATA] Node.js calistirilamadi! Lutfen Node.js'in yuklu oldugundan emin olun.
)
pause
