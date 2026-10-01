#!/bin/bash

# Classified Ads System - Deployment Script
# Usage: ./deploy.sh [dev|prod]

set -e

ENVIRONMENT="${1:-dev}"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🚀 Deploying Classified Ads System - Environment: $ENVIRONMENT"
echo "📁 Project directory: $PROJECT_DIR"

# Load environment file
if [ -f "$PROJECT_DIR/.env" ]; then
    export $(cat "$PROJECT_DIR/.env" | grep -v '^#' | xargs)
    echo "✅ Loaded environment from .env"
else
    echo "⚠️  .env file not found. Using defaults."
fi

# Function to stop containers
stop_containers() {
    echo "🛑 Stopping containers..."
    cd "$PROJECT_DIR"
    docker-compose down || true
    sleep 2
}

# Function to build images
build_images() {
    echo "🔨 Building Docker images..."
    cd "$PROJECT_DIR"
    docker-compose build --no-cache
}

# Function to start containers
start_containers() {
    echo "🚀 Starting containers..."
    cd "$PROJECT_DIR"
    docker-compose up -d
    sleep 5

    echo "🏥 Checking service health..."

    # Wait for postgres
    echo "⏳ Waiting for PostgreSQL..."
    max_attempts=30
    attempt=0
    while ! docker exec classifiedads-db pg_isready -U ${DB_USER:-postgres} > /dev/null 2>&1; do
        attempt=$((attempt + 1))
        if [ $attempt -ge $max_attempts ]; then
            echo "❌ PostgreSQL failed to start"
            docker-compose logs postgres
            exit 1
        fi
        echo "  ⏳ Attempt $attempt/$max_attempts..."
        sleep 2
    done
    echo "✅ PostgreSQL is ready"

    # Wait for backend
    echo "⏳ Waiting for Backend..."
    max_attempts=30
    attempt=0
    while ! curl -s http://localhost:8080/api/auth/validate > /dev/null 2>&1; do
        attempt=$((attempt + 1))
        if [ $attempt -ge $max_attempts ]; then
            echo "❌ Backend failed to start"
            docker-compose logs backend
            exit 1
        fi
        echo "  ⏳ Attempt $attempt/$max_attempts..."
        sleep 2
    done
    echo "✅ Backend is ready"
}

# Function to show status
show_status() {
    echo ""
    echo "📊 Service Status:"
    echo "=================="
    docker-compose ps

    echo ""
    echo "🌐 Access Points:"
    echo "=================="
    echo "Frontend:    http://localhost:3000"
    echo "Backend API: http://localhost:8080/api"
    echo "PostgreSQL:  localhost:5432"
    echo "Nginx:       http://localhost:80"
}

# Function to view logs
show_logs() {
    echo ""
    echo "📋 Recent Logs:"
    echo "==============="
    docker-compose logs --tail=50 -f
}

# Main deployment logic
case "$ENVIRONMENT" in
    dev)
        echo "📝 Development Deployment"
        build_images
        start_containers
        show_status
        read -p "View logs? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            show_logs
        fi
        ;;
    prod)
        echo "🔒 Production Deployment"
        read -p "This will deploy to production. Continue? (y/n) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo "❌ Deployment cancelled"
            exit 1
        fi
        stop_containers
        build_images
        start_containers
        show_status
        ;;
    *)
        echo "❌ Invalid environment. Use: dev or prod"
        exit 1
        ;;
esac

echo ""
echo "✅ Deployment complete!"
echo ""
echo "📚 Next steps:"
echo "1. Check service status: docker-compose ps"
echo "2. View logs: docker-compose logs -f"
echo "3. Test API: curl http://localhost:8080/api/auth/validate"
echo "4. Access frontend: http://localhost:3000"
