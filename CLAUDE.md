# Netflix Clone — Project Guide

A learning project: a Netflix-style streaming app built **backend-first**, feature by feature.
Backend is a Spring Boot REST API; an Angular frontend is planned but not built yet.

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

## Running the backend (Windows)
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
Requires PostgreSQL running with a `netflixclone` database. Config lives in
`application.properties` (DB URL, JWT secret, upload dir). App serves on **http://localhost:8080**.
Email is **disabled in dev** (`app.mail.enabled=false`) — verification/reset links are
printed to the console log instead of being emailed.

## Architecture conventions
- **Layering:** controller → service → repository. Controllers stay thin; logic lives in services.
- **DTOs at the edges:** controllers accept/return DTO records, never JPA entities. `dto/Mapper` converts.
- **Errors:** throw a custom exception from `exception/`; `GlobalExceptionHandler` maps it to an HTTP status. Don't build error responses by hand.
- **Auth:** stateless JWT. `JwtAuthFilter` validates the token and loads the user (with role) on every request. Admin-only endpoints use `@PreAuthorize("hasRole('ADMIN')")`.
- **Validation:** jakarta `@Valid` on request DTOs; failures become 400s with field errors.

## Feature status
1. ✅ Project foundation (entities, repos, DTOs, exceptions)
2. ✅ Signup + email verification
3. ✅ Login + JWT
4. ✅ Password management (forgot / reset / change)
5. ✅ Admin video CRUD (role-based)
6. ✅ File upload (videos + posters)
7. ⏳ Video streaming with HTTP Range/seek
8. ⏳ Favorites / watchlist
9. ⏳ Angular frontend

## Testing
Manual API testing via **Postman** (no automated tests yet). Typical flow:
signup → verify (link from console) → login (get JWT) → call protected endpoints with `Authorization: Bearer <token>`.

## Workflow
One commit per feature (`feat: ...`). Commit from the repo root, then push.
```powershell
git add . && git commit -m "feat: ..." && git push
```
