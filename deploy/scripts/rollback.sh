#!/bin/bash
set -euo pipefail

# ===========================================
# Rollback Script
# Usage: ./rollback.sh <image-tag>
# ===========================================

DEPLOY_DIR="/opt/eams"
COMPOSE_FILE="${DEPLOY_DIR}/docker-compose.aws.yml"
ENV_FILE="${DEPLOY_DIR}/.env"
ROLLBACK_TAG="${1:-}"

if [ -z "${ROLLBACK_TAG}" ]; then
    echo "Usage: ./rollback.sh <image-tag>"
    echo ""
    echo "Available tags:"
    docker images "ghcr.io/your-org/enterprise-asset-management-system" --format "  {{.Tag}}  ({{.CreatedAt}})"
    exit 1
fi

echo "=== EAMS Rollback ==="
echo "Rolling back to: ${ROLLBACK_TAG}"

cd "${DEPLOY_DIR}"

# Source env
set -a
source "${ENV_FILE}"
set +a

# Set rollback image
export DOCKER_IMAGE="ghcr.io/your-org/enterprise-asset-management-system:${ROLLBACK_TAG}"

# Stop current
echo "[1/3] Stopping current container..."
docker compose -f "${COMPOSE_FILE}" stop app || true

# Start with rollback image
echo "[2/3] Starting rollback container..."
docker compose -f "${COMPOSE_FILE}" up -d app

# Health check
echo "[3/3] Verifying health..."
RETRIES=0
MAX_RETRIES=20
until [ $RETRIES -ge $MAX_RETRIES ]; do
    if curl -sf http://localhost:8080/management/health > /dev/null 2>&1; then
        echo "Health check PASSED"
        break
    fi
    RETRIES=$((RETRIES + 1))
    sleep 5
done

if [ $RETRIES -ge $MAX_RETRIES ]; then
    echo "ERROR: Rollback health check FAILED"
    echo "Manual intervention required. Check: docker logs eams-app"
    exit 1
fi

echo ""
echo "=== Rollback Successful ==="
echo "Running: ${DOCKER_IMAGE}"
