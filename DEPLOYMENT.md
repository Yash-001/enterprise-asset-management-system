# EAMS Deployment Guide

## Prerequisites

- Docker & Docker Compose v2+
- PostgreSQL 16+ (if running without Docker)
- Java 21 (if running without Docker)

## Quick Start (Docker)

```bash
# 1. Copy and configure environment
cp .env.example .env
# Edit .env with your production values (DB_PASSWORD, JWT_SECRET are required)

# 2. Build and start
docker compose -f docker-compose.prod.yml up -d --build

# 3. Check health
curl http://localhost:8080/management/health
```

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_PASSWORD` | Yes | - | PostgreSQL password |
| `JWT_SECRET` | Yes | - | Base64-encoded JWT signing key (min 256 bits) |
| `DB_NAME` | No | `enterprise_asset_management` | Database name |
| `DB_USERNAME` | No | `eams_user` | Database username |
| `JWT_EXPIRATION` | No | `12h` | JWT token expiry |
| `APP_PORT` | No | `8080` | Host port mapping |
| `MAIL_HOST` | No | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | No | `587` | SMTP port |
| `MAIL_USERNAME` | No | - | SMTP username |
| `MAIL_PASSWORD` | No | - | SMTP password |
| `MAIL_FROM` | No | `noreply@eams.com` | Sender address |
| `STORAGE_PATH` | No | `/app/uploads` | File upload directory |

## Profiles

| Profile | Usage | Activate |
|---------|-------|----------|
| `dev` | Local development, verbose logging, Swagger UI | `SPRING_PROFILES_ACTIVE=dev` |
| `test` | Automated tests, minimal logging, schedulers disabled | `SPRING_PROFILES_ACTIVE=test` |
| `prod` | Production, file logging, Swagger disabled, tuned pools | `SPRING_PROFILES_ACTIVE=prod` |

## Running Without Docker

```bash
cd backend

# Build
./mvnw clean package -DskipTests

# Run with prod profile
java -jar target/enterprise-asset-management-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_URL=jdbc:postgresql://your-host:5432/enterprise_asset_management \
  --DB_USERNAME=eams_user \
  --DB_PASSWORD=your_password \
  --JWT_SECRET=your_jwt_secret
```

## Health Checks

- **Liveness**: `GET /management/health/liveness`
- **Readiness**: `GET /management/health/readiness`
- **Full health**: `GET /management/health` (requires authentication)

## Database Migrations

Flyway runs automatically on startup. In production:
- `baseline-on-migrate=false` (no auto-baseline)
- `clean-disabled=true` (prevents accidental data loss)
- `validate-on-migrate=true` (ensures schema integrity)

## Graceful Shutdown

The application handles `SIGTERM` gracefully with a 30-second drain period:
- Active requests complete before shutdown
- Connection pool drains open connections
- Scheduled tasks finish current iteration

## Logs

- **Dev**: Console only (DEBUG level)
- **Prod**: File-based at `/var/log/eams/eams-application.log`
  - Max file size: 100MB
  - Retention: 30 days
  - Total cap: 3GB

## Monitoring

Actuator endpoints (prod):
- `/management/health` — application health
- `/management/info` — build info
- `/management/metrics` — Micrometer metrics

## Scaling

To run multiple instances:
```bash
docker compose -f docker-compose.prod.yml up -d --scale app=3
```
Note: Add a reverse proxy (Nginx/Traefik) for load balancing when scaling.
