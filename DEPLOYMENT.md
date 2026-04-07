# Continuous Deployment Setup Guide

## Free Infrastructure Setup

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

ssh root@YOUR_DROPLET_IP

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
DEV_SERVER_SSH_KEY        = (your private SSH key)
```

**For SIT Environment:**
```
SIT_SERVER_HOST           = your-sit-server-public-ip
SIT_SERVER_USER           = ubuntu
SIT_SERVER_SSH_KEY        = (your private SSH key)
```

**Example: How to generate SSH key**
```bash
# On your LOCAL machine
ssh-keygen -t ed25519 -f ~/.ssh/github-deploy -N ""

# Copy public key to your server
ssh-copy-id -i ~/.ssh/github-deploy.pub ubuntu@your-server-ip

# Copy PRIVATE key content to GitHub Secrets (DEV_SERVER_SSH_KEY / SIT_SERVER_SSH_KEY)
cat ~/.ssh/github-deploy
```

### Step 4: Set Up Environment Variables on Server

Create `.env.dev` and `.env.sit` files on your server:

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

# .env.sit
cat > .env.sit << 'EOF'
MYSQL_ROOT_PASSWORD=sit_root_pass_456
MYSQL_USER=fairshare
MYSQL_PASSWORD=fairshare_sit_pass
MYSQL_DATABASE=fairshare
COMPOSE_PROJECT_NAME=fairshare-sit
EOF

# Keep these files secure
chmod 600 .env.dev .env.sit
```

### Step 5: First Manual Deployment (Test)

On your server:

```bash
cd ~/fairshare-deployment

# Log in to GitHub Container Registry
echo "your_github_token_here" | docker login ghcr.io -u your-username --password-stdin

# Pull and start dev
docker-compose -f docker-compose.dev.yml pull
docker-compose -f docker-compose.dev.yml up -d

# Check status
docker-compose -f docker-compose.dev.yml ps

# View logs
docker-compose -f docker-compose.dev.yml logs -f backend
```

## How It Works

```
1. Push code to feature branch (not main)
   ↓
2. Create Pull Request to main
   - GitHub Actions CI runs (tests + security scans)
   - Code review happens
   ↓
3. Merge PR into main (click "Merge" button on GitHub)
   - Code now on main branch
   ↓
4. CD Workflow triggers: backend-cd.yml
   - Builds Docker image with Spring Boot app
   - Pushes image to GitHub Container Registry (GHCR)
   ↓
5. SSH into your Dev server
   - Pulls latest Docker image
   - Runs "docker-compose up -d"
   - MySQL + Backend start automatically
   ↓
6. Same for SIT environment (sequential)
```

## Git Workflow (Best Practice)

```bash
# 1. Create feature branch from main
git checkout -b feature/my-feature

# 2. Make changes and push to feature branch
git push origin feature/my-feature

# 3. Create Pull Request on GitHub
# → CI tests run here (no deployment yet)

# 4. After approval, merge PR
# → Deployment happens automatically to Dev + SIT ✅
```

**Important:** Deployment ONLY happens when code is merged into `main`, not when creating the PR.

## Database Configuration

**Dev Environment:**
- Automatic schema creation (`spring.jpa.hibernate.ddl-auto: update`)
- Good for development
- MySQL exposed on port 3306
- **data.sql is executed automatically** after schema creation

**SIT Environment:**
- Validates existing schema (`spring.jpa.hibernate.ddl-auto: validate`)
- Requires schema to exist
- MySQL exposed on port 3307
- **data.sql is executed automatically** after validation

The `data.sql` file contains:
- Static roles (ADMIN, USER)
- Group roles (GROUP_ADMIN, GROUP_MEMBER)
- Badges with rules

It uses `ON DUPLICATE KEY UPDATE` to safely insert or update existing records.

## Important: Containers Run in the Background

**Your deployment WILL NOT STOP when you close the SSH connection.**

Here's why:
- `docker-compose up -d` = starts containers in **detached mode** (background)
- Containers run independently of your SSH session
- `restart: unless-stopped` = containers auto-restart if they crash
- Server reboot = containers automatically start on boot

