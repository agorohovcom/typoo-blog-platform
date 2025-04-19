# Этап сборки
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Аргумент для названия сервиса
ARG SERVICE_NAME
ENV SERVICE_DIR=services/${SERVICE_NAME}

# Рабочая директория
WORKDIR /build

# Копируем всё, что нужно для сборки
COPY ../pom.xml ./pom.xml
COPY ../../$SERVICE_DIR/pom.xml ./$SERVICE_DIR/pom.xml
COPY ../../$SERVICE_DIR/src ./$SERVICE_DIR/src


# Для go-offline нужно скопировать всё дерево, иначе не работает правильно
COPY ../services ./services

# Загружаем зависимости
#RUN mvn -f $SERVICE_DIR/pom.xml dependency:go-offline

# Сборка с пропуском тестов
RUN mvn -f $SERVICE_DIR/pom.xml clean package -DskipTests

# Этап запуска
FROM maven:3.9.6-eclipse-temurin-21-alpine

# Аргумент для названия сервиса
ARG SERVICE_NAME
ENV SERVICE_DIR=services/${SERVICE_NAME}

WORKDIR /app

# Копируем JAR из предыдущего этапа
COPY --from=build /build/services/article-service/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]