# dto/ — Data Transfer Objects

The shapes that cross the API boundary. Entities stay internal; DTOs are what clients send/receive.

## Layout
- `request/` — input records with jakarta validation:
  `SignupRequest`, `LoginRequest`, `ForgotPasswordRequest`, `ResetPasswordRequest`,
  `ChangePasswordRequest`, `VideoRequest`.
- `response/` — output records: `UserResponse` (no password!), `VideoResponse`, `AuthResponse`.
- `Mapper` — static helpers converting entities → response DTOs. Also turns relative media
  paths (e.g. `uploads/x.mp4`) into absolute URLs for responses.

## Conventions
- DTOs are Java **records** — immutable, minimal.
- Put validation annotations (`@NotBlank`, `@Email`, `@Size`, …) on request DTOs; controllers
  enforce them with `@Valid`.
- Response DTOs must **exclude sensitive fields** (passwords, raw tokens).
- Add a new DTO instead of exposing an entity when an endpoint needs a different shape.
