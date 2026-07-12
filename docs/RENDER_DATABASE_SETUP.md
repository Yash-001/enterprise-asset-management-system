# Render PostgreSQL — Enterprise Production Deployment Guide

## Project Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Backend | Spring Boot | 3.5 |
| Language | Java | 21 |
| Database | PostgreSQL | 16 (Render) / 17 (local dev) |
| Migrations | Flyway | Managed by Spring Boot |
| Auth | JWT (jjwt) | 0.12.6 |
| Hosting | Render | Free / Starter |
| Frontend (future) | Vue 3 on Vercel | — |
| Migration target | AWS RDS + ECS | — |

---

## PostgreSQL Version Selection

Render currently supports PostgreSQL **14, 15, and 16**. PostgreSQL 17 is not yet available on Render (as of mid-2026).

**Recommendation:** Use **PostgreSQL 16** on Render.

**Why this is acceptable:**
- Your local development uses PostgreSQL 17, but there are no breaking changes between 16 and 17 that affect Spring Boot/Flyway/Hibernate.
- All SQL features used by EAMS (indexes, sequences, enums, constraints) are identical across 16-17.
- When you migrate to AWS RDS (which supports PostgreSQL 17), upgrading is a zero-code-change operation.
- Flyway migrations are forward-compatible.

---

## Step 1: Create PostgreSQL on Render

