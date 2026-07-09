#!/bin/bash
set -euo pipefail

# ===========================================
# Database & Uploads Backup Script
# Schedule via cron: 0 2 * * * /opt/eams/deploy/scripts/backup.sh
# ===========================================

DEPLOY_DIR="/opt/eams"
BACKUP_DIR="${DEPLOY_DIR}/backups"
S3_BUCKET="${S3_BACKUP_BUCKET:-eams-backups}"
RETENTION_DAYS=30
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Source env for DB credentials
set -a
source "${DEPLOY_DIR}/.env"
set +a

echo "=== EAMS Backup — ${TIMESTAMP} ==="

mkdir -p "${BACKUP_DIR}"

# ============================
# Database Backup
# ============================
echo "[1/3] Backing up database..."
DB_BACKUP_FILE="${BACKUP_DIR}/db_${TIMESTAMP}.sql.gz"

if docker ps --format '{{.Names}}' | grep -q eams-postgres; then
    # Self-hosted PostgreSQL in Docker
    docker exec eams-postgres pg_dump -U "${DB_USERNAME}" "${DB_NAME:-enterprise_asset_management}" | gzip > "${DB_BACKUP_FILE}"
else
    # RDS — use pg_dump with host
    DB_HOST=$(echo "${DB_URL}" | sed -n 's|.*://\([^:]*\):.*|\1|p')
    DB_PORT=$(echo "${DB_URL}" | sed -n 's|.*:\([0-9]*\)/.*|\1|p')
    DB_DBNAME=$(echo "${DB_URL}" | sed -n 's|.*/\(.*\)|\1|p')
    PGPASSWORD="${DB_PASSWORD}" pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USERNAME}" "${DB_DBNAME}" | gzip > "${DB_BACKUP_FILE}"
fi

echo "  Database backup: ${DB_BACKUP_FILE} ($(du -h ${DB_BACKUP_FILE} | cut -f1))"

# ============================
# Uploads Backup
# ============================
echo "[2/3] Backing up uploads..."
UPLOADS_BACKUP_FILE="${BACKUP_DIR}/uploads_${TIMESTAMP}.tar.gz"

if [ -d "${DEPLOY_DIR}/uploads" ] && [ "$(ls -A ${DEPLOY_DIR}/uploads 2>/dev/null)" ]; then
    tar -czf "${UPLOADS_BACKUP_FILE}" -C "${DEPLOY_DIR}" uploads/
    echo "  Uploads backup: ${UPLOADS_BACKUP_FILE} ($(du -h ${UPLOADS_BACKUP_FILE} | cut -f1))"
else
    echo "  No uploads to backup"
fi

# ============================
# Upload to S3
# ============================
echo "[3/3] Uploading to S3..."
if command -v aws &> /dev/null; then
    aws s3 cp "${DB_BACKUP_FILE}" "s3://${S3_BUCKET}/database/" --quiet
    if [ -f "${UPLOADS_BACKUP_FILE}" ]; then
        aws s3 cp "${UPLOADS_BACKUP_FILE}" "s3://${S3_BUCKET}/uploads/" --quiet
    fi
    echo "  Uploaded to s3://${S3_BUCKET}/"
else
    echo "  WARN: AWS CLI not found. Backups stored locally only."
fi

# ============================
# Cleanup old local backups
# ============================
find "${BACKUP_DIR}" -type f -mtime +${RETENTION_DAYS} -delete
echo "  Cleaned local backups older than ${RETENTION_DAYS} days"

# Cleanup old S3 backups (lifecycle policy recommended instead)
if command -v aws &> /dev/null; then
    CUTOFF_DATE=$(date -d "-${RETENTION_DAYS} days" +%Y-%m-%d 2>/dev/null || date -v-${RETENTION_DAYS}d +%Y-%m-%d)
    aws s3 ls "s3://${S3_BUCKET}/database/" | while read -r line; do
        FILE_DATE=$(echo "$line" | awk '{print $1}')
        if [[ "${FILE_DATE}" < "${CUTOFF_DATE}" ]]; then
            FILE_NAME=$(echo "$line" | awk '{print $4}')
            aws s3 rm "s3://${S3_BUCKET}/database/${FILE_NAME}" --quiet
        fi
    done
fi

echo ""
echo "=== Backup Complete ==="
