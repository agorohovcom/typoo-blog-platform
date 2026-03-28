# Typoo Blog Platform – Архитектура проекта

## 🎯 Общее описание

Typoo Blog Platform — микросервисная блог‑платформа, разрабатываемая как pet‑project для замены WordPress на *
*agorohov.com**. Это монорепозиторий на Java с современным стеком технологий, ориентированный на масштабируемость и
лучшие практики разработки.

**Цель проекта**

- Создать полноценную блог‑платформу, расширяемую до веб‑сайта с портфолио, информационными разделами и интерактивными
  приложениями.

---

## 🏗️ Архитектурные принципы

### Микросервисная архитектура
- Монорепозиторий с модульной структурой
- Service Discovery через **Consul**
- API‑Gateway для маршрутизации запросов
- Centralized Config — Spring Cloud Config
- **gRPC** для межсервисного взаимодействия
- Event‑driven коммуникация через **Kafka** (планируется)
- SSR‑Frontend через выделенный **UI Service** (BFF‑паттерн)

### Технологический стек

- **Java 21**, **Spring Boot 3.5.3**, **Spring Cloud 2025.0.0**
- Gradle + `libs.versions.toml`
- PostgreSQL + Liquibase
- **gRPC + Protocol Buffers**
- Docker + Docker‑Compose
- Testcontainers

---

## 📁 Структура проекта

```text
typoo-blog-platform/
├─ 📄 Конфигурационные файлы
│   ├─ build.gradle.kts
│   ├─ settings.gradle.kts
│   ├─ gradle.properties
│   └─ gradle/libs.versions.toml
│
├─ 📦 Модули
│   ├─ shared/
│   │   ├─ build.gradle.kts
│   │   ├─ src/main/java/com/agorohov/shared/utils/dotenvloader/
│   │   └─ src/main/proto/
│   └─ services/
│       ├─ config-server/
│       ├─ gateway-server/
│       ├─ article-service/
│       ├─ content-service/
│       └─ ui-service/
│
├─ 🐳 Docker & инфраструктура
│   ├─ docker‑compose.yml
│   └─ prometheus/
│
└─ 📋 Документация
    └─ README.md
```

---

## 🔧 Ключевые сервисы

### 1. Config Server (`config-server`)
- Централизованное управление конфигурациями
- Поддержка профилей
- Интеграция с Consul

### 2. Gateway Server (`gateway-server`)

- Единая точка входа
- Маршрутизация запросов и load‑balancing
- Прокси‑соединения от UI Service
- Метрики через **Prometheus**

### 3. Article Service (`article-service`)

- CRUD‑операции над статьями
- Статусы: `DRAFT`, `PUBLISHED`, `ARCHIVED`
- PostgreSQL + Liquibase
- Теги, категории, версии
- Интеграция с Content Service

### 4. Content Service (`content-service`)

- Управление файлами и медиа
- Хранилища: локальное, S3, GridFS
- Обработка изображений
- gRPC‑API и REST‑через Gateway

### 5. UI Service (`ui-service`) – Frontend / BFF

- SSR‑frontend на **Spring Boot + Thymeleaf + HTMX + TailwindCSS**
- Backend‑For‑Frontend слой
- Общение через Gateway

---

## 📋 Основные обязанности

- **Рендеринг страниц**: `/`, `/blog`, `/blog/{slug}`, категории
- **Агрегация данных**: Article Service, Content Service, (планируется Comment Service)
- **Формирование ViewModel** для UI

---

## 🏗️ Архитектура UI Service

```
Controller → Facade → Client → Gateway → Services
          ↓
        Mapper
          ↓
       ViewModel
```

### Внутренние слои

- **Controller** – принимает HTTP‑запросы, вызывает Facade, возвращает HTML.
- **Facade** – агрегирует данные, описывает сценарии страниц, без бизнес‑логики.
   - Примеры методов: `getArticlePage`, `getHomePage`, `getCategoryPage`.
- **Client** – обёртки над API (e.g., `ArticleClient`, `ContentClient`, `CommentClient`).
- **Mapper** – преобразует DTO в ViewModel.
- **ViewModel** – модели, используемые UI (e.g., `ArticlePageView`, `HomePageView`, `CommentListView`).

---

## 🛠️ Технологии UI

### HTMX (пример)

```html

<div hx-get="/comments?articleId=..." hx-trigger="load"></div>
```

- Stateless, без сессий
- JWT (в планах)
- Cookies при необходимости

**Ограничения**

- ❌ Нет бизнес‑логики
- ❌ Нет состояния
- ✅ Только orchestration и отображение

**Взаимодействие**
Browser → UI Service → Gateway → Article / Content / Comment

### Почему SSR + HTMX?

- SEO‑дружественно
- Быстрый рендер
- Минимум JavaScript
- Проще, чем SPA

---

## 📦 Shared Module (`shared`)

- `DotenvLoader` — загрузка переменных окружения
- `.env` профили, Docker‑secrets
- Общие DTO и protobuf‑сообщения

---

## 🚀 Текущее состояние

### ✅ Сделано

- Инфраструктура (Docker, compose)
- Gateway Server
- Article Service
- Мониторинг

### 🚧 В процессе

- Логика статей
- Content Service
- Frontend (UI Service)

---

## 📆 Планы

- MVP Content Service
- Расширение Article Service
- Comment Service + Kafka
- Интеграция Keycloak

---

## 🔄 Workflow

```sh
docker-compose up
./gradlew build
```

---

## 🎯 Особенности

- Гибкая конфигурация
- Универсальный Content Service
- Production‑ready
- Event‑driven готовность

---

## 🔮 Roadmap

**Фаза 1** – Блог + медиа

**Фаза 2** – Портфолио, профиль

**Фаза 3** – Поиск, кеш, CDN