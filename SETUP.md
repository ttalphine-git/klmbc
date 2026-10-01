# Setup Guide - Classified Ads System

Complete step-by-step guide to set up and run the Classified Ads System.

## Table of Contents
1. [Quick Start (Docker)](#quick-start-docker)
2. [Local Development](#local-development)
3. [Deployment](#deployment)
4. [Troubleshooting](#troubleshooting)

---

## Quick Start (Docker)

### Prerequisites
- Docker Desktop (includes Docker & Docker Compose)
- Git

### Step 1: Prepare the Project

```bash
# Navigate to project directory
cd d:\KLMBCS

# Copy environment template
cp .env.example .env

# (Optional) Edit .env for custom configuration
# nano .env
```

### Step 2: Start All Services

```bash
# Using docker-compose
docker-compose up --build

# OR using make (if available)
make rebuild

# OR using PowerShell (Windows)
.\deploy.ps1 -Environment dev
```

### Step 3: Verify Services

```bash
# Check all services are running
docker-compose ps

# Should see:
# - classifiedads-db (postgres)
# - classifiedads-backend (spring boot)
# - classifiedads-frontend (nginx)
# - classifiedads-nginx (reverse proxy)
```

### Step 4: Access the Application

Open your browser:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/auth/validate

### Step 5: Create Your First Account

1. Click **Sign Up** on the main page
2. Fill in your details (email, password, name, optional phone & avatar)
3. Click **Create Account**
4. You'll be logged in and redirected to the map

### Step 6: Post Your First Ad

1. Click **Post Ad** button (top right)
2. Fill in ad details (title, description, category, price)
3. Click on the map to select location (latitude/longitude)
4. Upload images
5. Click **Create Ad**

---

## Local Development

### Backend Setup (Java/Spring Boot)

**Prerequisites**:
- Java 17+
- Maven 3.8+
- PostgreSQL 15

**Steps**:

```bash
cd backend

# Install dependencies
mvn clean install

# Run locally
mvn spring-boot:run

# Runs on http://localhost:8080
```

**Configuration** (`src/main/resources/application.yml`):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/classifiedads
    username: postgres
    password: postgres
```

### Frontend Setup (React/TypeScript)

**Prerequisites**:
- Node.js 18+
- npm or yarn

**Steps**:

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Runs on http://localhost:5173
```

**Build for production**:
```bash
npm run build
# Creates dist/ folder with optimized build
```

### Database Setup (PostgreSQL)

**Steps**:

```bash
# Create database
createdb classifiedads

# Run migrations
psql -U postgres -d classifiedads -f backend/src/main/resources/db/migration/V1__Initial_Schema.sql
psql -U postgres -d classifiedads -f backend/src/main/resources/db/migration/V2__Add_Indexes.sql

# Verify tables
psql -U postgres -d classifiedads -c "\dt"
```

---

## Deployment

### Deploy to Digital Ocean

#### Prerequisites
- Digital Ocean account
- Droplet with Docker & Docker Compose
- Domain name (optional, for production)

#### Steps

**1. SSH into your droplet**:
```bash
ssh root@your-droplet-ip
```

**2. Clone the repository**:
```bash
git clone https://github.com/youruser/classifiedads.git /opt/classifiedads
cd /opt/classifiedads
```

**3. Configure environment**:
```bash
cp .env.example .env

# Edit with your production values
nano .env
```

**4. Start services**:
```bash
docker-compose up -d
```

**5. Setup SSL (Let's Encrypt)**:
```bash
# Install certbot
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx

# Get certificate
sudo certbot certonly --standalone -d your-domain.com

# Copy to project
sudo cp /etc/letsencrypt/live/your-domain.com/fullchain.pem ./ssl/
sudo cp /etc/letsencrypt/live/your-domain.com/privkey.pem ./ssl/

# Update nginx.conf with SSL paths
# Restart nginx
docker-compose restart nginx
```

**6. Setup auto-renewal**:
```bash
# Add to crontab
0 2 * * * cd /opt/classifiedads && docker-compose restart nginx
```

#### Monitoring

```bash
# View logs
docker-compose logs -f

# Check service status
docker-compose ps

# Restart services
docker-compose restart

# View resource usage
docker stats
```

#### Updating Application

```bash
cd /opt/classifiedads
git pull origin main
docker-compose build
docker-compose up -d
```

---

## Troubleshooting

### Issue: PostgreSQL Won't Start

**Symptoms**: Database connection refused

**Solution**:
```bash
# Check logs
docker-compose logs postgres

# Verify container is running
docker ps | grep postgres

# Restart PostgreSQL
docker-compose restart postgres

# Wait for it to be healthy
docker-compose ps postgres
```

### Issue: Backend Can't Connect to Database

**Symptoms**: Connection refused error

**Solution**:
```bash
# Verify connection string in application.yml
# Default: jdbc:postgresql://postgres:5432/classifiedads

# Check PostgreSQL is accepting connections
docker-compose exec postgres pg_isready

# Check backend logs
docker-compose logs backend | grep -i error
```

### Issue: Frontend Can't Connect to Backend

**Symptoms**: API errors, 401 Unauthorized

**Solution**:
```bash
# Check backend is running
curl http://localhost:8080/api/auth/validate

# Check CORS configuration in SecurityConfig.java
# Check VITE_API_URL in .env

# Verify proxy configuration in vite.config.ts

# Check browser console for CORS errors
```

### Issue: Image Upload Fails

**Symptoms**: 500 error when uploading images

**Solution**:
```bash
# Check uploads directory exists and has proper permissions
docker exec classifiedads-backend ls -la /app/uploads

# Create if missing
docker exec classifiedads-backend mkdir -p /app/uploads
docker exec classifiedads-backend chmod 755 /app/uploads

# Check file size limit in SecurityConfig
# Max file size: 50MB (configurable in application.yml)
```

### Issue: Containers Keep Restarting

**Symptoms**: Services restart repeatedly

**Solution**:
```bash
# Check logs for errors
docker-compose logs -f --tail=100

# Verify all environment variables are set
cat .env

# Check Docker resources
docker stats

# Increase Docker memory allocation if needed
```

### Issue: Port Already in Use

**Symptoms**: "Address already in use" error

**Solution**:
```bash
# Find what's using the port (example: port 8080)
lsof -i :8080

# Kill the process or change ports in .env
# Restart docker-compose
docker-compose down
docker-compose up -d
```

---

## Development Workflow

### Making Code Changes

**Backend**:
```bash
# Make changes in src/
# Docker will auto-reload (if using spring-boot-devtools)
# Restart container for changes to take effect
docker-compose restart backend
```

**Frontend**:
```bash
# Make changes in src/
# Vite dev server will hot-reload automatically
# No restart needed
```

### Database Schema Changes

**Step 1**: Create new migration file
```bash
# Create: backend/src/main/resources/db/migration/V3__Your_Migration.sql
# Follow naming: V<number>__Description.sql
```

**Step 2**: Add SQL commands
```sql
-- Your SQL changes here
ALTER TABLE classified_ads ADD COLUMN status VARCHAR(50) DEFAULT 'ACTIVE';

-- Update schema_version
INSERT INTO schema_version (version, description, type, script, installed_by, success)
VALUES (3, 'Your Migration Description', 'SQL', 'V3__Your_Migration.sql', 'system', true);
```

**Step 3**: Restart PostgreSQL
```bash
docker-compose restart postgres
```

### Committing Changes to Git

```bash
# Stage changes
git add -A

# Commit with descriptive message
git commit -m "feat: add location-based ad filtering"

# Push to repository
git push origin main
```

---

## Performance Optimization

### Database
- ✅ Indexes created on frequently queried columns
- ✅ Pagination implemented for list endpoints
- Add: Connection pooling, query optimization

### Frontend
- ✅ TypeScript for type safety
- ✅ Lazy loading of components
- ✅ Vite for fast builds
- Add: Code splitting, image optimization

### Backend
- ✅ Compression enabled in nginx
- ✅ Database indexing
- Add: Caching layer (Redis), rate limiting

---

## Next Steps

After successful setup:

1. **Test the application**:
   - Create accounts
   - Post ads
   - View on map
   - Upload images

2. **Review security**:
   - Change default JWT secret
   - Enable HTTPS in production
   - Review CORS settings

3. **Scale the application**:
   - Add Redis for caching
   - Move uploads to S3
   - Migrate to managed PostgreSQL
   - Add monitoring (Prometheus, Grafana)

4. **Add features**:
   - Search functionality
   - Saved ads/favorites
   - User messaging
   - Ad categories and filters

---

## Support

For issues or questions:
1. Check logs: `docker-compose logs -f`
2. Review README.md for architecture details
3. Create GitHub issue with error details

---

## Quick Command Reference

```bash
# Start/Stop
docker-compose up -d        # Start in background
docker-compose down         # Stop all services
docker-compose restart      # Restart all services

# Logs
docker-compose logs -f      # View all logs
docker-compose logs backend # View backend logs only

# Database
docker-compose exec postgres psql -U postgres -d classifiedads

# Rebuild
docker-compose build        # Rebuild images
docker-compose up -d        # Start with latest images

# Cleanup
docker-compose down -v      # Stop and remove volumes
docker system prune          # Clean up Docker resources

# Status
docker-compose ps           # Show running containers
docker stats                # Show resource usage
```

---

**Happy deploying! 🚀**
