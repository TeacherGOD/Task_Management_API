# Task Management System/Система Управления задачами

Простая система управления задачами с аутентификацией, авторизацией и REST API.
Разработана на Java 17 с использованием Spring Boot, Spring Security и PostgreSQL.

## Запуск проекта

### 1. Локальный запуск (без Docker)
```
# Собрать проект
mvn package
# Запустить приложение
java -jar target/TaskManagement-0.0.1-SNAPSHOT.jar
```

### 2. Запуск через Docker
```
# Собрать и запустить контейнеры
docker-compose up --build
```

*(по какой-то причине, Lombok не работал, так что без `mvn clean package`. Но при запуске программы в IDE и `mvn package` проект собирается, а тесты проходятся. Проект доделывал в день, когда в России "поплохело" с кучей зарубежных сайтов (тот же docker.io не открывается без впн), поэтому не успел исправить данную проблему.)*



## Основные функции
Аутентификация через JWT  
Ролевая модель (Администратор/Пользователь)  
CRUD для задач, пользователей и комментариев  
Фильтрация задач по статусу, приоритету, автору и исполнителю  
Пагинация и сортировка  
Документация API через Swagger UI  
Контейнеризация через Docker

## Технологии
Backend: Java 17, Spring Boot 3.2, Spring Security, JWT  
База данных: PostgreSQL, Flyway (миграции)  
Тестирование: JUnit 5, Mockito, Testcontainers  
Инструменты: Maven, Lombok, ModelMapper  
Документация: Swagger/OpenAPI 3  
Инфраструктура: Docker, Docker Compose


## Конфигурация
Для удобства приложил данные файлы в репозиторий, но, в реальных проектах, само-собой, так делать не стоит.
#### Настройки в `application.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/task_management
spring.datasource.username=postgres
spring.datasource.password=%пароль на Субд%

app.jwt.secret=%jwt секретный ключ%
app.jwt.expiration-ms=86400000  # 24 часа
```
#### Настройки в `.env`:
```
# PostgreSQL
DB_PASSWORD='T3$T0VoE_Z4DAN!3_N4_Work'
# JWT
JWT_SECRET='UZrvOp%oOXfPW4WhgaV1Nj~57n~lHJwC'
#First Admin Init
INIT_ADMIN_EMAIL=%почта первого администратора%
INIT_ADMIN_PASSWORD=%Пароль для первого администратора%
INIT_ADMIN_NAME=%Имя для первого администратора%
```

## Документация API
Документация в Swagger, после запуска можно ознакомиться на сайте:
http://localhost:8080/swagger-ui.html

## Тестирование
```
# Запуск всех тестов
mvn test
# Покрытие кода (отчет в target/site/jacoco)
mvn jacoco:report
```

## Особенности
Автоматические миграции БД через Flyway  
Кастомные security-выражения для проверки прав доступа  
Глобальная обработка ошибок с детализированными ответами
