# Real-Time Chat & Notification Platform

A production-style backend project built with **Java 17 + Spring Boot**, demonstrating real-time messaging, event-driven notifications, and core distributed-systems concepts commonly used in industry.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

## Features

- 🔐 **JWT-based authentication** — register/login with BCrypt-hashed passwords
- 💬 **Real-time messaging** over WebSocket/STOMP
- 🗄️ **PostgreSQL** persistence for users, conversations, and messages
- ⚡ **Redis** for lightweight online-presence tracking
- 📣 **Apache Kafka** for asynchronous, event-driven notifications
- 🐳 **Docker Compose** for one-command local infrastructure
- 🖥️ A minimal static HTML/JS client (`/`) to try the whole flow in a browser

## Architecture

```
Browser Client
     │
     ├── REST (auth, conversations) ─────► Spring Boot
     │                                         │
     └── WebSocket/STOMP (/ws) ───────────────►│
                                                ├──► PostgreSQL (messages, users, conversations)
                                                ├──► Redis (presence)
                                                └──► Kafka topic "chat-notifications"
                                                          │
                                                          └──► Notification Consumer (logs event)
```

## Tech Stack

| Layer           | Technology                          |
|------------------|--------------------------------------|
| Language         | Java 17                             |
| Framework        | Spring Boot 3.3.5                   |
| Real-time        | Spring WebSocket + STOMP            |
| Database         | PostgreSQL + Spring Data JPA        |
| Cache/Presence   | Redis                               |
| Messaging/Events | Apache Kafka + Spring Kafka         |
| Auth             | JWT (jjwt 0.12.6) + Spring Security |
| Build tool       | Maven                               |
| Infra            | Docker Compose                      |

## Prerequisites

- Java 17+ (JDK)
- Maven 3.9+
- Docker & Docker Compose (for Postgres, Redis, Kafka)

## Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/b22me048/realtime-chat-notification-platform.git
cd realtime-chat-notification-platform
```

### 2. Start infrastructure (Postgres, Redis, Kafka)

```bash
docker compose up -d postgres redis kafka
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

> Default local config lives in `src/main/resources/application.yml` (DB credentials, JWT secret, Kafka/Redis hosts). For anything beyond local development, override these via environment variables rather than committing real secrets — see [Configuration](#configuration).

### 4. Try it out

Open http://localhost:8080 in your browser, or drive it manually:

1. **Register** — `POST /api/auth/register`
2. **Login** — `POST /api/auth/login` → returns a JWT
3. **Create a conversation** — `POST /api/conversations`
4. **Connect** to `/ws` with a STOMP client
5. **Subscribe** to `/topic/conversation/{conversationId}`
6. **Send** messages to `/app/chat/{conversationId}` with your JWT in the payload
7. Messages are persisted in PostgreSQL, a Kafka event is published per message, and the notification consumer logs it. Redis is updated with the sender's presence.

## API Reference

| Method | Endpoint                            | Description                      |
|--------|---------------------------------------|-----------------------------------|
| POST   | `/api/auth/register`                  | Create an account, returns JWT   |
| POST   | `/api/auth/login`                     | Authenticate, returns JWT        |
| POST   | `/api/conversations`                  | Create a conversation            |
| GET    | `/api/conversations/{id}`             | Get a conversation by ID         |
| GET    | `/api/conversations/{id}/messages`    | Last 100 messages (newest first) |

**WebSocket (STOMP over `/ws`)**

| Direction | Destination                            | Payload                                     |
|-----------|------------------------------------------|-----------------------------------------------|
| Subscribe | `/topic/conversation/{conversationId}`   | Receives `ChatMessage` objects               |
| Send      | `/app/chat/{conversationId}`             | `{ "token": "<jwt>", "content": "<text>" }`  |

## Configuration

Key settings in `application.yml` (override via env vars for anything beyond local dev):

| Setting                                    | Purpose                     | Default (local only)                       |
|----------------------------------------------|-------------------------------|-----------------------------------------------|
| `spring.datasource.url/username/password`  | PostgreSQL connection       | `chatdb` / `chatuser` / `chatpass`         |
| `spring.data.redis.host/port`              | Redis connection            | `localhost:6379`                           |
| `spring.kafka.bootstrap-servers`           | Kafka broker                | `localhost:9092`                           |
| `app.jwt.secret`                           | HMAC signing key for JWTs   | placeholder — **replace before any real deployment** |
| `app.jwt.expiration-ms`                    | Token lifetime in ms        | `86400000` (24h)                           |

⚠️ **Never commit real credentials or JWT secrets.** The values shipped here are for local development only.

## Project Structure

```
src/main/java/com/preetam/chat/
├── ChatApplication.java        # Spring Boot entry point
├── auth/                       # Registration & login
├── config/                     # WebSocket configuration
├── conversation/               # Conversation entity, repo, controller
├── message/                    # Message entity, repo, WebSocket controller
├── messaging/                  # Kafka config, event DTO, consumer
├── security/                   # JWT service, Spring Security config
└── user/                       # User entity & repository
src/main/resources/
├── application.yml
└── static/index.html           # Minimal demo client
```

## Known Limitations / Roadmap

- REST endpoints (e.g. message history) are currently `permitAll()` and don't enforce the JWT — only the WebSocket send path validates the token. A follow-up would add a JWT filter for REST auth.
- No automated test suite yet (`spring-boot-starter-test` is included and ready to use).
- `ddl-auto: update` is convenient for local dev; use a migration tool (Flyway/Liquibase) for production.

## License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.