So you can:
1. SSH into your server
2. Start the deployment with `docker-compose up -d`
3. Disconnect from SSH
4. Containers keep running 24/7 ✅

**To verify containers are still running after you disconnect:**
```bash
# SSH back in later
ssh ubuntu@your-server-ip

# Check containers are still running
docker ps

# Check logs from your past session
docker-compose -f docker-compose.dev.yml logs --tail=50 backend
```

## Monitoring & Troubleshooting

### 1. Check GitHub Actions Workflow Status

1. Go to your GitHub repo → **Actions** tab
2. Find the most recent workflow run
3. Look for:
   - ✅ All jobs passed = successful deployment
   - ❌ Red X = check failed job for error logs
4. Click on failed job to see detailed logs

### 2. SSH Into Your Server & Check Containers

```bash
# Check if containers are running
docker ps

# Expected output for dev:
# fairshare-mysql-dev (running)
# fairshare-backend-dev (running)

# Check container status and health
docker-compose -f docker-compose.dev.yml ps

# Check health status specifically
docker ps --format "table {{.Names}}\t{{.Status}}"
```

### 3. View Application Logs

```bash
# View real-time logs (follow mode)
docker-compose -f docker-compose.dev.yml logs -f backend

# View last 100 lines
docker-compose -f docker-compose.dev.yml logs --tail=100 backend

# View logs from specific container
docker logs -f fairshare-backend-dev
```

**What to look for in logs:**
- ✅ `Started FairsharebackendApplication` = app started successfully
- ✅ `Hibernate: create table...` = schema being created (dev only)
- ✅ `Hibernate: insert into...` = data.sql being executed
- ❌ `Connection refused` = MySQL not ready, wait a bit
- ❌ `Schema validation failed` = SIT schema doesn't match (drop volume and redeploy)

### 4. Test API Endpoint

```bash
# From your server
curl -v http://localhost:8080/health

# Expected response:
# {"status":"UP"}

# For SIT (port 8081)
curl -v http://localhost:8081/health
```

### 5. Check Database Connection

```bash
# Connect to MySQL inside the container
docker exec -it fairshare-mysql-dev mysql -u fairshare -p fairshare

# Once logged in, check tables
SHOW TABLES;
SELECT COUNT(*) FROM tb_role;
SELECT COUNT(*) FROM tb_badge;
EXIT;
```

### 6. Container Health Status

```bash
# Check if health checks are passing
docker inspect fairshare-backend-dev | grep -A 20 "Health"

# Expected output:
# "Health": {
#     "Status": "healthy"
# }
```

### Troubleshooting Common Issues

**Issue: Backend container won't start**
```bash
# View full error logs
docker-compose -f docker-compose.dev.yml logs backend

# Restart containers
docker-compose -f docker-compose.dev.yml restart
```

**Issue: MySQL connection timeout**
```bash
# MySQL needs time to initialize, wait 30 seconds then check
docker-compose -f docker-compose.dev.yml ps

# Check MySQL logs
docker-compose -f docker-compose.dev.yml logs mysql
```

**Issue: "Schema validation failed" in SIT**
```bash
# SIT expects schema to exist. First deployment must initialize schema
# Option 1: Use dev volume first, then SIT
# Option 2: Remove volume and let dev create it first
docker-compose -f docker-compose.sit.yml down -v
```

**Issue: Want to see if data.sql ran**
```bash
# Check if roles and badges exist
docker exec -it fairshare-mysql-dev mysql -u fairshare -pfairshare_dev_pass fairshare -e "SELECT * FROM tb_role;"

# Should show GROUP_ADMIN and GROUP_MEMBER
```

### View logs
```bash
docker-compose -f docker-compose.dev.yml logs -f backend
```

## Next Steps (Optional)

Once this is working, you can:
1. Add UAT and Prod environments
2. Use Nginx as reverse proxy for multiple apps
3. Set up automated backups
4. Add health checks + auto-restart policies
5. Implement blue-green deployments
6. Add database backups to cloud storage

