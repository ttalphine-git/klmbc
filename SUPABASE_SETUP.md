# Deploy with Supabase - Complete Guide

Use Supabase (PostgreSQL-as-a-Service) instead of local PostgreSQL. Fast, free, and production-ready!

## What is Supabase?

- ✅ **PostgreSQL Database** - hosted in the cloud
- ✅ **Free Tier** - perfect for development
- ✅ **No Installation** - no local setup needed
- ✅ **Real-time APIs** - built-in
- ✅ **Web Dashboard** - visual database management
- ✅ **Backups & Scaling** - automatic

---

## Step 1: Create Supabase Project

### 1.1 Sign Up

1. Go to: https://supabase.com
2. Click **Sign Up**
3. Use GitHub or email to register
4. Verify email

### 1.2 Create New Project

1. Click **New Project**
2. Project name: `classified-ads`
3. Database password: `strong-password-123` (remember this!)
4. Region: Choose closest to Kerala (e.g., Asia - Singapore)
5. Click **Create new project**
6. **Wait 2-3 minutes** for project to initialize

### 1.3 Get Connection Details

After project is ready:

1. Go to **Settings** → **Database** → **Connection string**
2. Copy the **JDBC connection string** (or URI)
3. It looks like:
```
postgresql://postgres:password@db.xxx.supabase.co:5432/postgres
```

Save this somewhere safe! ⚠️

---

## Step 2: Update Backend Configuration

### 2.1 Edit application.yml

File: `d:\KLMBCS\backend\src\main\resources\application.yml`

Replace datasource section:

```yaml
spring:
  datasource:
    # Supabase PostgreSQL Connection
    url: jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: your-supabase-password-here
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

jwt:
  secret: ${JWT_SECRET:classifiedads-secret-key-min-32-characters-required}
  expiration: ${JWT_EXPIRATION:86400000}

file:
  upload:
    dir: ${FILE_UPLOAD_DIR:./uploads}

server:
  port: 8080

logging:
  level:
    root: INFO
    com.classifiedads: DEBUG
```

**Key changes:**
- `url`: Use your Supabase connection string
- `username`: Usually `postgres`
- `password`: Your Supabase password
- `?sslmode=require`: Important for Supabase

### 2.2 Create .env File (Optional but Recommended)

File: `d:\KLMBCS\.env`

```env
# Supabase Database
DB_URL=jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres?sslmode=require
DB_USER=postgres
DB_PASSWORD=your-supabase-password
DB_PORT=5432

# Backend
BACKEND_PORT=8080
JWT_SECRET=classifiedads-secret-key-min-32-characters
JWT_EXPIRATION=86400000

# Frontend
FRONTEND_PORT=5173
VITE_API_URL=http://localhost:8080/api

# Files
FILE_UPLOAD_DIR=./uploads
```

---

## Step 3: Setup Database Schema

### 3.1 Using Supabase Web Console

1. Go to Supabase Dashboard
2. Click **SQL Editor**
3. Click **New Query**
4. Copy-paste from: `backend/src/main/resources/db/migration/V1__Initial_Schema.sql`
5. Click **Run**
6. Repeat for `V2__Add_Indexes.sql`

### 3.2 Alternative: Using pgAdmin (Desktop)

1. Download pgAdmin: https://www.pgadmin.org/
2. Open pgAdmin
3. Create new connection:
   - **Host**: db.xxxxxxxxxxxx.supabase.co
   - **Port**: 5432
   - **Database**: postgres
   - **Username**: postgres
   - **Password**: your-password
4. Navigate to database
5. Run SQL scripts from migration files

### 3.3 Verify Tables Created

In Supabase SQL Editor, run:

```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

Should return:
```
ad_images
classified_ads
schema_version
users
```

---

## Step 4: Setup File Storage (Supabase Storage)

### Option A: Use Supabase Storage

1. Go to Supabase Dashboard
2. Click **Storage** (left sidebar)
3. Create new bucket: `uploads`
4. Create sub-folders:
   - `uploads/users`
   - `uploads/ads`
5. Set bucket to **Public** for file access

### Option B: Use Local Filesystem

Create folders on your machine:

```powershell
New-Item -ItemType Directory -Force -Path "./uploads/users"
New-Item -ItemType Directory -Force -Path "./uploads/ads"
```

Update `application.yml`:
```yaml
file:
  upload:
    dir: ./uploads
