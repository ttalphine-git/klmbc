# Local Development Setup (Without Docker)

Run the entire Classified Ads System locally without Docker.

## Prerequisites

### 1. PostgreSQL 15
- Download: https://www.postgresql.org/download/windows/
- Install with default settings
- Remember the password for `postgres` user

### 2. Java 17+
- Download: https://www.oracle.com/java/technologies/downloads/#java17
- Or use: https://adoptium.net/temurin/releases/
- Install and verify: `java -version`

### 3. Maven 3.8+
- Download: https://maven.apache.org/download.cgi
- Extract to a folder (e.g., `C:\apache-maven-3.9.0`)
- Add to PATH: `C:\apache-maven-3.9.0\bin`
- Verify: `mvn -version`

### 4. Node.js 18+
- Download: https://nodejs.org/ (LTS version)
- Install and verify: `node --version` and `npm --version`

---

## Step 1: Setup PostgreSQL Database

### Option A: Using PostgreSQL GUI (pgAdmin)

1. Open **pgAdmin** (installed with PostgreSQL)
2. Connect to local server with password
3. Create new database:
   - Right-click **Databases** → **Create** → **Database**
   - Name: `classifiedads`
   - Save

### Option B: Using Command Line

```powershell
# Open Command Prompt as Administrator
# Navigate to PostgreSQL bin directory
cd "C:\Program Files\PostgreSQL\15\bin"

# Connect to PostgreSQL
psql -U postgres

# Run these commands:
CREATE DATABASE classifiedads;
\connect classifiedads

# Exit
\q
```

### Run Database Migrations

```powershell
cd d:\KLMBCS\backend\src\main\resources\db\migration

# Run first migration
psql -U postgres -d classifiedads -f V1__Initial_Schema.sql

# Run second migration
psql -U postgres -d classifiedads -f V2__Add_Indexes.sql

# Verify tables created
psql -U postgres -d classifiedads -c "\dt"
```

Expected output:
```
             List of relations
 Schema |      Name       | Type  | Owner
--------+-----------------+-------+----------
 public | ad_images       | table | postgres
 public | classified_ads  | table | postgres
 public | schema_version  | table | postgres
 public | users           | table | postgres
```

---

## Step 2: Start PostgreSQL Service

### Verify PostgreSQL is Running

```powershell
# Check if service is running
Get-Service PostgreSQL* | Select-Object Name, Status

# If not running, start it
Start-Service postgresql-x64-15

# Or use Services app:
# Windows Key → Services → Find "postgresql" → Start
```

Test connection:
```powershell
psql -U postgres -d classifiedads -c "SELECT 1 as ok;"
```

Should return: `ok`

---

## Step 3: Start Backend (Spring Boot)

### Option A: Using Maven Command

```powershell
cd d:\KLMBCS\backend

# Build project
mvn clean install

# Start application
mvn spring-boot:run
```

Backend will start at: **http://localhost:8080**

### Option B: Using IDE (VS Code / IntelliJ)

**IntelliJ IDEA**:
1. Open `d:\KLMBCS\backend` as project
2. Right-click `ClassifiedAdsApplication.java`
3. Click "Run 'ClassifiedAdsApplication.main()'"

**VS Code**:
1. Install Extension Pack for Java
2. Open folder: `d:\KLMBCS\backend`
3. Press Ctrl+F5 to run

### Verify Backend Started

```powershell
# In new terminal
curl http://localhost:8080/api/auth/validate
```

Should return: `true`

---

## Step 4: Start Frontend (React)

### Option A: Development Server (Recommended)

```powershell
cd d:\KLMBCS\frontend

# Install dependencies (first time only)
npm install

# Start dev server
npm run dev
```

Frontend will start at: **http://localhost:5173**

### Option B: Production Build

```powershell
cd d:\KLMBCS\frontend

# Install dependencies
npm install

# Build
npm run build

# Install simple HTTP server
npm install -g http-server

# Serve
http-server dist -p 3000
```

Frontend will be at: **http://localhost:3000**

---

## Step 5: Access the Application

Open your browser:

**Development Mode**:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api

**Production Mode**:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

---

## Setup Environment Variables

### Backend Configuration

Edit: `d:\KLMBCS\backend\src\main\resources\application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/classifiedads
    username: postgres
    password: YOUR_POSTGRES_PASSWORD  # Change this!
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate

jwt:
  secret: classifiedads-secret-key-min-32-characters
  expiration: 86400000

file:
  upload:
    dir: C:/uploads  # Or D:/uploads
```

### Frontend Configuration

Create: `d:\KLMBCS\frontend\.env.local`

```env
VITE_API_URL=http://localhost:8080/api
```

---

## Running Multiple Terminals

You need 3 terminal windows:

### Terminal 1: Backend
```powershell
cd d:\KLMBCS\backend
mvn spring-boot:run
```

### Terminal 2: Frontend
```powershell
cd d:\KLMBCS\frontend
npm run dev
```

### Terminal 3: Monitoring/Testing
```powershell
# Test API
curl http://localhost:8080/api/auth/validate

# Check logs
Get-Service PostgreSQL* | Select-Object Name, Status
```

