# 🏭 Enterprise Asset Management System (EAMS)

[![CI — Build & Test](https://github.com/yashconsulting/enterprise-asset-management-system/actions/workflows/ci.yml/badge.svg)](https://github.com/yashconsulting/enterprise-asset-management-system/actions/workflows/ci.yml)
[![Docker — Build & Push](https://github.com/yashconsulting/enterprise-asset-management-system/actions/workflows/docker.yml/badge.svg)](https://github.com/yashconsulting/enterprise-asset-management-system/actions/workflows/docker.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A production-ready, enterprise-grade Asset Management System built with Spring Boot 3. Designed for organizations to track physical assets, manage maintenance schedules, handle inventory, process purchase orders, and maintain a complete audit trail — all through a secure REST API.

---

## 📐 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Applications                       │
│              (Web App / Mobile App / Third-Party)                │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS
┌────────────────────────────▼────────────────────────────────────┐
│                     Nginx Reverse Proxy                          │
│              (SSL Termination, Rate Limiting)                    │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP :8080
┌────────────────────────────▼────────────────────────────────────┐
│                    Spring Boot Application                        │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │   Auth   │  │  Asset   │  │  Work    │  │  Purchase    │   │
│  │  Module  │  │  Module  │  │  Order   │  │   Order      │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │Inventory │  │Maintenan.│  │  Notif.  │  │   Document   │   │
│  │  Module  │  │  Module  │  │  Module  │  │   Module     │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │  Email   │  │Scheduler │  │  Audit   │  │   Report     │   │
│  │ Service  │  │  Module  │  │   Log    │  │   Module     │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘   │
│                                                                  │
│  Cross-Cutting: Security (JWT) │ AOP Auditing │ Event Bus       │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    PostgreSQL Database                            │
│              (Flyway Managed Migrations)                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠 Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5 |
| **Security** | Spring Security + JWT (jjwt 0.12) |
| **Database** | PostgreSQL 17 |
| **ORM** | Spring Data JPA / Hibernate |
| **Migrations** | Flyway |
| **Email** | Spring Mail + Thymeleaf templates |
| **Scheduling** | Spring Scheduler (externalized cron) |
| **Audit** | Custom AOP-based audit logging |
| **Events** | Spring ApplicationEvents (async) |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Testing** | JUnit 5, Mockito, Testcontainers, MockMvc |
| **Build** | Maven |
| **Containerization** | Docker (multi-stage build) |
| **CI/CD** | GitHub Actions |
| **Deployment** | Render / AWS EC2 / Docker Compose |

---

## ✨ Features

### Core Modules
- **Asset Management** — Full CRUD, status lifecycle (Available → Assigned → Maintenance → Disposed), search & filtering
- **Asset Assignment** — Assign/return assets to employees, track assignment history
- **Work Orders** — Create, assign, track work orders with status workflow (Requested → Assigned → In Progress → Completed)
- **Maintenance Plans** — Scheduled maintenance with frequency configuration, overdue detection, auto-rescheduling
- **Inventory Management** — Spare parts tracking, stock transactions (IN/OUT/RETURN/ADJUSTMENT), low stock alerts
- **Purchase Orders** — Full procurement workflow (Draft → Approved → Ordered → Received), auto stock receiving
- **Vendor Management** — Vendor registry linked to purchase orders

### Enterprise Features
- **Authentication & Authorization** — JWT-based auth with role hierarchy (Admin, Manager, User)
- **Notification System** — In-app notifications via Spring Events (7 event types, async)
- **Email Service** — HTML email templates (welcome, password reset, maintenance reminder, PO approval, low stock, work order)
- **Document Management** — File upload/download with storage abstraction (local filesystem, cloud-ready)
- **Audit Logging** — Automatic CRUD tracking via AOP (entity, action, before/after, user, IP, timestamp)
- **Scheduled Jobs** — Midnight (overdue detection, reminders) + hourly (stock checks, upcoming maintenance)
- **Reporting** — Dashboard metrics and aggregated reports
- **CORS** — Fully configurable via environment variables

### Production Ready
- **Multi-environment config** — local, dev, test, prod profiles
- **Graceful shutdown** — 30s drain period
- **Connection pooling** — HikariCP tuned per environment
- **Health checks** — Spring Actuator (liveness, readiness, DB, disk, mail)
- **Docker** — Multi-stage build, layered JAR, non-root user, health check
- **CI/CD** — GitHub Actions (build → test → Docker → deploy)
- **Deployment** — Render Blueprint, AWS (EC2 + RDS + Nginx), Docker Compose

---

## 📸 Screenshots

> _Screenshots will be added once the frontend is implemented._

| Screen | Preview |
|--------|---------|
| Swagger API Docs | ![Swagger UI](docs/screenshots/swagger-ui.png) |
| Asset Dashboard | ![Assets](docs/screenshots/assets.png) |
| Work Order Management | ![Work Orders](docs/screenshots/work-orders.png) |
| Maintenance Calendar | ![Maintenance](docs/screenshots/maintenance.png) |
| Inventory Overview | ![Inventory](docs/screenshots/inventory.png) |

---

## 🐳 Docker Setup

### Local Development

```bash
# Start PostgreSQL + App
docker compose up -d

# View logs
docker compose logs -f app

# Stop
docker compose down

# Reset (destroy volumes)
docker compose down -v
```

### Production

```bash
# Configure environment
cp .env.example .env
# Edit .env with production values

# Build and deploy
docker compose -f docker-compose.prod.yml up -d --build

# Verify health
curl http://localhost:8080/management/health
```

---

## 🚀 Render Deployment

This project includes a `render.yaml` Blueprint for one-click deployment on [Render](https://render.com):

1. Push code to GitHub
2. Go to Render Dashboard → **New** → **Blueprint**
3. Connect your repository
4. Render auto-detects `render.yaml` and provisions:
   - Web Service (Docker)
   - PostgreSQL database
   - Environment variables (auto-generated JWT secret)
5. Set mail credentials in the Render dashboard (marked `sync: false`)
6. Deploy

The app will be available at: `https://eams-backend.onrender.com`

---

## 📖 Swagger / API Documentation

Once the application is running, access the interactive API docs:

| Environment | URL |
|-------------|-----|
| Local | http://localhost:8080/swagger-ui.html |
| Dev | http://localhost:8080/swagger-ui.html |
| Production | Disabled (security) |

---

## 🔐 Authentication Flow

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Client  │         │   EAMS   │         │    DB    │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                     │                     │
     │  POST /auth/login   │                     │
     │  {email, password}  │                     │
     │────────────────────>│                     │
     │                     │  Verify credentials │
     │                     │────────────────────>│
     │                     │     User found      │
     │                     │<────────────────────│
     │                     │                     │
     │  200 {accessToken}  │                     │
     │<────────────────────│                     │
     │                     │                     │
     │  GET /api/v1/assets │                     │
     │  Authorization:     │                     │
     │  Bearer <token>     │                     │
     │────────────────────>│                     │
     │                     │  Validate JWT       │
     │                     │  Check role         │
     │                     │────────────────────>│
     │  200 [assets...]    │                     │
     │<────────────────────│                     │
```

**Roles:**
| Role | Permissions |
|------|-------------|
| `ADMIN` | Full access to all modules |
| `MANAGER` | CRUD on assets, work orders, POs, notifications |
| `USER` | Read-only + own notifications + document upload |

---

## 📁 Folder Structure

```
enterprise-asset-management-system/
├── .github/workflows/          # CI/CD pipelines
│   ├── ci.yml                  # Build & Test
│   └── docker.yml              # Docker Build & Push
├── backend/
│   ├── src/main/java/com/yashconsulting/eams/
│   │   ├── asset/              # Asset management module
│   │   ├── assignment/         # Asset assignment module
│   │   ├── audit/              # Audit logging (AOP)
│   │   ├── auth/               # Authentication (JWT)
│   │   ├── config/             # Security, CORS, app config
│   │   ├── department/         # Department module
│   │   ├── document/           # File attachment module
│   │   ├── email/              # Email service (Thymeleaf)
│   │   ├── exception/          # Global exception handling
│   │   ├── inventory/          # Spare parts & stock
│   │   ├── location/           # Location module
│   │   ├── maintenance/        # Maintenance planning
│   │   ├── notification/       # In-app notifications & events
│   │   ├── purchase/           # Purchase order module
│   │   ├── report/             # Reporting & dashboards
│   │   ├── scheduler/          # Scheduled jobs
│   │   ├── security/           # JWT filter, user details
│   │   ├── user/               # User management
│   │   ├── vendor/             # Vendor management
│   │   └── workorder/          # Work order module
│   ├── src/main/resources/
│   │   ├── application.properties          # Base config
│   │   ├── application-local.properties    # IDE development
│   │   ├── application-dev.properties      # Docker dev
│   │   ├── application-test.properties     # Test profile
│   │   ├── application-prod.properties     # Production
│   │   ├── db/migration/                   # Flyway SQL scripts
│   │   └── templates/email/                # HTML email templates
│   ├── src/test/                           # Unit + Integration tests
│   ├── Dockerfile                          # Multi-stage production build
│   └── pom.xml
├── deploy/
│   ├── AWS_DEPLOYMENT.md       # AWS deployment guide
│   ├── docker-compose.aws.yml  # AWS production stack
│   ├── nginx/nginx.conf        # Nginx reverse proxy config
│   ├── cloudwatch/             # Monitoring config
│   └── scripts/                # Deploy, rollback, backup, SSL
├── docker-compose.yml          # Local development
├── docker-compose.prod.yml     # Production (standalone)
├── render.yaml                 # Render Blueprint
├── DEPLOYMENT.md               # Deployment guide
├── .env.example                # Environment variable template
└── README.md                   # This file
```

---

## 🗺 Future Roadmap

- [ ] **Frontend** — React/Angular dashboard with real-time notifications
- [ ] **WebSocket** — Live notification push via STOMP
- [ ] **Cloud Storage** — AWS S3 / Azure Blob adapter for documents
- [ ] **Barcode/QR** — Asset tagging and scanning
- [ ] **Mobile App** — React Native or Flutter companion app
- [ ] **Multi-Tenancy** — Organization-scoped data isolation
- [ ] **Reporting Engine** — PDF/Excel export, scheduled report delivery
- [ ] **LDAP/SSO** — Active Directory / SAML / OAuth2 integration
- [ ] **Asset Depreciation** — Automated financial calculations
- [ ] **Predictive Maintenance** — ML-based failure prediction
- [ ] **API Rate Limiting** — Per-user/per-role throttling
- [ ] **Internationalization** — Multi-language support (i18n)

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature`
3. **Commit** with clear messages: `git commit -m "feat: add barcode scanning"`
4. **Push** to your fork: `git push origin feature/your-feature`
5. **Open** a Pull Request against `develop`

### Guidelines
- Follow existing code style and package structure
- Write unit tests for service layer (target 80%+ coverage)
- Write integration tests for new endpoints
- Update Swagger annotations for API changes
- Add Flyway migration for schema changes (never modify existing migrations)
- Run `./mvnw verify` locally before pushing

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 👤 Author

**Yash Ranjan** — [Yash Consulting](https://github.com/yashconsulting)

Built as a portfolio project demonstrating enterprise Java development patterns, production deployment practices, and consulting-grade software engineering.
