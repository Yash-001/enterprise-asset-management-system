#!/bin/bash
set -euo pipefail

# ===========================================
# Deployment Script
# Usage: ./deploy.sh [image-tag]
# ===========================================

DEPLOY_DIR="/opt/eams"
COMPOSE_FILE="${DEPLOY_DIR}/docker-compose.aws.yml"
ENV_FILE="${DEPLOY_DIR}/.env"
BACKUP_TAG=""
IMAGE_TAG="${1:-latest}"

echo "=== EAMS Deployment ==="
echo "Image tag: ${IMAGE_TAG}"
echo "Deploy dir: ${DEPLOY_DIR}"

cd "${DEPLOY_DIR}"

# Validate .env exists
if [ ! -f "${ENV_FILE}" ]; then
    echo "ERROR: .env file not found at ${ENV_FILE}"
    echo "Copy .env.example and configure it first."
    exit 1
fi

# Source env for variable substitution
set -a
source "${ENV_FILE}"
set +a

# Save current image tag for rollback
BACKUP_TAG=$(docker inspect --format='{{.Config.Image}}' eams-app 2>/dev/null || echo "none")
echo "Current image (for rollback): ${BACKUP_TAG}"

# Pull new image
echo "[1/5] Pulling new image..."
export DOCKER_IMAGE="${DOCKER_IMAGE:-ghcr.io/your-org/enterprise-asset-management-system}:${IMAGE_TAG}"
docker compose -f "${COMPOSE_FILE}" pull app

# Stop old container gracefully
echo "[2/5] Stopping current container..."
docker compose -f "${COMPOSE_FILE}" stop app || true

# Start new container
echo "[3/5] Starting new container..."
docker compose -f "${COMPOSE_FILE}" up -d app

# Wait for health check
echo "[4/5] Waiting for health check..."
RETRIES=0
MAX_RETRIES=30
until [ $RETRIES -ge $MAX_RETRIES ]; do
    if curl -sf http://localhost:8080/management/health > /dev/null 2>&1; then
        echo "Health check PASSED"
        break
    fi
    RETRIES=$((RETRIES + 1))
    echo "  Attempt ${RETRIES}/${MAX_RETRIES}..."
    sleep 5
done

if [ $RETRIES -ge $MAX_RETRIES ]; then
    echo "ERROR: Health check FAILED after ${MAX_RETRIES} attempts"
    echo "Rolling back to: ${BACKUP_TAG}"
    
    if [ "${BACKUP_TAG}" != "none" ]; then
        export DOCKER_IMAGE="${BACKUP_TAG}"
        docker compose -f "${COMPOSE_FILE}" up -d app
        echo "Rollback initiated. Check logs: docker logs eams-app"
    fi
    exit 1
fi

# Restart nginx to pick up changes
echo "[5/5] Reloading Nginx..."
docker compose -f "${COMPOSE_FILE}" exec nginx nginx -s reload 2>/dev/null || true

# Cleanup old images
docker image prune -f --filter "until=72h" 2>/dev/null || true

echo ""
echo "=== Deployment Successful ==="
echo "Image: ${DOCKER_IMAGE}"
echo "Health: $(curl -s http://localhost:8080/management/health | head -c 100)"
echo ""
