.PHONY: help build up down logs status clean rebuild dev-backend dev-frontend test

help:
	@echo "Classified Ads System - Available Commands"
	@echo "=========================================="
	@echo "make build              - Build Docker images"
	@echo "make up                 - Start all services"
	@echo "make down               - Stop all services"
	@echo "make restart            - Restart all services"
	@echo "make logs               - View service logs"
	@echo "make logs-backend       - View backend logs"
	@echo "make logs-frontend      - View frontend logs"
	@echo "make logs-postgres      - View postgres logs"
	@echo "make status             - Show service status"
	@echo "make clean              - Remove all containers and volumes"
	@echo "make rebuild            - Rebuild images and restart services"
	@echo "make migrate            - Run database migrations"
	@echo "make shell-backend      - SSH into backend container"
	@echo "make shell-postgres     - SSH into postgres container"
	@echo "make test-api           - Test API endpoints"
	@echo "make prod-deploy        - Deploy to production"

build:
	@echo "🔨 Building Docker images..."
	docker-compose build

up:
	@echo "🚀 Starting services..."
	docker-compose up -d
	@sleep 3
	@echo "✅ Services started"
	@make status

down:
	@echo "🛑 Stopping services..."
	docker-compose down

restart: down up

logs:
	@docker-compose logs -f

logs-backend:
	@docker-compose logs -f backend

logs-frontend:
	@docker-compose logs -f frontend

logs-postgres:
	@docker-compose logs -f postgres

status:
	@echo "📊 Service Status:"
	@echo "=================="
	@docker-compose ps
	@echo ""
	@echo "🌐 Access Points:"
	@echo "=================="
	@echo "Frontend:    http://localhost:3000"
	@echo "Backend API: http://localhost:8080/api"
	@echo "PostgreSQL:  localhost:5432"

clean:
	@echo "🧹 Cleaning up..."
	docker-compose down -v
	@echo "✅ Cleanup complete"

rebuild: clean build up

migrate:
	@echo "📦 Running database migrations..."
	docker-compose exec -T postgres psql -U postgres -d classifiedads -f /docker-entrypoint-initdb.d/V1__Initial_Schema.sql
	docker-compose exec -T postgres psql -U postgres -d classifiedads -f /docker-entrypoint-initdb.d/V2__Add_Indexes.sql

shell-backend:
	@docker-compose exec backend /bin/bash

shell-postgres:
	@docker-compose exec postgres psql -U postgres -d classifiedads

test-api:
	@echo "🧪 Testing API endpoints..."
	@echo "Testing auth validation..."
	@curl -X GET http://localhost:8080/api/auth/validate || true
	@echo ""
	@echo "✅ API tests complete"

dev-backend:
	@cd backend && mvn clean install && mvn spring-boot:run

dev-frontend:
	@cd frontend && npm install && npm run dev

prod-deploy:
	@echo "🔒 Production Deployment"
	@bash deploy.sh prod

.DEFAULT_GOAL := help
