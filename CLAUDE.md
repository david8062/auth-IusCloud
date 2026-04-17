# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build the project
./mvnw clean package -DskipTests

# Run the application (requires active Spring profile)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=AuthApplicationTests

# Build Docker image for development
docker build -f Dockerfile.dev -t auth-dev .
```

## Architecture Overview

This is a **multi-tenant authentication microservice** built with Spring Boot 4.x (snapshot) and Java 21. It serves as the auth layer for a legal SaaS platform (IusCloud/abogado_saas).

### Multi-Tenancy Model

Tenancy is resolved **per-request** via `TenantFilter` (`config/filter/`), which:
1. Resolves the tenant from the HTTP `Host` header (subdomain-based)
2. Falls back to `X-Tenant-ID` request header
3. Stores the resolved `UUID` in `TenantContext` (a `ThreadLocal`-backed static class)
4. Clears the context in a `finally` block after the filter chain

The `/api/v1/onboard` path is excluded from `TenantFilter` since new tenants don't exist yet.

### Request Flow

```
Request → TenantFilter → JwtAuthenticationFilter → Controller → Service → Repository
```

`JwtAuthenticationFilter` validates the Bearer token, extracts `userId` as principal, and attaches `TenantAuthenticationDetails(tenantId)` to the Spring Security context.

### JWT Claims Structure

Tokens include: `email`, `tenantId`, `firstName`, `lastName`, `active`, `roles` (list), `permissions` (set of permission codes like `CASE_WRITE`). Subject is `userId` (UUID string). Config: `security.jwt.secret`, `security.jwt.expiration`, `security.jwt.refresh-expiration` in `application-dev.properties`.

### Onboarding Flow

`POST /api/v1/onboard` — public endpoint that:
1. Creates a new tenant
2. Creates an ADMIN role for that tenant (via `RoleHelper`)
3. Creates the owner user
4. Returns a JWT + refresh token pair (calls `loginWithTenant` internally)

### Feature Package Structure

Each feature under `core/features/` follows the same layout:
```
{feature}/
  controller/     — REST controllers
  service/        — Business logic
  repository/     — Spring Data JPA interfaces
  domain/
    model/        — JPA entities (extend BaseModel)
    dto/          — Request/Response DTOs
    mapper/       — MapStruct mappers
```

Features: `auth`, `tenants`, `users`, `roles`

### Base Classes

- `BaseModel` — `@MappedSuperclass` with `id` (UUID), `createdAt`, `updatedAt`, `deletedAt`, `active`. All entities extend this.
- `BaseRepository` — extends `JpaRepository`
- `BaseDTO` — base for response DTOs

### Response Wrappers

Use `ResponseUtil` (static factory) for all controller responses:
- `ResponseUtil.ok(data)` → `ApiResponse<T>` with HTTP 200
- `ResponseUtil.created(data)` → HTTP 201
- `ResponseUtil.list(data)` → `ListResponse<T>`
- `ResponseUtil.paged(page)` → `PagedResponse<T>`
- `ResponseUtil.noContent()` → HTTP 204

### Database

- PostgreSQL, managed by **Flyway** migrations in `src/main/resources/db/migration/`
- Dev DB: `jdbc:postgresql://postgres:5432/auth-lusCloud` (expects a `postgres` container)
- Soft deletes: `deleted_at` column + partial unique indexes (`WHERE deleted_at IS NULL`)
- Roles are **per-tenant** (have `tenant_id`); Permissions are **global** (shared across tenants)

### Security Config

`WebSecurityConfig` is `@Profile("dev")` only. Public endpoints:
- `POST /api/v1/auth/**` (login, refresh, logout)
- `POST /api/v1/onboard/**`

All other endpoints require a valid JWT.

### Base URL

`/ms-auth/` (configured via `server.servlet.context-path`)

Port: `8081` in dev profile.
