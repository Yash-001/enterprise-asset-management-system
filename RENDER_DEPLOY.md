# EAMS — Render Deployment Guide

## Deployment Method: Docker (via render.yaml Blueprint)

---

## Build Command

Render uses the `Dockerfile` at `./backend/Dockerfile`. No separate build command needed.

If deploying as **Native Environment** (non-Docker):
```bash
cd backend && ./mvnw clean package -DskipTests -Pprod
```

---

## Start Command

If deploying as **Native Environment** (non-Docker):
```bash
java -jar backend/target/enterprise-asset-management-1.0.0.jar --spring.profiles.active=prod
```

Docker deployments use the `ENTRYPOINT` in the Dockerfile (no manual start command needed).

---

## Health Check Endpoint

```
GET /management/health
```

Response (healthy):
```json
{"status": "UP"}
```

Configure in Render dashboard: **Health Check Path** = `/management/health`

---

## Production Profile Activation

Set environment variable:
```
SPRING_PROFILES_ACTIVE=prod
```

This activates `application-prod.properties` which:
- Reads database from `SPRING_DATASOURCE_URL`
- Reads JWT from `JWT_SECRET`
- Binds to `PORT` (Render sets this automatically)
- Disables Swagger
- Disables SQL logging
- Enables compression + graceful shutdown

---

## Required Environment Variables

| Variable | Required | Source | Description |
|----------|----------|--------|-------------|
| `SPRING_PROFILES_ACTIVE` | Yes | Manual | Set to `prod` |
| `SPRING_DATASOURCE_URL` | Yes | Render DB link | JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | Yes | Render DB link | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Yes | Render DB link | Database password |
| `JWT_SECRET` | Yes | Auto-generated | Base64-encoded 256-bit key |
| `JWT_EXPIRATION` | No | Manual | Token TTL (default: `12h`) |
| `PORT` | Auto | Render | Server port (Render sets this) |
| `CORS_ALLOWED_ORIGINS` | No | Manual | Frontend URL (e.g., `https://eams.onrender.com`) |
| `MAIL_HOST` | No | Manual | SMTP host |
| `MAIL_PORT` | No | Manual | SMTP port (default: 587) |
| `MAIL_USERNAME` | No | Manual | SMTP username |
| `MAIL_PASSWORD` | No | Manual | SMTP password |
| `MAIL_FROM` | No | Manual | Sender email |
| `STORAGE_PATH` | No | Manual | File upload path (default: `/app/uploads`) |

---

## Deployment Steps

### Option A: Blueprint (Recommended)

1. Push code to GitHub (with `render.yaml` at project root)
2. Go to Render Dashboard → **New** → **Blueprint**
3. Connect your GitHub repo
4. Render reads `render.yaml` and provisions:
   - PostgreSQL database (`eams-db`)
   - Web service (`eams-backend`) with Docker
5. Set `CORS_ALLOWED_ORIGINS` and mail credentials in dashboard
6. Deploy

### Option B: Manual Setup

1. **Create PostgreSQL** in Render (Starter plan)
2. **Create Web Service** → Docker → point to `./backend/Dockerfile`
3. **Add Environment Variables** (see table above)
4. **Link Database** → copy Internal Database URL as `SPRING_DATASOURCE_URL`
5. **Set Health Check** → `/management/health`
6. Deploy

---

## Post-Deployment Verification

```bash
# Health check
curl https://your-app.onrender.com/management/health

# Login (replace with your credentials)
curl -X POST https://your-app.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@eams.com","password":"admin123"}'
```

---

## Troubleshooting

| Issue | Cause | Fix |
|-------|-------|-----|
| App crashes on start | Missing env vars | Check `SPRING_DATASOURCE_URL` and `JWT_SECRET` are set |
| Health check fails | Port mismatch | Ensure `PORT` is not manually overridden |
| DB connection refused | Wrong connection string | Use Render's Internal Database URL (not External) |
| CORS blocked | Missing origin | Set `CORS_ALLOWED_ORIGINS` to your frontend URL |
| 403 on all endpoints | JWT not configured | Verify `JWT_SECRET` is set (non-empty) |
