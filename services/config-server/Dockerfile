# 1. Этап сборки
FROM gradle:9.2.0-jdk21-alpine AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle

COPY shared shared

COPY services/config-server/build.gradle.kts services/config-server/
COPY services/config-server/src services/config-server/src

# # Копируем .env файл
# COPY services/config-server/.env.docker services/config-server/

# Собираем только текущий сервис
RUN gradle clean :services:config-server:bootJar -x test --no-daemon

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/services/config-server/build/libs/*.jar app.jar

# # Копируем .env файл
# COPY --from=builder /app/services/config-server/.env.docker services/config-server/

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]