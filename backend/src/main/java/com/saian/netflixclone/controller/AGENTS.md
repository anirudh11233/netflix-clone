# controller/ — HTTP endpoints

`@RestController` classes: the web layer. They translate HTTP requests into service calls
and return DTOs. Keep them **thin** — no business logic here.

## Files
- `AuthController` (`/api/auth`) — signup, login, verify, forgot/reset password. Public.
- `UserController` (`/api/users`) — `/me`, change-password. Requires a valid JWT.
- `VideoController` (`/api/videos`) — video CRUD. Admin-only writes via
  `@PreAuthorize("hasRole('ADMIN')")`; published-list/read open to any logged-in user.
- `FileUploadController` (`/api/files`) — admin uploads for video/poster files.

## Conventions
- Accept request DTO records with `@Valid`; return response DTOs — **never JPA entities**.
- Don't catch exceptions to build error responses; let them bubble to `GlobalExceptionHandler`.
- Authorization is declarative: use `@PreAuthorize` for role checks, not manual `if` logic.
- Get the current user's email from the `Authentication` parameter (`authentication.getName()`).
