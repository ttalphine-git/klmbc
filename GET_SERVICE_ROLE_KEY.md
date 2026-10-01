# How to Get Your Supabase Service Role Secret

## Step-by-Step Instructions

### 1. Go to Supabase Dashboard
- URL: https://supabase.com/dashboard
- Sign in if needed

### 2. Select Your Project
- Find project: `classified-ads`
- Click it

### 3. Open API Settings
- Left sidebar → **Settings**
- Click **API**

### 4. Find Service Role Secret
- Scroll down to see three sections:
  - Project URL
  - Anon public key
  - **Service Role Secret** ← This one!

### 5. Copy the Secret
- Click the **copy icon** next to "Service Role Secret"
- ⚠️ This is sensitive - don't share publicly!

### 6. Provide to Claude Code

Once you have the key, send it to me and I'll:
- ✅ Create all tables
- ✅ Add all indexes
- ✅ Verify everything works
- ✅ Set up your database ready for use

---

## Security Note

This key:
- ✅ Is needed only once for setup
- ✅ Will be kept private
- ✅ Can be regenerated anytime in Supabase settings
- ✅ Only needed during this setup phase

---

## Once You Have It

Share it with me in one of these ways:
1. **Paste directly in chat** (I'll use it and you can regenerate it after)
2. **Send via secure method** if you prefer

Then I'll immediately:
1. Create all database tables
2. Add performance indexes
3. Verify everything
4. You can regenerate the key after for security

---

## What Happens Next?

Once migrations run:
```
✅ users table - for user accounts
✅ classified_ads table - for posts
✅ ad_images table - for photos
✅ schema_version table - for migration tracking
✅ All indexes - for fast queries
```

Then you can:
- Start backend: mvn spring-boot:run
- Start frontend: npm run dev
- Create accounts
- Post ads
- Upload images
```

---

**Ready? Get your Service Role Secret and paste it here!** 🚀
