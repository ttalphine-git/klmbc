# How to Share Supabase Project with Team Members

Complete guide to collaborate on Supabase project and share access securely.

---

## Method 1: Share Supabase Project Access (Recommended)

### 1.1 Invite Team Members to Supabase Organization

**Step 1: Create Supabase Organization** (if you don't have one)
1. Go to: https://supabase.com
2. Click your profile (top right)
3. Click **Organizations**
4. Click **New Organization**
5. Name: `Classified Ads Team`
6. Create

**Step 2: Invite Team Members**
1. In organization, click **Settings** → **Members**
2. Click **Invite members**
3. Enter team member's email: `teammate@example.com`
4. Select role:
   - **Owner**: Full access
   - **Developer**: Can manage database & auth
   - **Restricted**: Limited access
5. Click **Invite**
6. Team member gets email with invite link

**Step 3: Team Member Accepts Invite**
- They click email link
- Sign in with GitHub/email
- Project appears in their Supabase dashboard
- They can now access database, storage, auth, etc.

### 1.2 Manage Team Member Roles

In **Settings → Members**:

| Role | Database | Auth | Storage | Billing |
|------|----------|------|---------|---------|
| Owner | ✅ Full | ✅ Full | ✅ Full | ✅ Yes |
| Developer | ✅ Full | ✅ Full | ✅ Full | ❌ No |
| Restricted | ⚠️ Read-only | ⚠️ Read-only | ⚠️ Read-only | ❌ No |

---

## Method 2: Share Connection String (For Backend Access)

### 2.1 Get Connection Details

**For Backend Developers:**

1. Go to Supabase Dashboard
2. Click **Settings** → **Database**
3. Under **Connection String**, copy:
   - **JDBC** (for Java/Spring Boot)
   - **PostgreSQL** (for Node.js/Python)
   - **URI** (for generic apps)

### 2.2 Share Securely

**⚠️ NEVER share in plain text!**

**Option A: Use Environment Variable Management**

1. Create `.env` file (NOT in git):
```env
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_KEY=eyJhbGciOi...
DB_CONNECTION_STRING=postgresql://postgres:PASSWORD@db.xxx.supabase.co:5432/postgres
```

2. Share via secure channel:
   - **1Password** / **Bitwarden** (password managers)
   - **Notion** (shared private doc)
   - **Slack** (direct message, then delete)

**Option B: Use GitHub Secrets** (For CI/CD)

If team is using GitHub:

1. Go to repo: **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Name: `SUPABASE_DB_URL`
4. Value: Your connection string
5. Click **Add secret**

Use in workflows:
```yaml
env:
  DB_URL: ${{ secrets.SUPABASE_DB_URL }}
```

**Option C: Vercel/Railway Secrets** (For Production)

If deploying backend:

**Railway.app:**
1. Go to project
2. Click service
3. Click **Variables**
4. Add:
   - Key: `DB_CONNECTION_STRING`
   - Value: `postgresql://postgres:PASSWORD@db.xxx.supabase.co:5432/postgres`

**Vercel:**
1. Go to project
2. Settings → **Environment Variables**
3. Add variables
4. Selectively share with team

---

## Method 3: Share Database Read-Only Access

For QA/testers who need to view data only:

### 3.1 Create Read-Only User in PostgreSQL

In Supabase SQL Editor:

```sql
-- Create read-only user
CREATE USER readonly_user WITH PASSWORD 'readonly_password_123';

-- Grant read-only access
GRANT CONNECT ON DATABASE postgres TO readonly_user;
GRANT USAGE ON SCHEMA public TO readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;

-- For future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly_user;
```

Share connection string:
```
postgresql://readonly_user:readonly_password_123@db.xxx.supabase.co:5432/postgres
```

Person can:
- ✅ View all data
- ❌ Cannot insert/update/delete

---

## Method 4: Share via GitHub (Code Repository)

### 4.1 Create GitHub Repository

1. Go to: https://github.com/new
2. Name: `classifiedads-kerala`
3. Description: "Classified Ads System for Kerala"
4. Choose: **Private** (for security)
5. Create repository

### 4.2 Push Code to GitHub

```powershell
cd d:\KLMBCS

# Initialize git (if not done)
git init

# Add remote
git remote add origin https://github.com/yourusername/classifiedads-kerala.git

# Push to GitHub
git add -A
git commit -m "Initial commit: Classified Ads System"
git branch -M main
git push -u origin main
```

### 4.3 Invite Team Members to Repository

1. Go to repo on GitHub
2. Click **Settings** → **Collaborators**
3. Click **Add people**
4. Search teammate's GitHub username
5. Select role:
   - **Maintain**: Can push, manage issues
   - **Push**: Can push code
   - **Pull**: Read-only
6. Send invite

### 4.4 Share Supabase Credentials Securely

**Create `SETUP.md` with instructions:**

```markdown
# Setup Instructions for Team

## Database Connection

1. Create `.env` file (DO NOT COMMIT):
```env
DB_CONNECTION_STRING=postgresql://postgres:PASSWORD@db.xxx.supabase.co:5432/postgres
JWT_SECRET=your-secret-key
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
```

2. Credentials shared via secure channel (1Password, Notion)

3. Backend: `mvn spring-boot:run`
4. Frontend: `npm run dev`
```

**Add to `.gitignore`:**
```
.env
.env.local
*.pem
*.key
secrets/
```

---

## Method 5: Using Docker Environment Files

For team running with Docker:

### 5.1 Create `.env` Template

File: `d:\KLMBCS\.env.example`

```env
# Supabase Database
DB_CONNECTION_STRING=postgresql://postgres:PASSWORD@db.xxxxx.supabase.co:5432/postgres?sslmode=require

# Backend
BACKEND_PORT=8080
JWT_SECRET=your-secret-here
CORS_ORIGINS=http://localhost:3000,http://localhost:5173

# Frontend
FRONTEND_PORT=5173
VITE_API_URL=http://localhost:8080/api

# Storage
FILE_UPLOAD_DIR=./uploads
```

### 5.2 Share Instructions

Create `TEAM_SETUP.md`:

```markdown
# Team Development Setup

## 1. Clone Repository
git clone https://github.com/team/classifiedads.git
cd classifiedads

## 2. Get Credentials
- Credentials shared in 1Password/Notion
- Copy values from there

## 3. Create .env File
cp .env.example .env
# Edit .env with credentials from secure storage

## 4. Start Services
docker-compose up --build

## 5. Access
- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- Database: Supabase dashboard
```

---

## Security Best Practices

### ✅ DO:
- ✅ Use environment variables for secrets
- ✅ Add `.env` to `.gitignore`
- ✅ Share credentials via password manager (1Password, Bitwarden)
- ✅ Rotate passwords regularly
- ✅ Use read-only users for testers
- ✅ Restrict Supabase access by role
- ✅ Enable 2FA on Supabase account
- ✅ Use secrets in GitHub/Railway/Vercel

### ❌ DON'T:
- ❌ Commit `.env` files to git
- ❌ Share passwords in Slack/email
- ❌ Use simple passwords
- ❌ Share master password with everyone
- ❌ Store credentials in code comments
- ❌ Use same password for all environments
- ❌ Share production credentials freely

---

## Complete Team Workflow

### Architecture

```
GitHub Repository
├── backend/
├── frontend/
├── docker-compose.yml
├── .env.example (public)
├── .env (PRIVATE - gitignored)
└── TEAM_SETUP.md

↓

Supabase Project (Shared)
├── Database (Shared access)
├── Storage (Shared access)
└── Auth (Shared access)

↓

Individual Developer Machines
├── Local .env (from secure storage)
├── Running backend:8080
├── Running frontend:5173
└── Connected to shared Supabase
```

### Example Workflow

**Developer 1: Setup**
```
1. Clone repo
2. Get Supabase credentials from 1Password
3. Create .env locally
4. Run: docker-compose up
5. See shared database in Supabase dashboard
```

**Developer 2: Make Changes**
```
1. Pull latest: git pull
2. Create branch: git checkout -b feature/search
3. Make changes to backend/frontend
4. Test with shared Supabase
5. Push: git push origin feature/search
6. Create Pull Request
```

**Lead: Review & Merge**
```
1. Review code
2. Merge to main
3. Deploy to production (Railway/Render)
4. Changes live for all users
```

---

## Real-World Example

### Setup for 3-Person Team

**Team:**
- Alice (Backend Lead)
- Bob (Frontend Developer)
- Carol (QA Tester)

**Step 1: Supabase Organization**
```
Organization: Classified Ads Team
├── Alice (Owner)
├── Bob (Developer)
└── Carol (Restricted - Read-only)
```

**Step 2: GitHub Repository**
```
Repo: classifiedads-kerala (Private)
├── Alice (Maintain)
├── Bob (Push)
└── Carol (Pull)
```

**Step 3: Shared Credentials**
```
1Password Vault:
├── Supabase Password
├── Database Connection String
└── JWT Secret
```

**Step 4: Local Setup**

Alice:
```powershell
# Clones repo
git clone https://github.com/team/classifiedads.git

# Gets creds from 1Password
# Creates .env locally

# Starts services
docker-compose up
mvn spring-boot:run
npm run dev
```

Bob:
```powershell
# Same steps
# Works on frontend feature

git checkout -b feature/map-search
# Makes changes
git push origin feature/map-search
```

Carol:
```powershell
# Pulls latest
git pull

# Tests app with shared Supabase
# Views data in Supabase dashboard (read-only)
# Reports bugs via GitHub Issues
```

---

## Manage Access Levels

### For Different Roles

**Backend Developer:**
- ✅ Full database access
- ✅ Can modify schema
- ✅ Can see connection strings
- ✅ Read-write to storage

**Frontend Developer:**
- ✅ Read-only database
- ✅ Can use API endpoints
- ✅ Can upload files to storage
- ❌ Cannot modify schema

**QA Tester:**
- ✅ Read-only database
- ✅ Can test app
- ✅ Can view data
- ❌ Cannot modify anything

**DevOps Engineer:**
- ✅ Full access
- ✅ Can manage backups
- ✅ Can configure monitoring
- ✅ Can manage secrets

---

## Revoke Access

If team member leaves:

### Supabase
1. Go to org **Settings** → **Members**
2. Find member
3. Click **...** → **Remove**

### GitHub
1. Go to repo **Settings** → **Collaborators**
2. Find member
3. Click **...** → **Remove**

### Secrets
1. Rotate all passwords
2. Generate new database password
3. Update `.env` for remaining team
4. Share new credentials

---

## Communication Channels

### For Sharing Access

**Real-time Coordination:**
- Slack / Discord / Teams

**Code Collaboration:**
- GitHub (Pull Requests)
- GitHub Issues (Bug tracking)

**Documentation:**
- Notion / Wiki
- GitHub README
- Confluence

**Secrets Management:**
- 1Password (Recommended)
- Bitwarden
- LastPass
- Vault

---

## Monitoring & Debugging

### See Who Changed What

**Supabase:**
- Go to **SQL Editor**
- Check database audit logs
- See schema changes

**GitHub:**
- See commit history: `git log`
- See who pushed what: `git log --oneline --graph`
- PR reviews show who approved

### Database Usage

**Supabase Dashboard:**
1. Click **Usage** (top right)
2. See:
   - Database rows
   - Storage usage
   - API calls
   - Bandwidth

---

## Cost Sharing

### Who Pays?

**Supabase Organization Owner** (Alice) pays:
- $25/month Pro plan (or usage-based)
- Can be split among team

**Railway Backend Hosting**:
- $7/month or pay-as-you-go
- Owner pays or reimburse

**Vercel Frontend Hosting**:
- Free for public sites
- Owner gets invites others

### Split Costs

Option 1: Owner pays all, others reimburse
Option 2: Use shared team account
Option 3: Each person pays for their resource

---

## Troubleshooting

### "Can't access Supabase project"
```
Solution:
1. Check you're invited to organization
2. Check email for invite link
3. Sign in to same email as invite
4. Check member role in Settings
```

### "Connection refused" on shared database
```
Solution:
1. Verify connection string
2. Check Supabase project is running
3. Test: psql postgresql://...
4. Verify IP whitelist (if any)
```

### "Credentials expired"
```
Solution:
1. Get new credentials from 1Password
2. Rotate password in Supabase Settings
3. Update .env locally
4. Restart application
```

### "Too many people accessing database"
```
Solution:
1. Use connection pooling
2. Upgrade Supabase plan
3. Limit concurrent connections in app.yml
```

---

## Quick Reference

| Task | Who | How |
|------|-----|-----|
| Invite to Supabase | Owner | Org Settings → Members → Invite |
| Share credentials | Anyone | 1Password shared vault |
| Invite to GitHub | Owner | Repo Settings → Collaborators |
| Revoke access | Owner | Settings → Remove |
| Change role | Owner | Settings → Edit role |
| See who changed data | Anyone | Supabase logs |
| Reset password | Owner | Supabase Settings → Change password |

---

## Next Steps

1. **Invite team members to Supabase organization**
2. **Create GitHub repository (private)**
3. **Invite developers to GitHub**
4. **Share credentials via 1Password**
5. **Everyone clones repo and sets up locally**
6. **Start collaborating!**

---

**You're ready to collaborate! 🚀**
