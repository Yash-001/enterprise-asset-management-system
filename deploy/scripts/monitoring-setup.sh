#!/bin/bash
set -euo pipefail

# ===========================================
# CloudWatch Monitoring Setup
# Run after setup-ec2.sh
# ===========================================

DEPLOY_DIR="/opt/eams"

echo "=== CloudWatch Monitoring Setup ==="

# Configure CloudWatch Agent
echo "[1/3] Configuring CloudWatch Agent..."
sudo cp "${DEPLOY_DIR}/deploy/cloudwatch/cloudwatch-agent-config.json" /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -s \
    -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

# Setup health check cron
echo "[2/3] Setting up health check cron..."
HEALTH_CHECK_SCRIPT="${DEPLOY_DIR}/deploy/scripts/health-check.sh"
cat > "${HEALTH_CHECK_SCRIPT}" << 'EOF'
#!/bin/bash
HEALTH_URL="http://localhost:8080/management/health"
RESPONSE=$(curl -sf -o /dev/null -w "%{http_code}" "${HEALTH_URL}" 2>/dev/null || echo "000")

if [ "${RESPONSE}" != "200" ]; then
    # Send CloudWatch custom metric
    aws cloudwatch put-metric-data \
        --namespace "EAMS" \
        --metric-name "HealthCheckFailed" \
        --value 1 \
        --unit Count \
        --region "${AWS_REGION:-us-east-1}" 2>/dev/null || true
    
    echo "$(date) HEALTH CHECK FAILED (HTTP ${RESPONSE})" >> /opt/eams/logs/health-check.log
else
    aws cloudwatch put-metric-data \
        --namespace "EAMS" \
        --metric-name "HealthCheckFailed" \
        --value 0 \
        --unit Count \
        --region "${AWS_REGION:-us-east-1}" 2>/dev/null || true
fi
EOF
chmod +x "${HEALTH_CHECK_SCRIPT}"

# Setup cron jobs
echo "[3/3] Setting up cron jobs..."
CRON_FILE="/tmp/eams-cron"
cat > "${CRON_FILE}" << EOF
# EAMS Health Check (every 2 minutes)
*/2 * * * * ${DEPLOY_DIR}/deploy/scripts/health-check.sh

# EAMS Database Backup (daily at 2 AM UTC)
0 2 * * * ${DEPLOY_DIR}/deploy/scripts/backup.sh >> /opt/eams/logs/backup.log 2>&1

# Docker cleanup (weekly)
0 4 * * 0 docker system prune -f --volumes --filter "until=168h" >> /opt/eams/logs/docker-cleanup.log 2>&1
EOF

crontab "${CRON_FILE}"
rm "${CRON_FILE}"

echo ""
echo "=== Monitoring Setup Complete ==="
echo "CloudWatch Agent: Running"
echo "Health Check: Every 2 minutes"
echo "Backup: Daily at 2 AM UTC"
echo ""
echo "Create CloudWatch Alarms in AWS Console:"
echo "  - EAMS/HealthCheckFailed > 0 for 5 min → SNS alert"
echo "  - EAMS/cpu_usage_idle < 20 for 5 min → SNS alert"
echo "  - EAMS/mem_used_percent > 85 for 5 min → SNS alert"
echo "  - EAMS/disk_used_percent > 80 → SNS alert"
