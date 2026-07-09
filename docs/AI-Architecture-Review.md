# Principal Solution Architect Review

The repository is **documentation-only** (no source, build files, or Docker config yet). The docs give a coherent product direction and a sensible phased roadmap, but they stop at **feature lists and bullet entities**. Several cross-document gaps would cause rework, security ambiguity, and integration friction once implementation starts.

Below is what is **missing or inconsistent**, ordered by impact before coding.

---

## Executive Summary

| Area | Current state | Risk if unchanged |
|------|---------------|-------------------|
| Data model | Partial entity lists, no relationships | Wrong schema, broken assignments, missing audit/notifications |
| Auth & permissions | Roles named, access not specified | Inconsistent authorization, security holes |
| API design | Not documented | Frontend/backend coupling, inconsistent contracts |
| Technical baseline | Stack chosen, tooling unspecified | Every dev makes different choices |
| Business rules | Implied only | Validation bugs, bad deletes, report errors |
| NFRs | Thin | No test/deploy/ops targets |

**Recommendation:** Resolve **P0 items** before Phase 1. Treat **P1** as part of the first sprint’s spike docs. **P2/P3** can follow the roadmap, but several P1 items (notifications, forgot password) are tied to MVP features and should not wait until Phase 3–4.

---

## P0 — Blockers (define before implementation)

### 1. Complete and consistent data model (with relationships)

`Database-Entities.md` is the biggest gap.

**Missing entities** referenced elsewhere:
- **Employee** — `Features.md` has an Employees module; `Asset Assignment` uses `employeeId`, but there is no Employee entity. Unclear whether Employee = `User` or a separate table.
- **AuditLog** — required by Features and Permissions; no schema.
- **Notification** — required by Features; no schema.
- **RefreshToken / PasswordResetToken** — needed for JWT + forgot password; not defined.

**Incomplete entities:**
- **Asset** — no `departmentId`, location, description, lifecycle/depreciation fields (Business Problem and “Asset Lifecycle Report” imply these), audit fields (`createdAt`, `updatedAt`, `createdBy`).
- **User** — no name, department link, `lastLogin`, profile fields for Settings/Profile nav.
- **Vendor / Department / Category** — names only; no contacts, codes, hierarchy, or status.
- **Maintenance** — no priority, type (preventive/corrective), assignee, resolution notes, or link to requester.

**Missing relationship rules:**
- Cardinality (User ↔ Role: one or many?)
- Referential integrity on delete (what happens when deleting a Department with assets?)
- Unique constraints (`assetCode`, `serialNumber`, `email`)

**Deliverable needed:** ER diagram + entity spec with fields, types, indexes, FKs, and delete behavior.

---

### 2. Authentication and authorization specification

Features promise JWT, forgot password, change password, and RBAC. None of the flows are specified.

**Undocumented decisions:**
- User provisioning: admin-only vs self-registration?
- JWT: access vs refresh tokens, expiry, revocation, storage (httpOnly cookie vs localStorage)?
- Forgot password: email provider, token TTL, reset URL flow?
- Password policy: length, complexity, history?
- Role model: `User.role` (string) vs `Role` table — docs show both patterns without reconciliation.
- **Permission matrix:** `Permissions.md` is narrative (“Manage Assets”), not actionable. Need resource × action × role (e.g. `ASSET:DELETE` for Asset Manager only).

**Deliverable needed:** Auth sequence diagrams + permission matrix aligned to API routes and UI navigation.

---

### 3. API design standards and endpoint inventory

There is no REST contract: no resources, verbs, pagination, filtering, sorting, error format, or versioning.

Without this, frontend and backend will diverge on day one.

**Deliverable needed:**
- API conventions (e.g. `/api/v1/...`, pagination `page`/`size`, sort, filter query params)
- Standard error model (e.g. RFC 7807 Problem Details)
- OpenAPI sketch for Phase 1–2 endpoints (auth, users, assets, CRUD modules)

---

### 4. Technical baseline and project structure

`Technology-Decisions.md` lists languages/frameworks but not the implementation stack.

**Undecided items that block repo setup:**
- Build: Maven vs Gradle
- Spring modules: Web, Security, Data JPA, Validation, Mail, etc.
- Migrations: Flyway vs Liquibase
- Frontend: Vue 3?, Vite, Pinia/Vuex, Vue Router, HTTP client (axios/fetch)
- Testing: JUnit 5, Mockito, Testcontainers, Vitest/Cypress
- Docker Compose layout (app + Postgres + optional mailhog)
- Monorepo vs separate frontend/backend repos
- Package/module layout (Clean Architecture layers mentioned in NFRs but not defined)

**Deliverable needed:** ADR-style doc + agreed folder structure for backend and frontend.

---

### 5. Core business rules and validation

Features list CRUD operations but not rules.

**Examples needing explicit definition:**
- Can assets be deleted if assigned or under open maintenance?
- Soft delete vs hard delete per entity
- Asset status enum and allowed transitions (Available → Assigned → In Maintenance → Retired?)
- Assignment: one active assignment per asset? partial returns?
- Category/Department/Vendor delete when referenced
- Duplicate asset codes / serial numbers
- Who can assign assets — Asset Manager only, or Employee self-request?

**Deliverable needed:** Business rules document tied to entities and use cases.

---

## P1 — High priority (address in Phase 1 spike / early Phase 2)

### 6. Align roadmap, MVP scope, and module definitions

Internal inconsistencies:

