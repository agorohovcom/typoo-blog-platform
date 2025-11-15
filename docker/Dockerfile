# 1. Этап сборки
FROM gradle:9.2.0-jdk21-alpine AS builder
WORKDIR /app
ARG SERVICE_NAME

# Копируем только необходимые файлы для кеширования зависимостей
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY shared/build.gradle.kts shared/
COPY services/${SERVICE_NAME}/build.gradle.kts services/${SERVICE_NAME}/

# Копируем исходный код
COPY shared/src shared/src
COPY services/${SERVICE_NAME}/src services/${SERVICE_NAME}/src

# Копируем .env файл
COPY services/${SERVICE_NAME}/.env.docker services/${SERVICE_NAME}/

# Собираем только нужный сервис
RUN gradle clean :services:${SERVICE_NAME}:build -x test --no-daemon

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE_NAME

# Копируем собранный JAR
COPY --from=builder /app/services/${SERVICE_NAME}/build/libs/*.jar app.jar

# Копируем .env файл
COPY --from=builder /app/services/${SERVICE_NAME}/.env.docker services/${SERVICE_NAME}/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]