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
COPY services/config-server/build.gradle.kts services/config-server/
RUN gradle :services:config-server:compileJava --no-daemon || return 0

# Копируем исходники и собираем JAR
COPY services/config-server/src services/config-server/src
# Копируем конфиги в build-контексте для native конфиг сервера (закомментировать при использовании volume)
COPY services/config-server/config services/config-server/config
RUN gradle :services:config-server:bootJar -x test --no-daemon

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/services/config-server/build/libs/*.jar app.jar

# Копируем конфиги из этапа сборки (закомментировать при использовании volume)
COPY --from=builder /app/services/config-server/config /services/config-server/config

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]