| Topic | Issue |
|-------|--------|
| Dashboard | MVP in `Features.md`, Phase 4 in `Roadmap.md` |
| Employees | Feature module exists; roadmap has “User Management” in Phase 1 only |
| Settings | In `Modules.md` and `Navigation.md`; no features defined |
| Audit | Phase 4 in roadmap but security NFR expects audit logging from start |

**Deliverable needed:** Single **MVP scope document** with must-have vs Phase 2+ and per-phase acceptance criteria.

---

### 7. Notifications design

Email + in-app notifications are MVP features with no design.

**Missing:**
- Event catalog (assignment, maintenance created/closed, warranty expiring)
- Notification entity (read/unread, channel, payload)
- Sync email vs async queue (even a simple `@Async` + outbox pattern should be decided)
- User preferences (opt-in/out)

---

### 8. Audit logging design

Audit is a first-class module but not modeled.

**Missing:**
- What to log (entity, action, before/after, actor, IP, timestamp)
- Retention period
- Immutability / tamper considerations for Auditor role
- Whether login history is separate from entity change audit

---

### 9. Reports specification

Reports are named but not defined.

**Missing:**
- Report inputs, filters, grouping, date ranges
- Output format (on-screen, CSV, PDF)
- “Asset Lifecycle Report” vs available fields (no depreciation in schema)
- Auditor vs Asset Manager report differences

---

### 10. Environment, deployment, and local dev

Phase 6 mentions cloud/CI/CD, but local dev needs definition now.

**Missing:**
- `docker-compose` services and env vars
- Config profiles (dev, test, prod)
- Secrets handling (`.env`, never commit rules)
- Minimum CI pipeline (build, test, lint) even for portfolio project

---

### 11. Testing strategy (beyond “unit testing”)

NFRs mention unit tests only.

**Missing:**
- Coverage expectations
- Integration tests (API + DB via Testcontainers)
- Frontend component/e2e scope
- Definition of done per phase

---

### 12. Security baseline beyond JWT

For an enterprise portfolio app, document at least:

- CORS policy
- Rate limiting on auth endpoints
- Input sanitization / SQL injection (ORM helps, still document)
- HTTPS-only in production
- Sensitive field handling (password never in logs/audit)
- Optional: MFA roadmap (even if out of MVP)

---

## P2 — Important (before or during Phases 2–4)

### 13. UI/UX and navigation mapping

`Navigation.md` is a flat menu list.

**Missing:**
- Role-based menu visibility (Employee vs Admin)
- Key screen wireframes or flows (login → dashboard → asset detail → assign)
- Empty states, loading, error UX patterns with PrimeVue

---

### 14. File storage and document handling

AI roadmap mentions invoice upload and document analysis; Architecture lists “File Storage” as future integration.

**Decide early:** local disk vs S3-compatible storage, max file size, allowed types, virus scan (optional), link to Asset/Vendor.

---

### 15. Email integration

Required for forgot password and email notifications.

**Missing:** provider (SMTP, SendGrid, Mailhog for dev), templates, failure handling.

---

### 16. Expanded NFRs

Current NFRs are a good start but thin for enterprise claims.

**Add targets for:**
- Availability / backup / recovery
- Data retention and GDPR-style export/delete (if relevant)
- Concurrent users / load assumptions (even rough, for portfolio)
- Logging levels and correlation IDs
- Browser support matrix
- Accessibility (WCAG level, if any)

---

### 17. Cross-cutting architecture patterns

Architecture mentions three tiers only.

**Document:**
- Layer boundaries (controller → service → repository)
- DTO vs entity mapping
- Transaction boundaries
- Exception handling globally
- Idempotency for writes where needed
- Pagination strategy for all list endpoints

---

### 18. Reference / master data richness

Vendors without contact info, departments without hierarchy, categories without tree — may be fine for MVP, but should be an explicit **intentional simplification** vs oversight.

---

## P3 — Can follow roadmap but worth noting

| Item | Note |
|------|------|
| Multi-tenancy | Single-organization assumption should be stated explicitly |
| AI features | Good phased roadmap; depends on P2 file storage + structured asset data |
| Observability | Metrics, tracing, alerting — align with Phase 6 |
| i18n / timezones | Not mentioned; state “English only, UTC” if that’s the scope |
| Depreciation / financial compliance | Mentioned in business problem, absent from model — defer or document as out of scope |
| Development workflow | Branch strategy, commit conventions, PR checklist |

---

## Cross-document inconsistencies to resolve

1. **Employee vs User** — assignment and employee list need one canonical model.
2. **Role storage** — `User.role` field vs `Role` entity table.
3. **Dashboard timing** — MVP feature vs Phase 4 roadmap.
4. **Settings module** — listed everywhere, defined nowhere.
5. **Business problem promises** (depreciation, compliance) **vs schema** (not modeled).
6. **Clean Architecture** — stated in NFRs, not reflected in Architecture doc.

---

## What is already in good shape

- Clear **business problem** and **product vision**
- Sensible **module breakdown** and **role personas**
- Reasonable **technology choices** for a portfolio enterprise app
- **Phased roadmap** including AI without overcommitting early
- **NFR themes** (security, pagination, Docker, stateless API) point in the right direction

---

## Suggested documentation order before coding

1. **Data model + ER diagram** (fixes Employee/User and audit/notification gaps)
2. **Auth & permission matrix**
3. **API conventions + Phase 1–2 OpenAPI outline**
4. **Technical baseline ADR** (build, structure, Docker, migrations, testing)
5. **MVP scope + business rules** (single source of truth for roadmap vs features)
6. **Notifications, audit, and email** (minimal viable designs)