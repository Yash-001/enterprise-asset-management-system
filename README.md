# Enterprise Asset Management System

## Overview

Enterprise Asset Management System is a modern full-stack application designed to help organizations manage physical and digital assets through a secure, scalable, and enterprise-ready platform.

This project is being developed as a portfolio application demonstrating enterprise architecture, clean code practices, REST API design, and AI-powered business features.

---

## Status

🚧 Under Development

---

## Planned Features

- Authentication
- Authorization
- Asset Management
- Categories
- Departments
- Vendors
- Asset Assignment
- Maintenance Tracking
- Dashboard
- Reports
- Audit Logs
- Notifications
- AI Assistant

---

## Technology Stack

Backend
- Java 21
- Spring Boot

Frontend
- Vue.js
- PrimeVue

Database
- PostgreSQL

Containerization
- Docker

Version Control
- Git

---

## Configuration

The backend application supports configuration overrides via environment variables.

### Environment Variables

| Variable | Description | Local Default |
| --- | --- | --- |
| `DB_URL` | Database JDBC URL | `jdbc:postgresql://localhost:5432/enterprise_asset_management` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Base64-encoded secret key for signing JWTs | `MzRjYjVkNmU3YzhhOWI0ZTEwZjJhM2M1ZTZiN2Q4ZTkxMGExMmIzYzRkNWU2ZjdhOGI5YzBkMWUyZjNhNGI1Yw==` |

---

## License

MIT
