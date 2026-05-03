# 1. Этап сборки
FROM gradle:9.2.0-jdk21-alpine AS builder
WORKDIR /app

# Кешируем зависимости
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || return 0

# Кешируем shared модуль
COPY shared shared
RUN gradle :shared:compileJava --no-daemon || return 0

# Копируем билд скрипт сервиса
COPY services/gateway-server/build.gradle.kts services/gateway-server/
RUN gradle :services:gateway-server:compileJava --no-daemon || return 0

# Копируем исходники и собираем JAR
COPY services/gateway-server/src services/gateway-server/src
RUN gradle :services:gateway-server:bootJar -x test --no-daemon

# Собираем только текущий сервис
RUN gradle clean :services:gateway-server:bootJar -x test --no-daemon

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/services/gateway-server/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]