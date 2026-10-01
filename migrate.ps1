# Supabase Database Migration Script
# This script applies all migrations to your Supabase database

param(
    [string]$ServiceRoleKey = ""
)

$ProjectRef = "obmqtkbheuylezjntayi"
$ProjectUrl = "https://obmqtkbheuylezjntayi.supabase.co"

Write-Host "🚀 Supabase Migration Script" -ForegroundColor Cyan
Write-Host "============================" -ForegroundColor Cyan

# Prompt for Service Role Key if not provided
if (-not $ServiceRoleKey) {
    Write-Host "`n📋 Instructions to get your Service Role Key:" -ForegroundColor Yellow
    Write-Host "  1. Go to: https://supabase.com/dashboard" -ForegroundColor Gray
    Write-Host "  2. Select project: 'classified-ads'" -ForegroundColor Gray
    Write-Host "  3. Go to: Settings → API" -ForegroundColor Gray
    Write-Host "  4. Copy: 'Service Role Secret'" -ForegroundColor Gray
    Write-Host ""

    $ServiceRoleKey = Read-Host "Enter your Service Role Key"
}

if (-not $ServiceRoleKey) {
    Write-Host "❌ Error: Service Role Key is required" -ForegroundColor Red
    exit 1
}

Write-Host "`n🔐 Credentials received" -ForegroundColor Green

# Migration V1: Create tables
$migration1 = @"
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(500) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    profile_image_url VARCHAR(500),
    bio TEXT,
    is_verified BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS classified_ads (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(12, 2),
    category VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    location_name VARCHAR(255),
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ad_images (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES classified_ads(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS schema_version (
    version INT PRIMARY KEY,
    description VARCHAR(255),
    type VARCHAR(20),
    script VARCHAR(1000),
    checksum INT,
    installed_by VARCHAR(100),
    installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    execution_time INT,
    success BOOLEAN
);

INSERT INTO schema_version (version, description, type, script, installed_by, success)
VALUES (1, 'Initial Schema', 'SQL', 'V1__Initial_Schema.sql', 'system', true)
ON CONFLICT DO NOTHING;
"@

# Migration V2: Add indexes
$migration2 = @"
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ads_user_id ON classified_ads(user_id);
CREATE INDEX IF NOT EXISTS idx_ads_status ON classified_ads(status);
CREATE INDEX IF NOT EXISTS idx_ads_created_at ON classified_ads(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ads_location ON classified_ads(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_ads_category ON classified_ads(category);
CREATE INDEX IF NOT EXISTS idx_ad_images_ad_id ON ad_images(ad_id);

INSERT INTO schema_version (version, description, type, script, installed_by, success)
VALUES (2, 'Add Indexes', 'SQL', 'V2__Add_Indexes.sql', 'system', true)
ON CONFLICT DO NOTHING;
"@

Write-Host "`n📦 Running Migration V1: Create Tables" -ForegroundColor Cyan

$payload1 = @{
    query = $migration1
} | ConvertTo-Json

try {
    $response1 = Invoke-WebRequest `
        -Uri "$ProjectUrl/rest/v1/rpc/exec_sql" `
        -Method POST `
        -Headers @{
            "apikey" = $ServiceRoleKey
            "Content-Type" = "application/json"
        } `
        -Body $payload1 `
        -ErrorAction Stop

    Write-Host "✅ Migration V1 completed" -ForegroundColor Green
    Write-Host "   Response: $($response1.StatusCode)" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Migration V1 failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "   Try running migrations manually in Supabase SQL Editor" -ForegroundColor Yellow
}

Write-Host "`n📦 Running Migration V2: Add Indexes" -ForegroundColor Cyan

$payload2 = @{
    query = $migration2
} | ConvertTo-Json

try {
    $response2 = Invoke-WebRequest `
        -Uri "$ProjectUrl/rest/v1/rpc/exec_sql" `
        -Method POST `
        -Headers @{
            "apikey" = $ServiceRoleKey
            "Content-Type" = "application/json"
        } `
        -Body $payload2 `
        -ErrorAction Stop

    Write-Host "✅ Migration V2 completed" -ForegroundColor Green
    Write-Host "   Response: $($response2.StatusCode)" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Migration V2 failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "`n✨ Verification" -ForegroundColor Cyan

# Verify tables
$verifyQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;"
$verifyPayload = @{ query = $verifyQuery } | ConvertTo-Json

try {
    $verifyResponse = Invoke-WebRequest `
        -Uri "$ProjectUrl/rest/v1/rpc/exec_sql" `
        -Method POST `
        -Headers @{
            "apikey" = $ServiceRoleKey
            "Content-Type" = "application/json"
        } `
        -Body $verifyPayload `
        -ErrorAction Stop

    $tables = ($verifyResponse.Content | ConvertFrom-Json)

    Write-Host "📊 Tables in database:" -ForegroundColor Green
    if ($tables) {
        $tables | ForEach-Object { Write-Host "   ✓ $_" -ForegroundColor Green }
    }
}
catch {
    Write-Host "⚠️  Could not verify tables" -ForegroundColor Yellow
    Write-Host "   Check Supabase dashboard manually" -ForegroundColor Gray
}

Write-Host "`n✅ Migration script completed!" -ForegroundColor Green
Write-Host "🎉 Your database is ready for development" -ForegroundColor Cyan
