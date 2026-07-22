# service/ — Business logic

`@Service` classes hold the actual logic. Controllers call these; these call repositories.

## Files
- `AuthService` — signup, login (issues JWT), email verification, forgot/reset/change password.
- `EmailService` — sends verification & password-reset emails. In dev (`app.mail.enabled=false`)
  it logs the link instead of sending.
- `VideoService` — create/update/delete/list videos; maps entities to `VideoResponse`.
- `StorageService` — saves uploaded files to disk under random UUID names, validates type/size.

## Conventions
- Methods that write to the DB are `@Transactional`. With an open transaction, mutating a loaded
  entity persists automatically (dirty checking) — an explicit `save()` isn't always needed.
- Convert entities to DTOs here (via `dto/Mapper`) before returning to controllers.
- Throw custom exceptions from `exception/` for error cases; don't return null/error codes.
- Never leak secrets: password hashing stays in the auth flow, responses never include passwords.
