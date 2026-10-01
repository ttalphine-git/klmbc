#!/usr/bin/env pwsh

# Classified Ads System - Deployment Script (Windows)
# Usage: .\deploy.ps1 -Environment dev

param(
    [string]$Environment = "dev"
)

$ErrorActionPreference = "Stop"

$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "🚀 Deploying Classified Ads System - Environment: $Environment" -ForegroundColor Cyan
Write-Host "📁 Project directory: $ProjectDir" -ForegroundColor Cyan

# Load environment file
$envFile = Join-Path $ProjectDir ".env"
if (Test-Path $envFile) {
    $envContent = Get-Content $envFile | Where-Object { $_ -notmatch "^#" -and $_ -match "=" }
    foreach ($line in $envContent) {
        $parts = $line -split "="
        if ($parts.Count -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
    Write-Host "✅ Loaded environment from .env" -ForegroundColor Green
}
else {
    Write-Host "⚠️  .env file not found. Using defaults." -ForegroundColor Yellow
}

function Stop-Containers {
    Write-Host "🛑 Stopping containers..." -ForegroundColor Yellow
    Set-Location $ProjectDir
    docker-compose down -ErrorAction Continue
    Start-Sleep -Seconds 2
}

function Build-Images {
    Write-Host "🔨 Building Docker images..." -ForegroundColor Yellow
    Set-Location $ProjectDir
    docker-compose build --no-cache
}

function Start-Containers {
    Write-Host "🚀 Starting containers..." -ForegroundColor Yellow
    Set-Location $ProjectDir
    docker-compose up -d
    Start-Sleep -Seconds 5

    Write-Host "🏥 Checking service health..." -ForegroundColor Cyan

    # Wait for postgres
    Write-Host "⏳ Waiting for PostgreSQL..." -ForegroundColor Yellow
    $maxAttempts = 30
    $attempt = 0
    while ($attempt -lt $maxAttempts) {
        try {
            $result = docker exec classifiedads-db pg_isready -U postgres 2>$null
            if ($result -eq "accepting connections") {
                break
            }
        }
        catch {
            # Continue
        }
        $attempt++
        if ($attempt -ge $maxAttempts) {
            Write-Host "❌ PostgreSQL failed to start" -ForegroundColor Red
            docker-compose logs postgres
            exit 1
        }
        Write-Host "  ⏳ Attempt $attempt/$maxAttempts..." -ForegroundColor Gray
        Start-Sleep -Seconds 2
    }
    Write-Host "✅ PostgreSQL is ready" -ForegroundColor Green

    # Wait for backend
    Write-Host "⏳ Waiting for Backend..." -ForegroundColor Yellow
    $maxAttempts = 30
    $attempt = 0
    while ($attempt -lt $maxAttempts) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/validate" -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                break
            }
        }
        catch {
            # Continue
        }
        $attempt++
        if ($attempt -ge $maxAttempts) {
            Write-Host "❌ Backend failed to start" -ForegroundColor Red
            docker-compose logs backend
            exit 1
        }
        Write-Host "  ⏳ Attempt $attempt/$maxAttempts..." -ForegroundColor Gray
        Start-Sleep -Seconds 2
    }
    Write-Host "✅ Backend is ready" -ForegroundColor Green
}

function Show-Status {
    Write-Host ""
    Write-Host "📊 Service Status:" -ForegroundColor Cyan
    Write-Host "==================" -ForegroundColor Cyan
    docker-compose ps

    Write-Host ""
    Write-Host "🌐 Access Points:" -ForegroundColor Cyan
    Write-Host "==================" -ForegroundColor Cyan
    Write-Host "Frontend:    http://localhost:3000"
    Write-Host "Backend API: http://localhost:8080/api"
    Write-Host "PostgreSQL:  localhost:5432"
    Write-Host "Nginx:       http://localhost:80"
}

function Show-Logs {
    Write-Host ""
    Write-Host "📋 Recent Logs:" -ForegroundColor Cyan
    Write-Host "===============" -ForegroundColor Cyan
    docker-compose logs --tail=50 -f
}

# Main deployment logic
switch ($Environment) {
    "dev" {
        Write-Host "📝 Development Deployment" -ForegroundColor Cyan
        Build-Images
        Start-Containers
        Show-Status

        $response = Read-Host "View logs? (y/n)"
        if ($response -eq "y" -or $response -eq "Y") {
            Show-Logs
        }
    }
    "prod" {
        Write-Host "🔒 Production Deployment" -ForegroundColor Red
        $confirm = Read-Host "This will deploy to production. Continue? (y/n)"
        if ($confirm -ne "y" -and $confirm -ne "Y") {
            Write-Host "❌ Deployment cancelled" -ForegroundColor Red
            exit 1
        }
        Stop-Containers
        Build-Images
        Start-Containers
        Show-Status
    }
    default {
        Write-Host "❌ Invalid environment. Use: dev or prod" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "✅ Deployment complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📚 Next steps:" -ForegroundColor Cyan
Write-Host "1. Check service status: docker-compose ps"
Write-Host "2. View logs: docker-compose logs -f"
Write-Host "3. Test API: curl http://localhost:8080/api/auth/validate"
Write-Host "4. Access frontend: http://localhost:3000"
