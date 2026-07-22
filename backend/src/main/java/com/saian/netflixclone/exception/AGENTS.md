# exception/ — Error handling

Custom exceptions plus one central handler that turns them into clean HTTP responses.

## Files
- `GlobalExceptionHandler` — `@RestControllerAdvice`; maps each exception to a status +
  JSON body (`timestamp`, `status`, `error`, and `fieldErrors` for validation).
- Custom exceptions and their statuses:
  - `ResourceNotFoundException` → 404
  - `InvalidTokenException` → 400
  - `InvalidCredentialsException` → 401
  - `DuplicateResourceException` → 409
  - `FileStorageException` → 400
  - `EmailSendingException` → 500
  - Spring's `AuthorizationDeniedException` (from `@PreAuthorize`) → 403

## Conventions
- **Throw, don't catch-and-format.** Services/controllers throw one of these; the handler
  produces the response. This keeps error shapes consistent across the whole API.
- Add a new exception + a handler method here when you need a new error case — don't return
  ad-hoc error maps from controllers.
- Never expose stack traces or internal details in the response body.
