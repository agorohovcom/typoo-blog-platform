# 1. Этап сборки
FROM gradle:9.2.0-jdk21-alpine AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle

COPY shared shared

COPY services/gateway-server/build.gradle.kts services/gateway-server/
COPY services/gateway-server/src services/gateway-server/src

# # Копируем .env файл
# COPY services/gateway-server/.env.docker services/gateway-server/

# Собираем только текущий сервис
RUN gradle clean :services:gateway-server:bootJar -x test --no-daemon

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/services/gateway-server/build/libs/*.jar app.jar

# # Копируем .env файл
# COPY --from=builder /app/services/gateway-server/.env.docker services/gateway-server/

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]