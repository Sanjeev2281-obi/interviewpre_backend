# Backend Service (Spring Boot)

This repository contains the backend service for the SaaS Platform, built using Spring Boot.

## Required Environment Variables

To run the application locally or in production, you must set the following environment variables:

| Variable Name | Description | Example / Recommended |
| :--- | :--- | :--- |
| `DB_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://ep-red-bonus.neon.tech/neondb` |
| `DB_USERNAME` | PostgreSQL database username | `neondb_owner` |
| `DB_PASSWORD` | PostgreSQL database password | `password_string` |
| `JWT_SECRET` | Secret key used for signing JWT tokens | A secure, random 256-bit string |
| `RAZORPAY_KEY_ID` | Razorpay Key ID | `rzp_test_xxxxxx` (development) or `rzp_live_xxxxxx` (production) |
| `RAZORPAY_KEY_SECRET` | Razorpay Key Secret | Secure secret key for Razorpay API |
| `GEMINI_API_KEY` | Google Gemini API Key | API key for `gemini-2.0-flash` integrations |

---

## Local Setup Instructions

There are two primary ways to supply these environment variables when running locally:

### Option A: Using Environment Variables (Recommended)

1. Copy `.env.example` to a new file named `.env`:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in your actual credentials.
3. Export the environment variables in your terminal session before starting the application:
   ```bash
   export $(cat .env | grep -v '^#' | xargs)
   ```
   Alternatively, you can run the application with the variables inline:
   ```bash
   DB_URL="jdbc:postgresql://..." DB_USERNAME="..." DB_PASSWORD="..." JWT_SECRET="..." RAZORPAY_KEY_ID="..." RAZORPAY_KEY_SECRET="..." GEMINI_API_KEY="..." ./mvnw spring-boot:run
   ```

### Option B: Using `application-local.properties` (IDE Friendly)

If you use IntelliJ or VS Code, you can configure local properties:

1. Create a file called `src/main/resources/application-local.properties` (this file is pre-configured in `.gitignore` and will never be committed to git).
2. Add your secrets to it:
   ```properties
   spring.datasource.url=jdbc:postgresql://<host>/neondb?sslmode=require
   spring.datasource.username=neondb_owner
   spring.datasource.password=npg_your_password
   jwt.secret=your-secure-local-secret-key-at-least-256-bits
   razorpay.key.id=rzp_test_your_id
   razorpay.key.secret=your_secret
   gemini.api.key=your_gemini_api_key
   ```
3. Run the application with the `local` Spring profile active. For example, using Maven:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

---

## How to Run the Backend

To compile and run the backend locally, run the following:

```bash
# Clean and compile
./mvnw clean compile

# Run the app
./mvnw spring-boot:run
```

The server will start on [http://localhost:8080](http://localhost:8080).

---

## Cloud Deployment (Render / Railway)

When deploying to a cloud hosting platform (e.g. Render or Railway), define the environment variables in the platform's Environment Settings dashboard:

1. Go to your web service settings.
2. Under **Environment Variables** / **Variables**, add each of the keys listed in the [Required Environment Variables](#required-environment-variables) section with their production values.
3. Enable automated build/deploy linking to your Git repository.

---

## CI/CD Pipeline (GitHub Secrets)

If you use GitHub Actions for building or deploying your backend:

1. In your GitHub repository, navigate to **Settings** > **Secrets and variables** > **Actions**.
2. Click **New repository secret**.
3. Add the sensitive credentials:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
   - `RAZORPAY_KEY_ID`
   - `RAZORPAY_KEY_SECRET`
   - `GEMINI_API_KEY`
4. Use them in your workflow YAML file:
   ```yaml
   env:
     DB_URL: ${{ secrets.DB_URL }}
     DB_USERNAME: ${{ secrets.DB_USERNAME }}
     DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
     JWT_SECRET: ${{ secrets.JWT_SECRET }}
     RAZORPAY_KEY_ID: ${{ secrets.RAZORPAY_KEY_ID }}
     RAZORPAY_KEY_SECRET: ${{ secrets.RAZORPAY_KEY_SECRET }}
     GEMINI_API_KEY: ${{ secrets.GEMINI_API_KEY }}
   ```

---

## Guide: Removing Committed Secrets from Git History

If you accidentally committed secrets (like API keys or passwords) to your Git history:

> [!WARNING]
> Changing files and making a new commit does **NOT** delete the secret from Git history. It will still be visible in previous commits. You must rewrite the git history.

### Option A: Remove recent unpushed local commits
If your commits were rejected by GitHub's Push Protection and only exist locally, you can reset your branch to origin and recommit:
```bash
# Reset branch to match origin/main (all edits will remain unstaged in your working folder)
git reset origin/main

# Stage all files
git add .

# Create a clean commit
git commit -m "ready"

# Push to origin
git push origin main
```

### Option B: Purge secrets from pushed history using `git-filter-repo` (Recommended)
If the secrets have already been pushed to GitHub, use `git-filter-repo` to permanently erase them from the history of all commits:

1. Install `git-filter-repo` (requires Python):
   ```bash
   brew install git-filter-repo
   ```
2. Erase the secret value (replace `your_actual_secret_value` with the exact text of the secret):
   ```bash
   git filter-repo --replace-text <(echo "your_actual_secret_value==>REMOVED_SECRET")
   ```
3. Force push back to GitHub:
   ```bash
   git push origin main --force
   ```
