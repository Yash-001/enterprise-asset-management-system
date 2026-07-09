#!/bin/bash
set -euo pipefail

# ===========================================
# EC2 Instance Setup Script (Amazon Linux 2023 / Ubuntu)
# Run once on fresh EC2 instance
# ===========================================

echo "=== EAMS EC2 Setup ==="

# Detect OS
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
else
    echo "Cannot detect OS"
    exit 1
fi

# Update system
echo "[1/7] Updating system packages..."
if [ "$OS" = "amzn" ]; then
    sudo yum update -y
elif [ "$OS" = "ubuntu" ]; then
    sudo apt-get update && sudo apt-get upgrade -y
fi

# Install Docker
echo "[2/7] Installing Docker..."
if [ "$OS" = "amzn" ]; then
    sudo yum install -y docker
    sudo systemctl enable docker
    sudo systemctl start docker
    sudo usermod -aG docker ec2-user
elif [ "$OS" = "ubuntu" ]; then
    sudo apt-get install -y docker.io
    sudo systemctl enable docker
    sudo systemctl start docker
    sudo usermod -aG docker ubuntu
fi

# Install Docker Compose
echo "[3/7] Installing Docker Compose..."
DOCKER_COMPOSE_VERSION="v2.27.0"
sudo curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install AWS CLI (if not present)
echo "[4/7] Installing AWS CLI..."
if ! command -v aws &> /dev/null; then
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
    unzip -q awscliv2.zip
    sudo ./aws/install
    rm -rf aws awscliv2.zip
fi

# Create application directory
echo "[5/7] Setting up application directory..."
sudo mkdir -p /opt/eams/{uploads,logs,backups,nginx}
sudo chown -R $(whoami):$(whoami) /opt/eams

# Install CloudWatch Agent
echo "[6/7] Installing CloudWatch Agent..."
if [ "$OS" = "amzn" ]; then
    sudo yum install -y amazon-cloudwatch-agent
elif [ "$OS" = "ubuntu" ]; then
    wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
    sudo dpkg -i amazon-cloudwatch-agent.deb
    rm amazon-cloudwatch-agent.deb
fi

# Setup swap (useful for t3.small/medium)
echo "[7/7] Configuring swap..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
fi

echo ""
echo "=== Setup Complete ==="
echo "Next steps:"
echo "  1. Copy deploy files to /opt/eams/"
echo "  2. Create .env file: cp .env.example .env && nano .env"
echo "  3. Run: ./deploy/scripts/deploy.sh"
echo ""
echo "NOTE: Log out and back in for Docker group membership to take effect."
