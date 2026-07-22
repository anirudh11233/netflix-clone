# config/ — Security & app configuration

Spring configuration beans. This is where security and cross-cutting wiring live.

## Files
- `SecurityConfig` — the `SecurityFilterChain`: stateless sessions, CSRF off, CORS for the
  frontend, public routes (`/api/auth/**`, `/uploads/**`), everything else authenticated.
  Registers `JwtAuthFilter` and enables method security (`@EnableMethodSecurity`) so
  `@PreAuthorize` works. Also defines the `PasswordEncoder` (BCrypt) bean.
- `JwtService` — creates and validates JWTs (sign with the secret, read the email/subject).
- `JwtAuthFilter` — a `OncePerRequestFilter`: reads `Authorization: Bearer <token>`, validates it,
  loads the user, and puts their identity + role into the `SecurityContext` for each request.
- `WebConfig` — serves uploaded files from disk (`uploads/`) at the `/uploads/**` URL.

## Conventions
- Security rules are centralized here — don't scatter auth checks in controllers
  (use `@PreAuthorize` on the method instead).
- The JWT secret and expiry come from `application.properties` (`app.jwt.*`).
- The token carries only the email; the **role is read fresh from the DB** on every request.
