# 🏥 Hospital Management System
### Production-Grade Backend · Spring Boot · PostgreSQL · Redis · JWT

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-316192?style=flat&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=flat&logo=redis&logoColor=white)](https://redis.io/)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=flat&logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/API_Docs-Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)](http://localhost:7060/api/swagger-ui.html)
[![Live](https://img.shields.io/badge/Status-Live_on_Railway-0B0D0E?style=flat&logo=railway)](https://railway.app/)

---

A **fully deployed, concurrency-safe hospital backend** built to production standards. This isn't a tutorial project — it solves real engineering problems: preventing double-bookings under concurrent load, securing multi-role access, and building retry-safe APIs.

**[📖 Swagger Docs](http://localhost:7060/api/swagger-ui.html)** · **[🚀 Live API](hospital-management-system-production-6d80.up.railway.app)** · **[📬 Contact](#contact)**

---

## ⚡ Why This Project Is Different

Most hospital/booking system projects on GitHub ignore the hard parts. This one doesn't.

| Problem | Naive Approach | This System |
|---|---|---|
| Two users book the same slot simultaneously | Application-level check (race condition) | **DB-level UNIQUE constraint** — one wins, one gets a clean error |
| Client retries a failed booking request | Duplicate record created | **Redis idempotency keys** — retries return cached result, no duplicates |
| Unauthorized role accessing admin endpoints | Trust the frontend | **JWT + Spring Security @PreAuthorize** — enforced at every endpoint |
| API returns confusing 500 errors | Stack traces leak to client | **Global exception handler** — structured JSON errors with correct HTTP codes |

---

## 🏗️ Architecture Overview

```
Client / Frontend
      │
      ▼
 JwtFilter (validates token, sets SecurityContext)
      │
      ▼
 Controllers (REST endpoints, @PreAuthorize role checks)
      │
      ├──▶ Services (business logic, @Transactional)
      │         │
      │         ├──▶ Redis (idempotency key check before DB write)
      │         ├──▶ PostgreSQL (ACID transactions, UNIQUE constraints)
      │         └──▶ MapStruct (Entity ↔ DTO mapping)
      │
      └──▶ GlobalExceptionHandler (structured error responses)
```

---

## 🔑 Core Features

### 1. Concurrency-Safe Appointment Booking
**The real problem:** Without proper constraints, two simultaneous booking requests for the same doctor + time slot can both succeed — creating a double booking.

**How this is solved here (two layers):**

```
Request 1 ──┐
             ├──▶ Redis idempotency check ──▶ DB UNIQUE constraint ──▶ ✅ Booking saved
Request 2 ──┘                                                        ──▶ ❌ ConstraintViolationException → 409 Conflict
```

- **Layer 1 — Redis Idempotency Keys:** Client sends a unique `Idempotency-Key` header. If the same key arrives again (retry), the cached response is returned — no second DB write.
- **Layer 2 — DB UNIQUE Constraint:** Even without idempotency keys, the DB enforces `UNIQUE(doctor_id, date, time_slot)`. The second concurrent transaction fails at commit time with a clean, handled error.

### 2. JWT Authentication + Role-Based Access Control
- Stateless JWT authentication with BCrypt password hashing
- Three roles: `ADMIN`, `DOCTOR`, `PATIENT` — each with distinct endpoint access
- Spring Security enforces access at the method level via `@PreAuthorize`
- Token validation on every request via a custom `JwtFilter`

### 3. Dynamic Search, Filtering & Pagination
Built using **Spring Data JPA Specifications** — no hardcoded queries:
```java
// Example: filter doctors by specialization + availability + department
Specification<Doctor> spec = DoctorSpecification.hasSpecialization(spec)
    .and(DoctorSpecification.isAvailableOn(day))
    .and(DoctorSpecification.inDepartment(deptId));
```
Every list endpoint supports `page`, `size`, `sort`, and multiple filter params.

### 4. Clean DTO ↔ Entity Mapping via MapStruct
No manual `get/set` chains. MapStruct generates compile-time type-safe mappers, keeping API contracts separate from DB schema — a production best practice.

### 5. Global Exception Handling
Every exception — validation errors, not-found, conflict, unauthorized — returns a structured response:
```json
{
  "status": 409,
  "error": "CONFLICT",
  "message": "This time slot is already booked.",
  "timestamp": "2026-05-10T14:32:01"
}
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| ORM | Spring Data JPA + Hibernate |
| Mapping | MapStruct |
| Cache / Idempotency | Redis |
| Database | PostgreSQL |
| Documentation | Springdoc OpenAPI + Swagger UI |
| Build | Maven |
| Deployment | Railway |

---

## 📁 Project Structure

```
src/main/java/com/example/demo/
├── Controller/          # REST endpoints (UserController, DoctorController, AppointmentController...)
├── Service/             # Business logic (AppointmentService handles booking + idempotency)
├── Model/               # JPA entities (Doctor, Patient, Appointment, DoctorAvailability...)
├── DTO/                 # API request/response objects
├── Mapper/              # MapStruct interfaces (compile-time generated)
├── Repository/          # Spring Data JPA repos + custom queries
├── Specification/       # Dynamic JPA filter specs for each entity
├── Pagination/          # Page config objects per entity
├── JWT/                 # JwtFilter + JWTService
├── Config/              # SecurityConfig, SwaggerConfig, IdempotencyCleanupJob
├── ExceptionHandler/    # GlobalExceptionHandler + custom exceptions
├── Enum/                # Role, Gender, AppointmentStatus, WorkingDay, DoctorSpecializations
└── Locking/             # Optimistic locking config
```

---

## 🚀 Running Locally

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Redis
- Maven

### Setup

```bash
# Clone
git clone https://github.com/MOHDJUNAID70/Hospital-Management-System---SpringBoot.git
cd Hospital-Management-System---SpringBoot

# Configure DB + Redis in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.redis.host=localhost
spring.redis.port=6379

# Run
./mvnw spring-boot:run
```

### API Docs (once running)
```
Swagger UI  →  http://localhost:7060/api/swagger-ui.html
OpenAPI JSON →  http://localhost:7060/api/v3/api-docs
```

---

## 📸 API Screenshots

> Swagger UI showing full endpoint coverage across all modules.

![Auth API](https://github.com/user-attachments/assets/4829971a-a677-4085-b53a-883574b4da7a)
![Doctor API](https://github.com/user-attachments/assets/96080dd1-99e5-4749-9cdc-14dfa8a69f87)
![Appointment API](https://github.com/user-attachments/assets/c2154b16-4ebf-40ff-b128-021f17afafa1)

---

## 🔌 Key API Endpoints

### Auth
| Method | Endpoint | Access |
|---|---|---|
| POST | `/users/register` | Public |
| POST | `/users/login` | Public |

### Appointments
| Method | Endpoint | Access |
|---|---|---|
| POST | `/hospital/appointments/booking` | PATIENT |
| GET | `/hospital/all_appointments` | ADMIN, DOCTOR |
| PUT | `/hospital/appointments/{id}/cancel` | PATIENT, ADMIN |
| GET | `/hospital/appointments/my` | PATIENT |

### Doctors
| Method | Endpoint | Access |
|---|---|---|
| GET | `/hospital/doctors` | All authenticated |
| GET | `/hospital/doctors/{id}/availability` | All authenticated |
| PUT | `/hospital/doctors/{id}/availability` | DOCTOR, ADMIN |
| POST | `/hospital/doctors` | ADMIN |

> Full interactive docs available on Swagger UI.

---

## 🧠 Engineering Decisions Worth Noting

**Why DB-level constraints over application-level checks?**
Application-level `if (slotTaken) throw error` has a race window — two threads can both pass the check before either writes. A DB UNIQUE constraint is atomic by design. The application handles the `DataIntegrityViolationException` and returns a clean 409.

**Why Redis for idempotency instead of a DB table?**
Idempotency keys are short-lived and high-frequency. Redis TTL-based expiry handles cleanup automatically (backed by `IdempotencyCleanupJob` as a fallback). DB polling for this is overkill.

**Why MapStruct over manual mapping or ModelMapper?**
MapStruct generates code at compile time — no reflection, no runtime overhead, and mapping errors are caught during build, not in production.

---

## 📬 Contact

**Mohd Junaid**
- GitHub: [@MOHDJUNAID70](https://github.com/MOHDJUNAID70)
- Email: mjunaid7082@gmail.com
- LinkedIn: [linkedin.com/in/mohdjunaid04](https://linkedin.com/in/mohdjunaid04/)
