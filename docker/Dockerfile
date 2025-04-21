# 1. Этап сборки shared + ...-service
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

ARG SERVICE_NAME

# Копируем всю структуру проекта
COPY ../../pom.xml ./pom.xml
COPY ../../shared ./shared
COPY ../../services/${SERVICE_NAME} ./services/${SERVICE_NAME}

# Собираем зависимость shared и сам сервис
RUN mvn clean install -DskipTests -pl shared,services/${SERVICE_NAME} -am

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG SERVICE_NAME
COPY --from=builder /app/services/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]