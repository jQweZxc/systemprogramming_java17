# Исправленный скрипт запуска
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "🚀 ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ ПАССАЖИРОПОТОКОМ" -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 1. Проверка Docker
Write-Host "[1/5] Проверка Docker..." -ForegroundColor Gray
docker version
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker не установлен или не запущен!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Docker работает" -ForegroundColor Green

# 2. Остановка предыдущих контейнеров
Write-Host "[2/5] Очистка предыдущих контейнеров..." -ForegroundColor Gray
docker-compose down --remove-orphans
Write-Host "✅ Система очищена" -ForegroundColor Green

# 3. Создание volumes
Write-Host "[3/5] Создание volumes на диске H:..." -ForegroundColor Gray
if (-not (Test-Path "H:\docker\volumes")) {
    New-Item -ItemType Directory -Path "H:\docker\volumes" -Force
    New-Item -ItemType Directory -Path "H:\docker\volumes\postgres" -Force
    New-Item -ItemType Directory -Path "H:\docker\volumes\pgadmin" -Force
    New-Item -ItemType Directory -Path "H:\docker\volumes\uploads" -Force
    New-Item -ItemType Directory -Path "H:\docker\volumes\logs" -Force
    Write-Host "✅ Volumes созданы" -ForegroundColor Green
} else {
    Write-Host "✅ Volumes уже существуют" -ForegroundColor Green
}

# 4. Удалить проблемные образы
Write-Host "[4/5] Очистка кэша Docker..." -ForegroundColor Gray
docker system prune -f
Write-Host "✅ Кэш очищен" -ForegroundColor Green

# 5. Сборка и запуск
Write-Host "[5/5] Сборка и запуск системы..." -ForegroundColor Gray
Write-Host "⏳ Это займет 2-5 минут..." -ForegroundColor Yellow
docker-compose up --build -d

# 6. Ожидание запуска
Write-Host "[6/5] Ожидание полного запуска..." -ForegroundColor Gray
Start-Sleep -Seconds 60

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "✅ СИСТЕМА УСПЕШНО ЗАПУЩЕНА!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Веб-интерфейс:  http://localhost:8080" -ForegroundColor Yellow
Write-Host "📋 Swagger API:    http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
Write-Host "🗄️  pgAdmin:        http://localhost:5050" -ForegroundColor Yellow
Write-Host "🔧 База данных:    localhost:5432 (postgres/karleon)" -ForegroundColor Yellow
Write-Host "🤖 Telegram бот:   @qwe24567Bot" -ForegroundColor Yellow
Write-Host ""
Write-Host "👤 Тестовый пользователь:" -ForegroundColor Gray
Write-Host "   Логин:    admin" -ForegroundColor White
Write-Host "   Пароль:   admin123" -ForegroundColor White
Write-Host ""
Write-Host "🔍 Мониторинг контейнеров:" -ForegroundColor Gray
Write-Host "   docker ps" -ForegroundColor White
Write-Host "   docker-compose logs -f app" -ForegroundColor White
Write-Host "============================================" -ForegroundColor Cyan