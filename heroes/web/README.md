# heroes-web

Чат-комнаты на Spring Boot 4: сообщения по SSE, вход по JWT, Telegram-бот на long polling,
хранение в PostgreSQL, метрики в Prometheus и Grafana.

## Переменные окружения

Приложение не стартует без секрета подписи токенов — это сделано намеренно, раньше ключ был
зашит в исходник и попал в репозиторий.

| Переменная | Обязательна | Что задаёт |
|---|---|---|
| `JWT_SECRET` | да | Ключ подписи JWT в base64 |
| `TELEGRAM_BOT_TOKEN` | да | Токен бота |
| `CORS_ALLOWED_ORIGINS` | нет | Источники для CORS, по умолчанию `http://localhost:9097` |
| `ADMIN_PASSWORD` | нет | Пароль администратора; пусто — учётка не заводится |

Сгенерировать секрет:

```bash
openssl rand -base64 64 | tr -d '\n'
```

## Запуск

Поднять окружение — PostgreSQL на порту 25432, Redis, Prometheus, Grafana:

```bash
docker compose -f heroes/web/docker-compose.yml up -d
```

Затем само приложение с профилем `local`:

```bash
JWT_SECRET=... TELEGRAM_BOT_TOKEN=... mvn -q -pl heroes/web spring-boot:run -Dspring-boot.run.profiles=local
```

Интерфейс — http://localhost:9097/web/index.html, описание API — http://localhost:9097/swagger-ui.html.

## Профили

| Профиль | База | Схема |
|---|---|---|
| по умолчанию | `heroes-db:5432` внутри docker-сети | `validate` — схема должна существовать заранее |
| `local` | `127.0.0.1:25432`, порт из docker-compose | `update` — Hibernate достраивает схему |

## Что внутри

| Пакет | Назначение |
|---|---|
| `controller` | Аутентификация, чат по SSE, комнаты |
| `service` | JWT, пользователи, присутствие в комнате, Telegram |
| `domain` | Сущности JPA и модели запросов |
| `configuration` | Безопасность, веб, планировщик, Redis, Telegram |
| `event` | Заготовка журнала событий |

Из actuator наружу открыты только `health`, `info` и `prometheus`; остальное — под ролью `ADMIN`.
