# 1. Этап сборки
FROM gradle:9.2.0-jdk21-alpine AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle

COPY shared shared

COPY services/article-service/build.gradle.kts services/article-service/
COPY services/article-service/src services/article-service/src

# Копируем .env файл
COPY services/article-service/.env.docker services/article-service/

# Собираем только текущий сервис
RUN gradle clean :services:article-service:bootJar -x test --no-daemon

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/services/article-service/build/libs/*.jar app.jar

# Копируем .env файл
COPY --from=builder /app/services/article-service/.env.docker services/article-service/

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]