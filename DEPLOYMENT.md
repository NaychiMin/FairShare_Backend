# Continuous Deployment Setup Guide

### Step 1: Create instance (Im using digital ocean)
1. Go to DigitalOcean → Create → Droplets
2. Choose:
- Image: Ubuntu (latest LTS)
- Plan: Basic ($4–$6/month is enough)
- CPU: Shared
3. Authentication: Choose SSH Key (recommended)
4. Public IP will be obtained

### Step 2: Set Up Your Server (Ubuntu OS)

After creating your instance:

1. ssh root@YOUR_DROPLET_IP
2. Only if you saved your ssh key in anoother location: ssh -i "{key_directory}" root@YOUR_DROPLET_IP

```bash
# Update system (can just ignore sudo)
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add your user to docker group (so you don't need sudo)
sudo usermod -aG docker $USER
newgrp docker

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installations
docker --version
docker-compose --version

# Create deployment directory
mkdir -p ~/fairshare-backend-deployment
cd ~/fairshare-backend-deployment

# Clone your repo
git clone https://github.com/NaychiMin/FairShare_Backend.git .
```

### Step 3: Configure GitHub Secrets

In your GitHub repository, go to **Settings > Secrets and variables > Actions** and add:

**For Dev Environment:**
```
DEV_SERVER_HOST           = your-dev-server-public-ip
DEV_SERVER_USER           = ubuntu (or your username)
DEV_SERVER_SSH_KEY        = (your private SSH key) (cat {})
```

### Step 4: Set Up Environment Variables on Server

Create `.env.dev` files on your server:

```bash
# On your server, in ~/fairshare-deployment/

# .env.dev
cat > .env.dev << 'EOF'
MYSQL_ROOT_PASSWORD=dev_root_pass_123
MYSQL_USER=fairshare
MYSQL_PASSWORD=fairshare_dev_pass
MYSQL_DATABASE=fairshare
COMPOSE_PROJECT_NAME=fairshare-dev
EOF

# Keep these files secure
chmod 600 .env.dev
```

```bash
# Add swap to prevent mysql from crashing/restarting

sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### Step 5: First Manual Deployment (Test)

On your server:

```bash
cd ~/fairshare-deployment

# Log in to GitHub Container Registry
echo "your_github_token_here" | docker login ghcr.io -u your-username --password-stdin

# Pull and start dev
docker-compose --env-file .env.dev -f docker-compose.dev.yml pull
docker-compose --env-file .env.dev -f docker-compose.dev.yml up -d

# Check status
docker ps
docker ps -a

# View logs
docker-compose -f docker-compose.dev.yml logs -f backend

# Check logs from your past session
docker-compose -f docker-compose.dev.yml logs --tail=50 backend
```

### Step 6: Check Database Connection

```bash
# Connect to MySQL inside the container
docker exec -it fairshare-mysql-dev mysql -u fairshare -p fairshare

# Once logged in, check tables
SHOW TABLES;
SELECT COUNT(*) FROM tb_role;
SELECT COUNT(*) FROM tb_badge;
EXIT;
```

### Step 7: Fix CORS policy in backend (Add in ip address of our server)
1. src/main/java/.../config/CorsConfig.java
2. SecurityConfig.java
3. WebSocketConfig.java

### Troubleshooting Common Issues

**Issue: Backend container won't start**
```bash
# View full error logs
docker-compose -f docker-compose.dev.yml logs backend

# Restart containers
docker-compose -f docker-compose.dev.yml restart
```

**Issue: If there are existing containers which cannot be removed**
```bash
sudo systemctl restart docker
```