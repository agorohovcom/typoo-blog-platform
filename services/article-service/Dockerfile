## Этап сборки
#FROM maven:3.8.4-openjdk-17-slim AS build
#WORKDIR /app
#
## Копируем только POM сначала (для кэширования зависимостей)
#COPY pom.xml .
## Скачиваем зависимости
#RUN mvn dependency:go-offline
#
## Копируем исходный код модуля
#COPY src ./src
## Собираем JAR (указываем путь к POM модуля явно)
#RUN mvn clean package -pl article-service -am -DskipTests
#
## Этап запуска
#FROM openjdk:17-jdk-alpine
#WORKDIR /app
## Копируем JAR из папки target модуля
#COPY --from=build /app/article-service/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

## Этап сборки
#FROM maven:3.8.4-openjdk-17-slim AS build
#WORKDIR /app
#
## 1. Копируем родительский POM (BOM) из корня проекта
#COPY ../../../pom.xml ./
#
## 2. Копируем POM текущего сервиса
#COPY ./pom.xml ./
#
## 3. Устанавливаем BOM в локальный репозиторий
#RUN mvn -f pom.xml install -N -DskipTests
#
## 4. Копируем исходный код сервиса
#COPY ./src ./src
#
## 5. Собираем сервис (указываем путь к POM явно)
#RUN mvn -f pom.xml clean package -DskipTests
#
## Этап запуска
#FROM openjdk:17-jdk-alpine
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Этап сборки
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Рабочая директория
WORKDIR /build

# Копируем всё, что нужно для сборки
COPY ../../pom.xml ./pom.xml
COPY ../../services/article-service/pom.xml ./services/article-service/pom.xml
COPY ../../services/article-service/src ./services/article-service/src

# Для go-offline нужно скопировать всё дерево, иначе не работает правильно
COPY ../../services ./services

# Загружаем зависимости
RUN mvn -f services/article-service/pom.xml dependency:go-offline

# Сборка с пропуском тестов
RUN mvn -f services/article-service/pom.xml clean package -DskipTests

# Этап запуска
FROM maven:3.9.6-eclipse-temurin-21-alpine

WORKDIR /app

# Копируем JAR из предыдущего этапа
COPY --from=build /build/services/article-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]