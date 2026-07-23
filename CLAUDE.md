# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

A learning project: a Netflix-style streaming app built **backend-first**, feature by feature.
Backend is a Spring Boot REST API; an Angular frontend is planned but not built yet.
The author is a Spring Boot beginner — explain each feature simply before major changes, one feature at a time.

## Tech stack
- **Java 17** (Temurin), **Spring Boot 4.1**
- **Spring Web MVC**, **Spring Data JPA / Hibernate**, **Spring Security** (stateless JWT)
- **PostgreSQL 17** database (`netflixclone`)
- **JWT** via jjwt 0.12.6, **BCrypt** password hashing
- **Lombok**, **Maven** (wrapper — no global Maven needed)
- Frontend (planned): **Angular 21**

## Layout
```
backend/                         Spring Boot API
  src/main/java/com/saian/netflixclone/
    config/       security, JWT, static-file serving
    controller/   HTTP endpoints (@RestController)
    service/      business logic (@Service, @Transactional)
    dto/          request/response records + Mapper
    entity/       JPA entities (DB tables)
    repository/   Spring Data JPA interfaces
    exception/    custom exceptions + global handler
    enums/        Role, VideoCategory
  src/main/resources/application.properties
  uploads/        uploaded media (gitignored)
frontend/          (empty — Angular app to come)
```
Each package has an `AGENTS.md` describing its purpose and conventions — read it before editing that layer.

## Commands (Windows / PowerShell)
Run from the `backend/` directory. Uses the Maven **wrapper** — no global Maven needed.
```powershell
cd backend
.\mvnw.cmd spring-boot:run              # run the app (DevTools hot-restarts on recompile)
.\mvnw.cmd clean package               # build the jar
.\mvnw.cmd test                        # run all tests
.\mvnw.cmd test -Dtest=ClassName#method  # run a single test
```
App serves on **http://localhost:8080**. The author runs the app in their own terminal;
DevTools hot-restarts on recompile, so a fresh `spring-boot:run` is usually unnecessary after edits.

Config lives in `application.properties` (DB URL, JWT secret, upload dir). Email is
**disabled in dev** (`app.mail.enabled=false`) — verification/reset links are printed to the
console log instead of being emailed.

### Database (PostgreSQL 17)
DB `netflixclone`, user `postgres`. `ddl-auto=update` so Hibernate creates/updates tables on startup.
Inspect data directly with psql (note the actual column is `published`, **not** `is_published`):
```powershell
$env:PGPASSWORD="anirudh1"; & "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -d netflixclone -c "SELECT * FROM videos;"
```

## Architecture conventions
- **Layering:** controller → service → repository. Controllers stay thin; logic lives in services.
- **DTOs at the edges:** controllers accept/return DTO records, never JPA entities. `dto/Mapper` converts.
- **Errors:** throw a custom exception from `exception/`; `GlobalExceptionHandler` maps it to an HTTP status. Don't build error responses by hand.
- **Auth:** stateless JWT. `JwtAuthFilter` validates the token and loads the user (with role) on every request. Admin-only endpoints use `@PreAuthorize("hasRole('ADMIN')")`.
- **Validation:** jakarta `@Valid` on request DTOs; failures become 400s with field errors.
- **Security whitelist:** only `/api/auth/**`, `/uploads/**`, and `GET /api/videos/*/stream` are public; everything else requires a valid JWT. Per-user endpoints key off `authentication.getName()` (the JWT email), never a request-body user id — this is what gives per-user isolation.

## API surface (current)
- `POST /api/auth/signup` · `POST /api/auth/login` · `GET /api/auth/verify?token=` · `POST /api/auth/forgot-password` · `POST /api/auth/reset-password`
- `GET /api/users/me` · `POST /api/users/change-password`
- `GET/POST/DELETE /api/users/me/favorites[/{videoId}]` — list / add / remove favorites (many-to-many `user_favorites` join, owning side is `User.favorites`)
- `GET /api/videos/published` · `GET /api/videos/{id}` · `GET /api/videos/{id}/stream` (HTTP Range → 206, 1 MB chunks, so the player can seek)
- Admin only (`@PreAuthorize("hasRole('ADMIN')")`): `GET/POST/PUT/DELETE /api/videos` and file upload

## Feature status
1–6 ✅ foundation, signup+verify, login+JWT, password mgmt, admin video CRUD, file upload.
7 ✅ Video streaming with HTTP Range/seek. 8 ✅ Favorites/watchlist. 9 ⏳ Angular frontend (next).

## Testing
No automated tests yet — features are verified manually against the running app via **curl / PowerShell**
(Postman isn't scriptable here). Typical flow: signup → grab the verify token from the console log (or DB)
→ `GET /api/auth/verify?token=` → login for a JWT → call protected endpoints with `Authorization: Bearer <token>`.
Note: PowerShell 5.1 does **not** support `-SkipHttpErrorCheck`; use try/catch and read `$_.Exception.Response.StatusCode`.

## Workflow
**One commit per feature** (`feat: ...`) — not batched. Commit from the repo root, then push to
`https://github.com/anirudh11233/netflix-clone`. Commit attribution: author `anirudh11233`, co-author `Claude Opus 4.8`.
```powershell
git add . && git commit -m "feat: ..." && git push
```
⚠️ `application.properties` currently carries the DB password and JWT secret in the repo — keep the repo private or move these to env vars before making it public.
