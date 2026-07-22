# repository/ — Data access

Spring Data JPA repository interfaces. Spring generates the implementations at runtime —
you just declare method signatures.

## Files
- `UserRepository` — `findByEmail`, `findByVerificationToken`, `findByResetPasswordToken`,
  `existsByEmail`.
- `VideoRepository` — `findByPublishedTrue` and category/title query methods.

## Conventions
- Extend `JpaRepository<Entity, Long>` — CRUD comes for free.
- Prefer **derived query methods** (method-name queries like `findByEmail`) for simple lookups;
  use `@Query` only when the name would get unwieldy.
- Repositories return entities/`Optional` — the **service layer** maps them to DTOs.
- No business logic here; this layer is purely persistence.
