@echo off
chcp 65001
cls

echo ============================================
echo 🐳 ЗАПУСК СИСТЕМЫ С DOCKER НА ДИСКЕ H:
echo ============================================
echo.

echo [1/4] Проверка Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker не установлен!
    echo Установите Docker Desktop: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)
echo ✅ Docker установлен

echo.
echo [2/4] Проверка свободного места на H:...
for /f "tokens=3" %%a in ('dir H:\ /-c ^| find "свободно"') do set free=%%a
echo ✅ Свободно на H:: %free%

echo.
echo [3/4] Создание папок для Docker...
if not exist "H:\docker_data" mkdir H:\docker_data
if not exist "H:\docker_data\postgres" mkdir H:\docker_data\postgres
if not exist "H:\docker_data\pgadmin" mkdir H:\docker_data\pgadmin
if not exist "H:\docker_data\uploads" mkdir H:\docker_data\uploads
if not exist "H:\docker_data\logs" mkdir H:\docker_data\logs
echo ✅ Папки созданы

echo.
echo [4/4] Запуск Docker Compose...
echo ⏳ Сборка и запуск контейнеров... Это займет 2-5 минут
echo.

docker-compose down
docker-compose up --build

pause