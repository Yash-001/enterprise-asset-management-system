# AWS Deployment Guide — EAMS

## Architecture Overview

```
Internet → Route 53 → ALB/Nginx (EC2) → Docker Compose App → PostgreSQL (RDS or EC2)
                         ↓
                    Let's Encrypt HTTPS
```

**Components:**
- **EC2 Instance**: t3.medium (2 vCPU, 4GB RAM) — runs Docker Compose (app + Nginx)
- **PostgreSQL**: Amazon RDS (recommended) or self-managed on EC2
- **Nginx**: Reverse proxy with HTTPS (Let's Encrypt / ACM)
- **S3**: Backups storage
- **CloudWatch**: Monitoring and alerting

---

## Prerequisites

- AWS Account with IAM user/role (EC2, RDS, S3, CloudWatch permissions)
- Domain name configured in Route 53 (or external DNS)
- SSH key pair created in AWS Console
- AWS CLI installed locally

---

## Step 1: EC2 Instance Setup

### Launch Instance

| Setting | Value |
|---------|-------|
| AMI | Amazon Linux 2023 or Ubuntu 22.04 LTS |
| Instance Type | t3.medium (prod) / t3.small (staging) |
| Storage | 30GB gp3 (root) + 50GB gp3 (data) |
| Security Group | Ports: 22 (SSH), 80 (HTTP), 443 (HTTPS) |
| Key Pair | Your SSH key |
| IAM Role | Attach role with S3, CloudWatch permissions |

### Security Group Rules

| Type | Port | Source | Purpose |
|------|------|--------|---------|
| SSH | 22 | Your IP / Bastion | Admin access |
| HTTP | 80 | 0.0.0.0/0 | Redirect to HTTPS |
| HTTPS | 443 | 0.0.0.0/0 | Application traffic |
| PostgreSQL | 5432 | EC2 SG only | DB access (if self-managed) |

---

## Step 2: PostgreSQL Setup

### Option A: Amazon RDS (Recommended)

| Setting | Value |
|---------|-------|
| Engine | PostgreSQL 16 |
| Instance | db.t3.medium |
| Storage | 50GB gp3 with autoscaling |
| Multi-AZ | Yes (production) |
| Backup | 7-day automated retention |
| Security Group | Allow from EC2 SG only |

### Option B: Self-Managed on EC2

PostgreSQL runs in the Docker Compose stack. Use the `postgres` service defined in `docker-compose.prod.yml`.

---

## Step 3: Domain & HTTPS

### Option A: Let's Encrypt (Nginx on EC2)

1. Point domain A record to EC2 Elastic IP
2. Nginx handles SSL termination with Certbot
3. Auto-renewal via cron

### Option B: AWS Certificate Manager + ALB

1. Request certificate in ACM
2. Attach to Application Load Balancer
3. ALB terminates SSL, forwards HTTP to EC2:8080

---

## Step 4: Deployment Process

```bash
# SSH into EC2
ssh -i your-key.pem ec2-user@your-ec2-ip

# Run setup script (first time only)
./deploy/scripts/setup-ec2.sh

# Deploy application
./deploy/scripts/deploy.sh

# Verify
curl https://your-domain.com/management/health
```

---

## Step 5: Backups

| What | How | Frequency | Retention |
|------|-----|-----------|-----------|
| Database | pg_dump → S3 | Daily 2 AM UTC | 30 days |
| Uploads | Sync to S3 | Daily 3 AM UTC | 90 days |
| RDS Snapshots | Automated | Daily | 7 days |

---

## Step 6: Monitoring

| Metric | Tool | Alert Threshold |
|--------|------|-----------------|
| CPU | CloudWatch | > 80% for 5 min |
| Memory | CloudWatch Agent | > 85% for 5 min |
| Disk | CloudWatch Agent | > 80% used |
| App Health | CloudWatch + cron | HTTP != 200 |
| DB Connections | RDS Metrics | > 80% max |
| Response Time | CloudWatch Logs | p95 > 2s |

---

## Step 7: Rollback Strategy

### Automatic Rollback
- Health check fails after deploy → previous image is restored
- Script keeps last 3 Docker images tagged

### Manual Rollback
```bash
# List available versions
docker images ghcr.io/your-org/eams --format "{{.Tag}}"

# Rollback to specific version
./deploy/scripts/rollback.sh <version-tag>
```

### Database Rollback
- Flyway forward-only (no destructive migrations)
- For emergencies: restore from RDS snapshot or S3 backup

---

## Environment Variables (Production)

Set in `/opt/eams/.env` on EC2:

```bash
# Required
DB_URL=jdbc:postgresql://your-rds-endpoint:5432/enterprise_asset_management
DB_USERNAME=eams_user
DB_PASSWORD=<secure-password>
JWT_SECRET=<base64-encoded-256-bit-key>

# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
STORAGE_PATH=/opt/eams/uploads

# Mail
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# AWS
AWS_REGION=us-east-1
S3_BACKUP_BUCKET=eams-backups
```

---

## Cost Estimate (Monthly)

| Resource | Spec | Estimated Cost |
|----------|------|----------------|
| EC2 | t3.medium (on-demand) | ~$30 |
| RDS | db.t3.medium (single-AZ) | ~$50 |
| EBS | 80GB gp3 | ~$7 |
| S3 | 10GB backups | ~$1 |
| Data Transfer | 50GB/month | ~$5 |
| **Total** | | **~$93/month** |

Reserved instances reduce EC2/RDS cost by ~40%.