```

### Option C: Use AWS S3 (Later)

Add S3 configuration when scaling up.

---

## Step 5: Run Backend Locally

### 5.1 Update Maven Dependencies

The backend already has PostgreSQL driver, so no changes needed!

### 5.2 Start Backend

```powershell
cd d:\KLMBCS\backend

# Build
mvn clean install

# Run
mvn spring-boot:run
```

✅ Backend starts at: **http://localhost:8080**

Test connection:
```powershell
curl http://localhost:8080/api/auth/validate
# Should return: true
```

---

## Step 6: Run Frontend Locally

### 6.1 Start Frontend Dev Server

```powershell
cd d:\KLMBCS\frontend

# Install (first time)
npm install

# Run
npm run dev
```

✅ Frontend starts at: **http://localhost:5173**

---

## Step 7: Test Complete Flow

1. Open: http://localhost:5173
2. Click **Sign Up**
3. Create account with email
4. Check Supabase: **Table Editor** → **users** → see your user!
5. Post an ad
6. Upload image
7. Check map

---

## Deploy Backend to Production

### Option A: Deploy to Railway.app (Recommended) ✅

**Easiest for Spring Boot apps**

#### 1. Sign Up
- Go to: https://railway.app
- Sign up with GitHub
- Authorize Railway

#### 2. Create New Project
- Click **New Project** → **Deploy from GitHub**
- Select your repo (or connect repo first)
- Select `backend` folder

#### 3. Add Environment Variables
In Railway dashboard:
```
DB_URL=jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres?sslmode=require
DB_USER=postgres
DB_PASSWORD=your-supabase-password
JWT_SECRET=your-secret-key
CORS_ORIGINS=https://your-frontend-domain.com
```

#### 4. Deploy
- Click **Deploy**
- Wait for build to complete
- Backend runs automatically! ✅

#### 5. Get Public URL
- Click your service
- Copy **Public URL** (e.g., https://classifiedads-backend.railway.app)

#### 6. Update Frontend API URL
In `frontend/.env.production`:
```
VITE_API_URL=https://classifiedads-backend.railway.app/api
```

---

### Option B: Deploy to Render.com

#### 1. Sign Up
- Go to: https://render.com
- Sign up with GitHub

#### 2. Create Web Service
- Click **New** → **Web Service**
- Connect GitHub repo
- Select branch: `main`

#### 3. Configuration
```
Build Command: mvn clean package -DskipTests
Start Command: java -jar target/*.jar
```

#### 4. Environment Variables
Same as Railway (see above)

#### 5. Deploy
- Click **Create Web Service**
- Wait for deployment
- Get public URL

---

### Option C: Deploy to Fly.io

#### 1. Install Fly CLI
```powershell
# Download: https://fly.io/docs/getting-started/installing-flyctl/
# Or use: choco install flyctl
```

#### 2. Create Fly App
```bash
fly auth login
fly launch --builder heroku --dockerfile Dockerfile
```

#### 3. Set Secrets
```bash
fly secrets set DB_URL="jdbc:postgresql://..."
fly secrets set DB_PASSWORD="your-password"
```

#### 4. Deploy
```bash
fly deploy
```

---

## Deploy Frontend to Production

### Option A: Vercel (Recommended for React)

#### 1. Sign Up
- Go to: https://vercel.com
- Sign up with GitHub

#### 2. Import Project
- Click **Import Project**
- Select your GitHub repo
- Select `frontend` folder

#### 3. Environment Variables
```
VITE_API_URL=https://your-backend-url.railway.app/api
```

#### 4. Deploy
- Click **Deploy**
- Get automatic URL (e.g., classifiedads.vercel.app)

---

### Option B: Netlify

#### 1. Sign Up
- Go to: https://netlify.com
- Sign up with GitHub

#### 2. New Site
- Click **New site from Git**
- Select repo
- Select folder: `frontend`

#### 3. Build Settings
```
Build command: npm run build
Publish directory: dist
```

#### 4. Environment Variables
```
VITE_API_URL=https://your-backend-url.railway.app/api
```

#### 5. Deploy
- Click **Deploy**

---

### Option C: GitHub Pages (Free)

1. Build frontend: `npm run build`
2. Push `dist/` folder to GitHub
3. Enable Pages in repo settings
4. Get free URL: `yourusername.github.io/classifiedads`

---

## Architecture After Supabase Setup

```
┌─────────────────────────────────────────┐
│  Frontend (Vercel/Netlify/GitHub Pages) │
│  https://classifiedads.vercel.app       │
└──────────────────┬──────────────────────┘
                   │
                   │ API calls
                   │
┌──────────────────▼──────────────────────┐
│  Backend (Railway/Render/Fly)           │
│  https://backend.railway.app            │
└──────────────────┬──────────────────────┘
                   │
                   │ SQL queries
                   │
┌──────────────────▼──────────────────────┐
│  Supabase PostgreSQL (Hosted)           │
│  db.xxxx.supabase.co                    │
└─────────────────────────────────────────┘

File Storage: Local filesystem or Supabase Storage
```

---

## Complete Deployment Checklist

### Local Development (Before Deploying)
- [ ] Backend runs: `mvn spring-boot:run`
- [ ] Frontend runs: `npm run dev`
- [ ] Can create accounts
- [ ] Can post ads
- [ ] Can upload images
- [ ] Map displays ads

### Supabase Setup
- [ ] Project created
- [ ] Connection string saved
- [ ] Database schema created
- [ ] Tables verified

### Backend Deployment
- [ ] Git repo created
- [ ] Backend pushed to GitHub
- [ ] Environment variables set
- [ ] Deployed to Railway/Render/Fly
- [ ] API responds: `curl https://backend.railway.app/api/auth/validate`

### Frontend Deployment
- [ ] Update `.env.production` with backend URL
- [ ] Build: `npm run build`
- [ ] Deploy to Vercel/Netlify
- [ ] Test all features on live URL

---

## Troubleshooting

### "Connection refused" to Supabase

**Solution:**
```yaml
# Make sure you have:
url: jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require
```

### Tables not found

**Solution:**
Run migrations again in Supabase SQL Editor:
```sql
-- Paste V1 and V2 migration scripts
```

### File upload fails

**Solution:**
- Check upload directory exists: `./uploads/`
- Change to Supabase Storage
- Or use S3

### Frontend can't reach backend

**Solution:**
```
1. Check backend URL in .env.production
2. Verify CORS settings in SecurityConfig.java
3. Test: curl https://backend.railway.app/api/auth/validate
```

---

## Cost Breakdown

| Service | Free Tier | Paid |
|---------|-----------|------|
| Supabase | 500 MB DB + API | $25/month |
| Railway | $5 credit/month | Pay as you go |
| Render | 750 hours/month | $7/month |
| Vercel | Unlimited | Pay for overages |
| Netlify | Unlimited | Pay for overages |

**Total for MVP**: ~$7-10/month (very affordable!)

---

## Next Steps After Deploy

1. **Custom Domain**
   - Point domain to Vercel/Railway
   - Enable HTTPS (automatic)

2. **Monitoring**
   - Railway Dashboard: Monitor logs & performance
   - Supabase: Monitor database usage

3. **Backups**
   - Supabase: Automatic daily backups
   - Railroad: Integrated backups

4. **Scaling**
   - Increase Railway/Render plan if needed
   - Upgrade Supabase tier if database grows

5. **Features**
   - Add email verification
   - Add search functionality
   - Add messaging between users

---

## Quick Reference Commands

```powershell
# Start backend locally
cd d:\KLMBCS\backend
mvn spring-boot:run

# Start frontend locally
cd d:\KLMBCS\frontend
npm run dev

# Build frontend for production
npm run build

# Test backend API
curl http://localhost:8080/api/auth/validate

# Connect to Supabase database
psql postgresql://postgres:PASSWORD@db.xxx.supabase.co:5432/postgres
```

---

## Support

- **Supabase Docs**: https://supabase.com/docs
- **Railway Docs**: https://railway.app/docs
- **Render Docs**: https://render.com/docs
- **Spring Boot Docs**: https://spring.io/projects/spring-boot

---

**You're ready to deploy! 🚀**
