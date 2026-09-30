# hexlet-spring-another-blog

Учебный проект на Spring Boot 4 (Java 24, Gradle 9): REST API блога — пользователи и посты.

![CI](https://github.com/Textile86/hexlet-spring-another-blog/actions/workflows/ci.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Textile86_hexlet-spring-another-blog&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Textile86_hexlet-spring-another-blog)

## Запуск

```bash
./gradlew bootRun          # http://localhost:8081
```

## Тесты, покрытие, статический анализ

```bash
./gradlew clean build       # тесты + JaCoCo (tasks.check зависит от jacocoTestReport)
./gradlew jacocoTestReport  # только отчёт о покрытии

SONAR_TOKEN=ваш_токен ./gradlew sonar   # анализ в SonarCloud
```

Локальные отчёты:

- `build/reports/tests/test/index.html` — результаты тестов
- `build/reports/jacoco/test/html/index.html` — покрытие кода

## Эндпоинты

| Метод  | URL               | Описание                            |
|--------|-------------------|-------------------------------------|
| GET    | `/api/users`      | список пользователей                |
| POST   | `/api/users`      | создать пользователя                |
| GET    | `/api/users/{id}` | пользователь по id                  |
| PUT    | `/api/users/{id}` | обновить пользователя               |
| DELETE | `/api/users/{id}` | удалить пользователя                |
| GET    | `/api/posts`      | опубликованные посты с пагинацией   |
| POST   | `/api/posts`      | создать пост                        |
| GET    | `/api/posts/{id}` | пост по id                          |
| PUT    | `/api/posts/{id}` | обновить пост                       |
| DELETE | `/api/posts/{id}` | удалить пост                        |

Пагинация и сортировка: `GET /api/posts?page=0&size=5&sort=createdAt,asc`