1. Login to [dashboard.render.com](https://dashboard.render.com)
2. Click **New +** (top right) → **PostgreSQL**
3. Fill in the configuration form (see Step 2)
4. Click **Create Database**
5. Wait 1-3 minutes for provisioning (status: Creating → Available)

---

## Step 2: Configuration Options

| Field | Recommended Value | Explanation |
|-------|-------------------|-------------|
| **Name** | `eams-db` | Dashboard identifier. Used for linking to services. Not the actual database name. |
| **Database** | `enterprise_asset_management` | The actual PostgreSQL database name. Appears in your JDBC URL path. |
| **User** | `eams_user` | PostgreSQL role created with ownership. Avoid `postgres` superuser in production. |
| **Region** | `Oregon (US West)` | Must match your backend Web Service region for Internal URL access. |
| **PostgreSQL Version** | `16` | Latest available on Render. Compatible with all project dependencies. |
| **Plan** | `Free` (demo) or `Starter` (prod) | See limitations table below. |

---

## Step 3: Free Tier Limitations

| Aspect | Free | Starter ($7/mo) |
|--------|------|-----------------|
| Storage | 1 GB | 1 GB (expandable) |
| Expiration | **Deleted after 90 days of inactivity** | Persistent (no expiration) |
| Max Connections | 97 | 97 |
| Backups | None | Daily automated |
| Performance | Shared CPU, limited IOPS | Dedicated resources |
| High Availability | No | No (Pro plan only) |
| IP Allowlist | All IPs | Configurable |

**For EAMS:**
- Free is acceptable for demo/staging environments.
- Starter is recommended for production data you cannot afford to lose.
- The 97 connection limit is well above your HikariCP `max-pool-size=10`.

---

## Step 4: Retrieve Connection Values

After creation, click the database → **Connections** panel provides:

| Value | Example | Purpose |
|-------|---------|---------|
| **Hostname** | `dpg-abc123xyz-a.oregon-postgres.render.com` | External hostname |
| **Port** | `5432` | Standard PostgreSQL port |
| **Database** | `enterprise_asset_management` | Database name |
| **Username** | `eams_user` | Auth role |
| **Password** | `aB3xY7kL9mN2pQ...` | Auto-generated (keep secret) |
| **Internal Database URL** | `postgres://eams_user:pass@dpg-abc123xyz-a/enterprise_asset_management` | Render → Render (private network) |
| **External Database URL** | `postgres://eams_user:pass@dpg-abc123xyz-a.oregon-postgres.render.com/enterprise_asset_management` | Outside Render (requires SSL) |

---

## Step 5: Internal URL vs External URL

| Type | When to Use | SSL Required | Performance |
|------|-------------|--------------|-------------|
| **Internal URL** | Backend Web Service → Database (both on Render, same region) | No | Fast (private network) |
| **External URL** | Local machine, external tools, different cloud, CI/CD pipelines | Yes (`?sslmode=require`) | Slower (public internet) |

**Production rule:** Always use Internal URL for your Render backend → Render database connection. It's faster, more secure (not exposed to internet), and doesn't require SSL configuration.

**AWS migration note:** When migrating to AWS, you'll switch to an RDS endpoint (same concept: private VPC connection).

---

## Step 6: Render Environment Variables

Render exposes individual connection components as environment variables when you link a database to a service:

| Render Variable | Value | Maps To |
|-----------------|-------|---------|
| `DATABASE_URL` | `postgres://user:pass@host/dbname` | Full connection string (postgres:// format) |
| `PGHOST` | `dpg-abc123xyz-a` | Hostname only |
| `PGPORT` | `5432` | Port only |
| `PGDATABASE` | `enterprise_asset_management` | Database name only |
| `PGUSER` | `eams_user` | Username only |
| `PGPASSWORD` | `aB3xY7kL9mN2pQ...` | Password only |

### The JDBC Conversion Problem

Render provides `DATABASE_URL` in `postgres://` format:
```
postgres://eams_user:password@dpg-abc123xyz-a:5432/enterprise_asset_management
```

Spring Boot requires `jdbc:postgresql://` format:
```
jdbc:postgresql://dpg-abc123xyz-a:5432/enterprise_asset_management
```

### Recommended Solution

Use the individual `PG*` variables instead of converting the URL:

```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://${PGHOST:localhost}:${PGPORT:5432}/${PGDATABASE:enterprise_asset_management}
spring.datasource.username=${PGUSER:postgres}
spring.datasource.password=${PGPASSWORD:postgres}
```

**Why this approach:**
- No URL parsing/conversion needed
- Each component is individually testable
- Works identically on Render, AWS RDS, and local development
- Spring Boot resolves `${VAR:default}` at startup — defaults enable local dev without changes

**Alternative (if you must use DATABASE_URL):**

If Render only provides `DATABASE_URL`, set a custom env var manually:
```
DATABASE_URL_JDBC=jdbc:postgresql://dpg-abc123xyz-a:5432/enterprise_asset_management
```

Then reference:
```properties
spring.datasource.url=${DATABASE_URL_JDBC}
```

---

## Step 7: Production HikariCP Settings

```properties
spring.datasource.hikari.minimum-idle=3
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.leak-detection-threshold=60000
spring.datasource.hikari.pool-name=EamsHikariPool
```

| Setting | Value | Why |
|---------|-------|-----|
| `minimum-idle=3` | 3 connections kept warm | Reduces cold-start latency for first requests. Low enough to not waste Render's 97 connection limit. |
| `maximum-pool-size=10` | Max 10 connections | Render Free allows 97 total. Leaving headroom for Flyway, monitoring, and potential horizontal scaling. Rule of thumb: `pool_size = (core_count * 2) + disk_spindles`. For Render shared CPU: 10 is safe. |
| `connection-timeout=20000` | 20 seconds | Max wait for a connection from pool. Longer than default (30s is too generous for user-facing requests). |
| `idle-timeout=300000` | 5 minutes | Connections idle > 5min are returned to the pool. Frees resources on shared infrastructure. |
| `max-lifetime=600000` | 10 minutes | Max connection age before forced recycle. Prevents stale connections to Render's managed PostgreSQL. Shorter than Render's server-side timeout. |
| `leak-detection-threshold=60000` | 60 seconds | Logs warning if a connection isn't returned within 60s. Catches connection leaks before they exhaust the pool. |

**AWS migration note:** These settings work identically with RDS. Increase `maximum-pool-size` to 20-50 on dedicated RDS instances.

---

## Step 8: Flyway Production Configuration

```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.clean-disabled=true
spring.flyway.validate-on-migrate=true
spring.flyway.out-of-order=false
spring.jpa.hibernate.ddl-auto=validate
```

| Setting | Value | Why |
|---------|-------|-----|
| `flyway.enabled=true` | Migrations run automatically on startup | No manual SQL execution needed. Schema is version-controlled. |
| `baseline-on-migrate=true` | Creates baseline on first deploy | Allows Flyway to start tracking on an empty database. After first deploy, this is a no-op. |
| `clean-disabled=true` | **Critical safety.** Prevents `flyway clean` from dropping all tables. | Without this, an accidental `clean` destroys production data. |
| `validate-on-migrate=true` | Verifies migration checksums haven't been modified | Detects if someone altered a previously-applied migration. Fails fast instead of applying corrupted state. |
| `out-of-order=false` | Migrations must be applied in order | Prevents V3 from running before V2. Ensures deterministic schema state. |
| `ddl-auto=validate` | Hibernate only validates, never modifies schema | **Never use `update`, `create`, or `create-drop` in production.** These can silently alter tables, drop constraints, or cause data loss. Flyway owns schema changes exclusively. |

**Why `ddl-auto=update` is dangerous in production:**
- May drop columns if entity fields are renamed
- Cannot handle complex migrations (data transforms, column type changes)
- Not idempotent — different results depending on current state
- No rollback capability
- Not auditable

---

## Step 9: JWT Secret Generation

### Generate a Secure Secret

```bash
openssl rand -base64 64
```

This produces a 64-byte (512-bit) random key, Base64-encoded (88 characters).

**Example output:**
```
k7G9mN2xP4qR8vB1cF5hJ0lT3wA6yD9eK2nM7pS4uX1bE8gI5jL0oQ3rV6tW9zA4dF7hK2mN5pR8sU1vB4cX=
```

### Requirements

| Aspect | Requirement |
|--------|-------------|
| **Minimum length** | 256 bits (32 bytes) — required by HMAC-SHA256 |
| **Recommended length** | 512 bits (64 bytes) — future-proof for HS384/HS512 |
| **Encoding** | Base64 (your `JwtTokenProvider` decodes with `Decoders.BASE64`) |
| **Storage** | Only in Render Environment Variables — never in code, config files, or git |
| **Rotation** | Generate a new secret per environment (dev ≠ staging ≠ prod) |

### Set in Render

Render Dashboard → Web Service → **Environment** → Add:
```
JWT_SECRET=k7G9mN2xP4qR8vB1cF5hJ0lT3wA6yD9eK2nM7pS4uX1bE8gI5jL0oQ3rV6tW9zA4dF7hK2mN5pR8sU1vB4cX=
```

**Never:**
- Commit to git
- Log in application output
- Use the same secret across environments
- Use short or predictable values

---

## Step 10: CORS Configuration

```properties
eams.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

### Production Setup

Set in Render environment variables:
```
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

For multiple origins (e.g., Vercel preview deployments):
```
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app,https://your-app-git-main.vercel.app
```

### Security Warning

**Never use `*` (wildcard) in production with `allowCredentials=true`.**

Why:
- Allows any website to make authenticated requests to your API
- Browsers block `Access-Control-Allow-Origin: *` when credentials (cookies/Authorization header) are sent
- Even if the browser blocks it, the request still reaches your server
- Your API is a target for CSRF-like attacks from malicious sites

**Correct production pattern:**
- Specify exact Vercel domain(s)
- Update `CORS_ALLOWED_ORIGINS` when frontend URL changes
- Include Vercel preview URLs only if needed for QA

---

## Step 11: Actuator Configuration

```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
management.endpoint.health.probes.enabled=true
management.endpoints.web.base-path=/management
```

### Production Security Recommendations

| Setting | Value | Why |
|---------|-------|-----|
| Expose only `health` | `include=health` | Minimizes attack surface. `metrics`, `env`, `beans` expose internal details. |
| Hide health details | `show-details=never` | Prevents leaking database connection status, disk space, etc. to unauthenticated users. |
| Enable probes | `probes.enabled=true` | Provides `/management/health/liveness` and `/management/health/readiness` for Render's health checks. |
| Custom base path | `/management` | Avoids the default `/actuator` path which is commonly scanned by bots. |

**Render Health Check:** Set to `/management/health` in your Web Service settings.

**AWS migration note:** When moving to ECS, use `/management/health/readiness` for ALB target group health checks.

---

## Step 12: Set Environment Variables on Render

Navigate to: Render Dashboard → Web Service → **Environment** tab

| Variable | Value | Required |
|----------|-------|----------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Yes |
| `PGHOST` | *(from database Connections panel)* | Yes |
| `PGPORT` | `5432` | Yes |
| `PGDATABASE` | `enterprise_asset_management` | Yes |
| `PGUSER` | `eams_user` | Yes |
| `PGPASSWORD` | *(from database Connections panel)* | Yes |
| `JWT_SECRET` | *(output of `openssl rand -base64 64`)* | Yes |
| `JWT_EXPIRATION` | `12h` | No (defaults to 12h) |
| `CORS_ALLOWED_ORIGINS` | `https://your-app.vercel.app` | Yes |
| `MAIL_HOST` | *(your SMTP host)* | No |
| `MAIL_USERNAME` | *(your SMTP user)* | No |
| `MAIL_PASSWORD` | *(your SMTP password)* | No |

---

## Step 13: What Happens on First Deploy

```
Docker builds → JVM starts → Spring Boot initializes →
  → HikariCP connects to Render PostgreSQL (via PGHOST)
  → Flyway detects empty database
  → Flyway applies V1__ through V17__ migrations
  → Hibernate validates entity mappings against schema
  → Application binds to PORT
  → Render health check confirms /management/health → UP
  → Service marked as Live
```

No manual SQL, no manual schema creation. Everything is automated.

---

## Production Security Checklist

```
[ ] Secrets stored ONLY in Render Environment Variables (not in code/git)
[ ] HTTPS enabled (Render provides this automatically for *.onrender.com)
[ ] SPRING_PROFILES_ACTIVE=prod (verified — no dev/local profile active)
[ ] spring.flyway.enabled=true (migrations run automatically)
[ ] spring.jpa.hibernate.ddl-auto=validate (Hibernate never modifies schema)
[ ] No localhost URLs in production config
[ ] JWT_SECRET configured (min 256-bit, Base64-encoded)
[ ] CORS_ALLOWED_ORIGINS set to actual frontend URL (not "*")
[ ] Health endpoint verified: /management/health returns {"status":"UP"}
[ ] spring.flyway.clean-disabled=true (prevents accidental data loss)
[ ] spring.jpa.show-sql=false (no SQL in production logs)
[ ] springdoc.swagger-ui.enabled=false (Swagger disabled in production)
[ ] actuator exposes ONLY health (no env/beans/metrics exposed)
[ ] Database password not logged or exposed in error messages
```

---

## Troubleshooting

### Flyway Migration Failures

| Symptom | Cause | Fix |
|---------|-------|-----|
| `FlywayException: Validate failed` | A previously-applied migration was modified | Never edit applied migrations. Create a new `V18__` migration instead. |
| `FlywayException: Found non-empty schema without schema history table` | Database has tables but no `flyway_schema_history` | Set `baseline-on-migrate=true` (already configured). |
| Migration fails with SQL error | Syntax error or constraint violation in migration | Fix the SQL in the migration file. If already partially applied, manually repair via `flyway_schema_history`. |

### Database Connection Failures

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Connection refused` | Wrong host or port | Verify `PGHOST` uses Internal hostname (no `.oregon-postgres.render.com` suffix for Render-to-Render). |
| `FATAL: password authentication failed` | Wrong password | Re-copy from Render DB Connections panel. |
| `FATAL: database does not exist` | Wrong `PGDATABASE` value | Verify exact spelling: `enterprise_asset_management`. |
| `SSL connection required` | Using External URL without SSL | Add `?sslmode=require` to URL, or switch to Internal URL. |
| `HikariPool: Connection is not available` | Pool exhausted | Reduce `maximum-pool-size` or check for connection leaks. |

### Invalid JDBC URL

| Symptom | Cause | Fix |
|---------|-------|-----|
| `No suitable driver found` | URL starts with `postgres://` instead of `jdbc:postgresql://` | Use individual `PG*` vars instead of `DATABASE_URL`. |
| `Invalid URL format` | Missing `jdbc:` prefix | Ensure your properties compose: `jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}` |

### JWT Startup Failures

| Symptom | Cause | Fix |
|---------|-------|-----|
| `JWT Secret key must be at least 256 bits` | Secret too short or not Base64-encoded | Generate with `openssl rand -base64 64`. |
| `IllegalArgumentException: Unable to decode` | Secret contains invalid Base64 characters | Regenerate. Ensure no trailing newline was copied. |
| `JWT_SECRET not set` | Missing env var | Add to Render Environment Variables. |

### HikariCP Pool Issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Connection is not available, request timed out` | All connections in use | Check for leaks (`leak-detection-threshold`). Increase `maximum-pool-size` if legitimate load. |
| `Connection marked as broken` | Server closed connection (max-lifetime exceeded on server side) | Reduce `max-lifetime` to below PostgreSQL's `idle_in_transaction_session_timeout`. |
| Slow first request after idle | Cold pool (minimum-idle = 0) | Set `minimum-idle=3` to keep warm connections. |

### CORS Failures

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Access-Control-Allow-Origin` missing from response | Origin not in allowed list | Add exact Vercel URL to `CORS_ALLOWED_ORIGINS`. |
| Preflight (OPTIONS) returns 403 | Spring Security blocks OPTIONS | Verify `.cors(Customizer.withDefaults())` is in `SecurityFilterChain`. |
| `Credentials flag is true but Access-Control-Allow-Origin is *` | Invalid combination | Never use `*` with `allowCredentials=true`. Use specific origins. |

---

## Verification After Deployment

```bash
# 1. Health check
curl https://your-app.onrender.com/management/health
# Expected: {"status":"UP"}

# 2. Login
curl -X POST https://your-app.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Admin","lastName":"User","email":"admin@eams.com","password":"Admin@123"}'

# 3. Promote to ADMIN (via database)
# Connect to Render External URL:
psql "postgres://eams_user:PASS@HOST.oregon-postgres.render.com:5432/enterprise_asset_management?sslmode=require"
# Then:
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@eams.com';

# 4. Verify login works
curl -X POST https://your-app.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@eams.com","password":"Admin@123"}'
# Expected: {"accessToken":"eyJ...","tokenType":"Bearer","expiresIn":...}
```

---

## AWS Migration Readiness

This configuration is designed for zero-code-change migration to AWS:

| Render | AWS Equivalent | Migration |
|--------|---------------|-----------|
| Render PostgreSQL | Amazon RDS PostgreSQL 17 | Change `PGHOST` to RDS endpoint |
| Render Web Service (Docker) | ECS Fargate / Elastic Beanstalk | Same Docker image |
| Render Environment Variables | AWS Systems Manager Parameter Store / Secrets Manager | Same variable names |
| Internal Database URL | RDS VPC Private Subnet | Same concept |
| `/management/health` | ALB Target Group Health Check | Same path |
| HikariCP pool-size=10 | Increase to 20-50 on dedicated RDS | Only config change |

When migrating:
1. Create RDS PostgreSQL 17 instance
2. `pg_dump` from Render → `pg_restore` to RDS
3. Update `PGHOST`, `PGUSER`, `PGPASSWORD` in ECS task definition
4. Deploy same Docker image to ECS
5. Done — no code changes
