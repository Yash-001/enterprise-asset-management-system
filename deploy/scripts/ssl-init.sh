#!/bin/bash
set -euo pipefail

# ===========================================
# Let's Encrypt SSL Certificate Setup
# Usage: ./ssl-init.sh your-domain.com your-email@example.com
# ===========================================

DOMAIN="${1:-}"
EMAIL="${2:-}"

if [ -z "${DOMAIN}" ] || [ -z "${EMAIL}" ]; then
    echo "Usage: ./ssl-init.sh <domain> <email>"
    echo "Example: ./ssl-init.sh eams.example.com admin@example.com"
    exit 1
fi

DEPLOY_DIR="/opt/eams"
COMPOSE_FILE="${DEPLOY_DIR}/docker-compose.aws.yml"

echo "=== SSL Certificate Setup ==="
echo "Domain: ${DOMAIN}"
echo "Email: ${EMAIL}"

cd "${DEPLOY_DIR}"

# Update nginx config with actual domain
sed -i "s/\${DOMAIN_NAME}/${DOMAIN}/g" "${DEPLOY_DIR}/nginx/nginx.conf"

# Step 1: Start nginx with HTTP only (for ACME challenge)
echo "[1/4] Starting Nginx (HTTP only)..."

# Create temporary nginx config for initial cert
cat > /tmp/nginx-init.conf << EOF
server {
    listen 80;
    server_name ${DOMAIN};
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }
    location / {
        return 200 'EAMS SSL Setup';
    }
}
EOF

docker run -d --name nginx-init \
    -p 80:80 \
    -v /tmp/nginx-init.conf:/etc/nginx/conf.d/default.conf:ro \
    -v eams_certbot_www:/var/www/certbot \
    nginx:alpine

sleep 3

# Step 2: Request certificate
echo "[2/4] Requesting certificate from Let's Encrypt..."
docker run --rm \
    -v eams_certbot_conf:/etc/letsencrypt \
    -v eams_certbot_www:/var/www/certbot \
    certbot/certbot certonly \
    --webroot \
    --webroot-path=/var/www/certbot \
    --email "${EMAIL}" \
    --agree-tos \
    --no-eff-email \
    -d "${DOMAIN}"

# Step 3: Cleanup init container
echo "[3/4] Cleaning up..."
docker stop nginx-init && docker rm nginx-init

# Step 4: Start full stack
echo "[4/4] Starting full stack with SSL..."
docker compose -f "${COMPOSE_FILE}" up -d

echo ""
echo "=== SSL Setup Complete ==="
echo "Your site is now available at: https://${DOMAIN}"
echo ""
echo "Certificate auto-renewal is handled by the certbot container."
