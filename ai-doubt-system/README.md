# AI-Powered Student Doubt Resolution System

This app lives under /ai-doubt-system so the existing assignment portal remains untouched.

Stack:
- HTML5, CSS3, vanilla JavaScript
- Vercel serverless API
- Supabase Auth, PostgreSQL, and Storage
- Swappable OpenAI-compatible AI provider

Required Vercel environment variables:
- SUPABASE_URL
- SUPABASE_ANON_KEY
- SUPABASE_SERVICE_ROLE_KEY
- AI_API_KEY
- AI_BASE_URL (default: https://api.openai.com/v1)
- AI_MODEL (default: gpt-4o-mini)

Run supabase/migrations/001_ai_doubt_system.sql against the existing Supabase project before using the app.
