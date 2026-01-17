# ============================================
# МНОГОСТУПЕНЧАТАЯ СБОРКА С КЭШИРОВАНИЕМ
# ============================================

# Stage 1: Базовый образ для загрузки зависимостей
FROM maven:3.9-eclipse-temurin-17 AS deps
WORKDIR /app

# Копируем только POM для кэширования зависимостей
COPY pom.xml .

# Скачиваем все зависимости (кэшируется отдельно)
RUN mvn dependency:go-offline -B

# Stage 2: Сборка приложения
FROM deps AS builder
WORKDIR /app

# Копируем исходный код
COPY src ./src

# Собираем приложение (кэшируется результат компиляции)
RUN mvn clean package -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dcheckstyle.skip=true \
    -Dspotbugs.skip=true \
    -Djacoco.skip=true \
    -Denforcer.skip=true

# Stage 3: Финальный образ
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Создаем необходимые директории СНАЧАЛА
RUN mkdir -p /app/uploads /app/logs

# Создаем пользователя
RUN addgroup -S spring && adduser -S spring -G spring

# Меняем владельца директорий
RUN chown -R spring:spring /app/uploads /app/logs

# Переключаемся на пользователя
USER spring:spring

# Копируем JAR из стадии builder (правильное имя стадии!)
COPY --from=builder /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]