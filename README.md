# Classified Ads System - Kerala

A modern, full-stack classified ads application for Kerala with map-based browsing, built with Spring Boot, React, and PostgreSQL.

## 🎯 Features

- 🗺️ **Interactive Map** - Browse classified ads on a full-screen Leaflet map
- 📱 **User Registration** - Create accounts with profile pictures
- 📝 **Post Ads** - Create and manage classified ads with images
- 🔍 **View Details** - Click on map markers to see detailed ad information
- 👤 **User Profiles** - Manage profile information and posted ads
- 🔐 **JWT Authentication** - Secure authentication system
- 📸 **Image Uploads** - Upload multiple images for ads and profile

## 🏗️ Architecture

### Monolithic Stack
- **Backend**: Java Spring Boot 3.2 with Spring Security & JWT
- **Frontend**: React 18 + TypeScript + Vite + Tailwind CSS + Leaflet
- **Database**: PostgreSQL 15 (file-system hosted initially)
- **Reverse Proxy**: Nginx
- **Containerization**: Docker & Docker Compose

### Directory Structure
```
KLMBCS/
├── backend/                          # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/classifiedads/
│   │   │   ├── controller/          # REST endpoints
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access
│   │   │   ├── model/               # Entities and DTOs
│   │   │   ├── security/            # JWT and auth
│   │   │   └── config/              # Spring configs
│   │   └── resources/
│   │       ├── application.yml      # Configuration
│   │       └── db/migration/        # SQL migrations
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                         # React TypeScript frontend
│   ├── src/
│   │   ├── api/                     # API client functions
│   │   ├── components/              # React components
│   │   ├── pages/                   # Page components
│   │   ├── store/                   # Zustand state management
│   │   ├── types/                   # TypeScript types
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── index.css
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml               # Multi-container orchestration
├── nginx.conf                        # Reverse proxy config
├── .gitignore
├── .env.example
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Node.js 18+ (for local development)
- Java 17+ (for local development)
- PostgreSQL 15+ (for local development)

### 1. Clone & Setup

```bash
cd /d/KLMBCS
cp .env.example .env
```

### 2. Update .env (Optional)
Edit `.env` to customize ports, passwords, etc.

### 3. Start with Docker Compose

```bash
docker-compose up --build
```

Services will start at:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432
- **Nginx Reverse Proxy**: http://localhost:80

### 4. Local Development (Without Docker)

**Backend**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Frontend**:
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

**PostgreSQL**:
```bash
# Start PostgreSQL locally and create database
createdb classifiedads
# Run migration scripts from backend/src/main/resources/db/migration/
```

## 📋 API Endpoints

### Authentication
```
POST   /api/auth/register       - User registration
POST   /api/auth/login          - User login
GET    /api/auth/validate       - Validate token
```

### Users
```
GET    /api/users/me            - Get current user profile
GET    /api/users/{userId}      - Get user profile
PUT    /api/users/me            - Update profile
POST   /api/users/me/avatar     - Upload profile avatar
```

### Classified Ads
```
GET    /api/ads                 - List all ads (paginated)
GET    /api/ads/map-points      - Get map points (for map display)
GET    /api/ads/{id}            - Get ad details
GET    /api/ads/category/{category} - Filter by category
GET    /api/ads/user/{userId}   - Get user's ads
POST   /api/ads                 - Create new ad
PUT    /api/ads/{id}            - Update ad
DELETE /api/ads/{id}            - Delete ad
POST   /api/ads/{id}/images     - Upload images
DELETE /api/ads/{id}/images/{imageId} - Delete image
```

### Files
```
POST   /api/files/upload        - Upload file
GET    /api/files/{filename}    - Download file
```

## 🗄️ Database

### Schema
- **users** - User accounts and profiles
- **classified_ads** - Posted classified ads
- **ad_images** - Images for ads
- **schema_version** - Migration tracking

### Running Migrations
Migrations run automatically on Docker startup. For manual runs:

```bash
# From backend directory
psql -U postgres -d classifiedads -f src/main/resources/db/migration/V1__Initial_Schema.sql
psql -U postgres -d classifiedads -f src/main/resources/db/migration/V2__Add_Indexes.sql
```

## 📦 Build & Deployment

### Build Docker Images
```bash
# Backend
docker build -t classifiedads-backend:latest ./backend

# Frontend
docker build -t classifiedads-frontend:latest ./frontend
```

### Deploy to Digital Ocean

1. **Push images to Docker Hub**:
```bash
docker tag classifiedads-backend:latest youruser/classifiedads-backend:latest
docker push youruser/classifiedads-backend:latest
```

2. **SSH into Digital Ocean droplet**:
```bash
ssh root@your-droplet-ip
```

3. **Clone repository**:
```bash
git clone <your-repo> /opt/classifiedads
cd /opt/classifiedads
```

4. **Create .env file**:
```bash
cp .env.example .env
# Edit .env with production values
```

5. **Start services**:
```bash
docker-compose up -d
```

6. **Setup SSL (Let's Encrypt)**:
```bash
sudo apt-get install certbot python3-certbot-nginx
sudo certbot certonly --nginx -d your-domain.com
# Update nginx.conf with SSL certificate paths
```

## 🔧 Configuration

### Backend (application.yml)
- Database connection
- JWT secret and expiration
- File upload directory
- CORS origins
- Logging levels

### Frontend (vite.config.ts)
- API base URL
- Dev server configuration
- Build optimization

### Nginx
- Reverse proxy configuration
- SSL/TLS setup
- Compression settings
- Cache policies

## 📝 Development Notes

### Port Assignments
- **5432** - PostgreSQL
- **8080** - Spring Boot Backend
- **3000** - Frontend (production)
- **5173** - Frontend Dev Server
- **80** - Nginx HTTP
- **443** - Nginx HTTPS

### File Uploads
- Location: `/app/uploads/`
- Subdirectories: `users/{userId}/`, `ads/{adId}/`
- Allowed types: JPG, JPEG, PNG, GIF, WebP
- Max size: 50MB

### Logging
- Backend: `logs/` directory
- Frontend: Browser console
- Nginx: `/var/log/nginx/`

## 🔒 Security

- ✅ JWT-based authentication
- ✅ Password hashing with BCrypt
- ✅ CORS configuration
- ✅ File upload validation
- ✅ SQL injection prevention (JPA)
- ✅ XSS protection via CSP headers

**TODO**: Add HTTPS, rate limiting, input validation

## 🐛 Troubleshooting

### PostgreSQL connection fails
```bash
# Check if postgres container is running
docker ps

# Check postgres logs
docker logs classifiedads-db

# Verify connection string in application.yml
```

### Images not uploading
```bash
# Ensure uploads directory exists with proper permissions
docker exec classifiedads-backend mkdir -p /app/uploads
docker exec classifiedads-backend chmod 755 /app/uploads
```

### Frontend not connecting to backend
```bash
# Check CORS configuration in SecurityConfig.java
# Ensure VITE_API_URL is correctly set in .env
# Check browser console for CORS errors
```

## 📚 Next Steps

1. **Production Database**: Migrate to managed PostgreSQL
2. **Object Storage**: Move uploads to S3/DigitalOcean Spaces
3. **Email Verification**: Add email confirmation
4. **Advanced Search**: Full-text search in ads
5. **Messaging**: Add chat between buyers and sellers
6. **Ratings**: Implement user rating system
7. **Analytics**: Add monitoring and metrics

## 📞 Support

For issues or questions, create an issue in the repository.

## 📄 License

Proprietary - All rights reserved
