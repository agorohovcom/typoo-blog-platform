## Собираем и копируем shared модуль
#FROM maven:3.9.6-eclipse-temurin-21-alpine AS shared-build
#
#WORKDIR /shared
#
## Копируем shared модуль
#COPY ../../shared/pom.xml ./pom.xml
#COPY ../../shared/src ./src
#
## Копируем parent POM (корневой)
#COPY ../../pom.xml ../pom.xml
#
## Собираем и устанавливаем в локальный .m2
#RUN mvn clean install -DskipTests
#
## Этап сборки ...-service
#FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
#
## Аргумент для названия сервиса
#ARG SERVICE_NAME
#ENV SERVICE_DIR=services/${SERVICE_NAME}
#
#WORKDIR /build
#
## Копируем всё, что нужно для сборки
#COPY ../pom.xml ./pom.xml
#COPY ../../$SERVICE_DIR/pom.xml ./$SERVICE_DIR/pom.xml
#COPY ../../$SERVICE_DIR/src ./$SERVICE_DIR/src
#
## Копируем shared из предыдущего этапа (важно для кэша .m2)
#COPY --from=shared-build /root/.m2 /root/.m2
#
### Для go-offline нужно скопировать всё дерево, иначе не работает правильно
##COPY ../services ./services
##
### Загружаем зависимости
##RUN #mvn -f $SERVICE_DIR/pom.xml dependency:go-offline
#
## Сборка с пропуском тестов
#RUN mvn -f $SERVICE_DIR/pom.xml clean package -DskipTests
#
## Этап запуска
#FROM maven:3.9.6-eclipse-temurin-21-alpine
#
## Аргумент для названия сервиса
#ARG SERVICE_NAME
#ENV SERVICE_DIR=services/${SERVICE_NAME}
#
#WORKDIR /app
#
## Копируем JAR из предыдущего этапа
#COPY --from=build /build/services/article-service/target/*.jar app.jar
#
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

# 1. Этап сборки shared + service
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Копируем всю структуру проекта
COPY ../../pom.xml ./pom.xml
COPY ../../shared ./shared
COPY ../../services ./services

# Аргумент для сервиса
ARG SERVICE_NAME

# Собираем зависимость shared и сам сервис
RUN mvn clean install -DskipTests -pl shared,services/${SERVICE_NAME} -am

# 2. Этап рантайма
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG SERVICE_NAME
COPY --from=builder /app/services/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]