---

## Create First Upload Directory

```powershell
# Create uploads folder for images
New-Item -ItemType Directory -Force -Path "D:\uploads"
New-Item -ItemType Directory -Force -Path "D:\uploads\users"
New-Item -ItemType Directory -Force -Path "D:\uploads\ads"

# Or use C: drive
New-Item -ItemType Directory -Force -Path "C:\uploads"
```

Update path in `application.yml`:
```yaml
file:
  upload:
    dir: D:/uploads  # Match your folder location
```

---

## Test the Application

### 1. Register New Account

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phoneNumber": "+919876543210",
    "bio": "Testing the app"
  }'
```

### 2. Open in Browser

Go to: **http://localhost:5173** (dev) or **http://localhost:3000** (production)

### 3. Sign Up
- Email: test@example.com
- Password: password123
- Name: Test User

### 4. Create Ad
- Click "Post Ad"
- Fill in details
- Select location on map
- Upload images

---

## Troubleshooting

### PostgreSQL Won't Start

```powershell
# Check status
Get-Service postgresql-x64-15 | Select-Object Name, Status

# Restart service
Restart-Service postgresql-x64-15

# Check if port 5432 is in use
netstat -ano | findstr :5432
```

### Maven Build Fails

```powershell
cd d:\KLMBCS\backend

# Clean cache
mvn clean

# Rebuild
mvn install -DskipTests

# Check Java version
java -version  # Should be 17 or higher
```

### npm Dependencies Issue

```powershell
cd d:\KLMBCS\frontend

# Clear cache
npm cache clean --force

# Delete node_modules
rmdir /s node_modules

# Reinstall
npm install
```

### Port Already in Use

```powershell
# Find process using port (example: 8080)
netstat -ano | findstr :8080

# Kill process by PID
taskkill /PID <PID> /F

# Or change port in application.yml
server:
  port: 8081
```

### File Upload Not Working

```powershell
# Verify uploads folder exists and is writable
Test-Path D:\uploads

# Grant full permissions
icacls D:\uploads /grant:r Everyone:(OI)(CI)F

# Update path in application.yml
file:
  upload:
    dir: D:/uploads
```

---

## Quick Start Script

Save as: `d:\KLMBCS\run-local.ps1`

```powershell
param(
    [string]$Action = "all"
)

function Start-Backend {
    Write-Host "🚀 Starting Backend..." -ForegroundColor Cyan
    Set-Location d:\KLMBCS\backend
    mvn spring-boot:run
}

function Start-Frontend {
    Write-Host "🚀 Starting Frontend..." -ForegroundColor Cyan
    Set-Location d:\KLMBCS\frontend
    npm run dev
}

function Setup {
    Write-Host "📋 Setting up local environment..." -ForegroundColor Cyan
    
    # Create uploads folder
    New-Item -ItemType Directory -Force -Path "D:\uploads\users"
    New-Item -ItemType Directory -Force -Path "D:\uploads\ads"
    
    # Install backend dependencies
    Set-Location d:\KLMBCS\backend
    mvn clean install
    
    # Install frontend dependencies
    Set-Location d:\KLMBCS\frontend
    npm install
    
    Write-Host "✅ Setup complete!" -ForegroundColor Green
    Write-Host "Run: .\run-local.ps1 -Action backend (in one terminal)"
    Write-Host "Run: .\run-local.ps1 -Action frontend (in another terminal)"
}

switch ($Action) {
    "backend" { Start-Backend }
    "frontend" { Start-Frontend }
    "setup" { Setup }
    "all" {
        Write-Host "📚 Local Development Setup"
        Write-Host "=========================="
        Write-Host ""
        Write-Host "For local development without Docker:"
        Write-Host ""
        Write-Host "1. Setup (first time):"
        Write-Host "   .\run-local.ps1 -Action setup"
        Write-Host ""
        Write-Host "2. Start Backend (Terminal 1):"
        Write-Host "   .\run-local.ps1 -Action backend"
        Write-Host ""
        Write-Host "3. Start Frontend (Terminal 2):"
        Write-Host "   .\run-local.ps1 -Action frontend"
        Write-Host ""
        Write-Host "4. Open browser:"
        Write-Host "   http://localhost:5173"
        Write-Host ""
    }
}
```

Run it:
```powershell
cd d:\KLMBCS
.\run-local.ps1 -Action setup  # First time only
.\run-local.ps1 -Action backend # Terminal 1
.\run-local.ps1 -Action frontend # Terminal 2
```

---

## Development Tips

### Hot Reload
- **Backend**: Restart needed after changes
- **Frontend**: Auto-reloads on save (Vite)

### Database Access
```powershell
# Connect to database
psql -U postgres -d classifiedads

# Common queries
SELECT * FROM users;
SELECT * FROM classified_ads;
```

### View Logs
```powershell
# Backend logs appear in Maven terminal
# Frontend logs appear in npm terminal
# Database logs in PostgreSQL logs folder
```

---

## Next Steps

Once running locally:
1. Test user registration
2. Post some ads
3. View on map
4. Upload images
5. Make code changes and see live updates

Then deploy to Docker or production!
