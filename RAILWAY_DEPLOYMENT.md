# Deploy to Railway - Complete Guide

Deploy your entire Classified Ads System to Railway in 10 minutes! No local build needed.

---

## Why Railway?

✅ **Solves Everything:**
- No local disk space issues
- Cloud-based builds (powerful servers)
- Automatic deployments
- Free tier available
- PostgreSQL/Supabase compatible
- One-click rollbacks
- Monitoring & logs included

---

## 🚀 Step 1: Push Code to GitHub

### 1.1 Create GitHub Repository

```powershell
cd d:\KLMBCS

# Initialize git
git init

# Add all files
git add -A

# Commit
git commit -m "Initial commit: Classified Ads System"

# Create repo on GitHub: https://github.com/new
# Then add remote:
git remote add origin https://github.com/YOUR_USERNAME/classifiedads.git

# Push to GitHub
git branch -M main
git push -u origin main
```

### 1.2 Update .gitignore

Ensure `.gitignore` already has (it should):
```
.env
.env.local
*.pem
*.key
.DS_Store
uploads/
target/
node_modules/
```

---

## 🚆 Step 2: Deploy Backend to Railway

### 2.1 Create Railway Account

1. Go to: https://railway.app
2. Sign up with GitHub
3. Authorize Railway

### 2.2 Create New Project

1. Click **New Project**
2. Select **Deploy from GitHub repo**
3. Authorize GitHub
4. Select repo: `classifiedads`
5. Confirm deployment

### 2.3 Configure Service

After repo connects:

1. Click your backend service
2. Go to **Settings** tab
3. Set **Root Directory**: `backend`

### 2.4 Add Environment Variables

Click **Variables** and add:

```env
DB_CONNECTION_STRING=postgresql://postgres:PASSWORD@db.obmqtkbheuylezjntayi.supabase.co:5432/postgres?sslmode=require
DB_USER=postgres
DB_PASSWORD=YOUR_SUPABASE_PASSWORD
JWT_SECRET=classifiedads-secret-key-min-32-characters
JWT_EXPIRATION=86400000
CORS_ORIGINS=http://localhost:3000,https://YOUR_FRONTEND_DOMAIN.com
FILE_UPLOAD_DIR=/app/uploads
```

### 2.5 Build Configuration

Railway auto-detects Maven. It will:
1. Read `backend/pom.xml`
2. Run: `mvn clean package`
3. Deploy JAR
4. Start: `java -jar target/*.jar`

✅ **Backend deploys automatically!**

---

## 🎨 Step 3: Deploy Frontend to Vercel (Easier)

### 3.1 Connect Vercel

1. Go to: https://vercel.com
2. Sign up with GitHub
3. Click **Import Project**
4. Select `classifiedads` repo
5. Select **Root Directory**: `frontend`

### 3.2 Add Environment Variables

Click **Environment Variables**:

```
VITE_API_URL=https://YOUR_RAILWAY_BACKEND_URL/api
```

### 3.3 Deploy

Click **Deploy**

✅ **Frontend live instantly!**

---

## 🔗 Alternative: Deploy Frontend to Railway Too

If you prefer everything on Railway:

### Option A: Deploy Frontend to Railway

1. In Railway, click **New Service**
2. Click **GitHub Repo**
3. Select `classifiedads`
4. Set **Root Directory**: `frontend`
5. Set **Build Command**: `npm install && npm run build`
6. Set **Start Command**: `npm run preview`

### Option B: Use Railway with Custom Build

Create `railway.json` in root:

```json
{
  "build": {
    "builder": "dockerfile"
  }
}
```

---

## 🔑 Getting Your API Key from Railway

After backend deploys:

1. Go to Railway dashboard
2. Click your backend service
3. Click **Settings**
4. Copy **Public URL** (e.g., `https://classifiedads-backend.railway.app`)

This URL is your API endpoint!

---

## 🌐 Get Your URLs

After deployment:

**Backend:**
```
https://classifiedads-backend.railway.app
```

**Frontend (Vercel):**
```
https://classifiedads.vercel.app
```

**Database (Supabase):**
```
https://obmqtkbheuylezjntayi.supabase.co
```

---

