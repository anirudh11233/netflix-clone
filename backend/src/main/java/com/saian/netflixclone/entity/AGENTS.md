# entity/ — JPA entities (database tables)

`@Entity` classes mapped to PostgreSQL tables by Hibernate. These define the schema
(`ddl-auto=update` auto-creates/updates tables on startup).

## Files
- `User` — table `users`: credentials, role, `enabled` flag, verification & reset tokens,
  many-to-many `favorites`, `createdAt`. Email is unique.
- `Video` — table `videos`: title/description/year/rating, `src`/`poster` paths,
  `published` flag, a `@ElementCollection` of categories, and the inverse side of favorites.

## Conventions
- Entities stay **inside** the app — never return them from controllers; map to DTOs first.
- Use Lombok (`@Data`/`@Builder`) but exclude relationship fields from `toString`/`equals`
  to avoid lazy-loading loops (see `Video.favoritedBy`).
- Store media as **relative paths**; absolute URLs are built in `dto/Mapper` at response time.
- Schema changes happen by editing these classes (Hibernate applies them via `ddl-auto`).