## ✅ Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] Railway account created
- [ ] Backend service created
- [ ] Environment variables added (Supabase connection)
- [ ] Backend deployed successfully
- [ ] Vercel account created
- [ ] Frontend deployed
- [ ] Test API: `curl https://YOUR_BACKEND.railway.app/api/auth/validate`
- [ ] Open frontend URL
- [ ] Create test account
- [ ] Post test ad
- [ ] Check data in Supabase

---

## 🧪 Test Your Live System

### 1. Test Backend API

```powershell
$backendUrl = "https://YOUR_RAILWAY_BACKEND_URL"

# Test connection
curl "$backendUrl/api/auth/validate"

# Should return: true ✅
```

### 2. Test Frontend

Open: `https://YOUR_VERCEL_URL`

1. Click **Sign Up**
2. Create account
3. Check Supabase: users table has new user ✅

### 3. Test Full Flow

1. Post ad
2. Upload image
3. View on map
4. Check Supabase: classified_ads table has post ✅

---

## 📊 Architecture After Railway

```
GitHub Repository
    ↓
┌───────────────────────────────────────┐
│   Railway Infrastructure              │
├───────────────┬───────────────────────┤
│ Backend       │  Frontend             │
│ Spring Boot   │  Node.js Preview      │
│ :8080         │  :3000                │
│ Auto-deploys  │  Auto-deploys         │
└───────────────┴───────────────────────┘
        ↓
    Supabase
    PostgreSQL
    (Already set up)
```

---

## 🔄 Continuous Deployment

After setup, deployments are automatic:

1. Make code changes locally
2. Push to GitHub: `git push`
3. Railway auto-detects changes
4. Backend: Auto-rebuilds with Maven
5. Frontend: Auto-builds with Vite
6. Both live in 2-5 minutes!

---

## 💰 Cost Breakdown

| Service | Free Tier | Paid |
|---------|-----------|------|
| Railway | 5GB/month | $5+ |
| Vercel | Unlimited | Pay overages |
| Supabase | 500MB DB | $25+ |
| **Total** | ~Free | ~$30/month |

---

## 🚨 Important Notes

### Environment Variables

**Never commit secrets!** Use Railway/Vercel dashboards:

❌ DON'T:
```
DB_PASSWORD=mypassword  # in .env
```

✅ DO:
```
# Railway dashboard → Variables
DB_PASSWORD=mypassword
```

### CORS Configuration

Backend needs to know frontend URL:

```env
CORS_ORIGINS=https://your-frontend-url.vercel.app
```

Update in Railway variables after getting frontend URL.

### Database Connection

Ensure Supabase connection works:

```powershell
# Test from Railway backend logs:
# Should see: "Connection successful"
```

---

## 🐛 Troubleshooting

### Backend won't build

**Solution:**
1. Check Railway build logs
2. Usually disk space (but Railway has plenty)
3. Or dependency issues

### Frontend shows blank page

**Solution:**
1. Check browser console
2. Verify `VITE_API_URL` is correct
3. Check CORS in backend

### Database connection fails

**Solution:**
1. Verify Supabase password in Railway env vars
2. Test connection: `psql postgresql://...`
3. Check IP whitelist (if any)

### API returns 401 Unauthorized

**Solution:**
1. Verify JWT_SECRET is same in backend env vars
2. Check token generation
3. Look at backend logs

---

## 📞 Support

- **Railway Docs**: https://docs.railway.app
- **Vercel Docs**: https://vercel.com/docs
- **Backend Logs**: Railway Dashboard → Logs
- **Frontend Logs**: Browser Console → F12

---

## 🎉 You're Done!

Your entire system is:
- ✅ Deployed to Railway (backend)
- ✅ Deployed to Vercel (frontend)
- ✅ Connected to Supabase (database)
- ✅ Live on the internet
- ✅ Auto-scaling
- ✅ Monitoring active

**Congratulations!** Your Classified Ads System is production-ready! 🚀

---

## Next Steps

1. **Custom Domain** (optional)
   - Point your domain to Vercel
   - Get automatic HTTPS

2. **Monitoring**
   - Watch Railway logs
   - Monitor Supabase usage
   - Set up alerts

3. **Improvements**
   - Add email verification
   - Add search functionality
   - Add user messaging

4. **Scale**
   - Upgrade Railway plan
   - Upgrade Supabase tier
   - Add CDN for images

---

**All set for production deployment!** 🌍
