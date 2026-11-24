# Hospital Management System

A full-featured **Hospital Management System** built with **Spring Boot 3**, **MongoDB**, **Spring Security (form login + JWT)**, **REST APIs**, **Swagger/OpenAPI**, and **Thymeleaf**. It supports three roles — Administrator, Doctor, and Patient — through both a web UI and a stateless JSON API.

## Features

### Web application
- **Administrator**: dashboard with counts, add/view/delete doctors, view patients, update any appointment status
- **Doctor**: personal dashboard, manage appointment statuses, create medical records for patients
- **Patient**: self-registration, book appointments (with conflict detection), track status, view records
- BCrypt password hashing, role-based URL authorization, role-aware login redirects

### REST API
- JWT access tokens issued at `POST /api/auth/login`
- Role-scoped endpoints (`/api/admin/**`, `/api/doctor/**`, `/api/patient/**`)
- Appointment booking with **conflict detection** — rejects slots within 30 minutes of an existing appointment for the same doctor or patient (HTTP `409 Conflict`)
- Request validation with clear JSON error responses
- Interactive Swagger UI and OpenAPI JSON

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2, Spring MVC |
| Database | MongoDB (Spring Data MongoDB) |
| Security | Spring Security 6, BCrypt, JWT (jjwt 0.12) |
| API docs | springdoc-openapi 2.3, Swagger UI |
| View | Thymeleaf, Bootstrap 5 |
| Build & test | Maven, JUnit 5, Mockito |

## Run locally

```bash
# 1. Start MongoDB
docker run -d -p 27017:27017 --name hospital-mongo mongo:7

# 2. Build and test
mvn clean package

# 3. Run
java -jar target/hospital-management-system-0.0.1-SNAPSHOT.jar
```

- Web app: **http://localhost:8080**
- Swagger UI: **http://localhost:8080/swagger-ui.html**
- OpenAPI JSON: **http://localhost:8080/v3/api-docs**

Use MongoDB Atlas or a different secret with environment variables:

```bash
set MONGODB_URI=mongodb+srv://<user>:<password>@cluster.mongodb.net/hospitaldb
set JWT_SECRET=<base64-encoded-64-byte-secret>
```

Seed users, doctors, a patient, an appointment, and a medical record are created on first startup when the database is empty.

## Demo credentials

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `admin123` |
| Doctor | `doctor` | `doctor123` |
| Patient | `patient` | `patient123` |

## API quick start

```bash
# 1. Obtain a JWT
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"patient\",\"password\":\"patient123\"}"

# 2. Use the token
curl http://localhost:8080/api/patient/appointments \
  -H "Authorization: Bearer <accessToken>"

# 3. Book an appointment (returns 409 on a conflicting slot)
curl -X POST http://localhost:8080/api/patient/appointments \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d "{\"doctorId\":\"<doctor-id>\",\"dateTime\":\"2026-12-10T10:30:00\",\"reason\":\"Check-up\"}"
```

### Endpoints

| Method | Endpoint | Role | Description |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Public | Issue JWT token |
| GET | `/api/auth/me` | Any | Inspect current token |
| GET | `/api/patient/profile` | Patient | Current patient profile |
| GET | `/api/patient/doctors` | Patient | List doctors |
| GET | `/api/patient/appointments` | Patient | My appointments |
| POST | `/api/patient/appointments` | Patient | Book appointment (409 on conflict) |
| GET | `/api/doctor/appointments` | Doctor | Assigned appointments |
| PATCH | `/api/doctor/appointments/{id}/status` | Doctor | Update appointment status |
| GET | `/api/doctor/records` | Doctor | My medical records |
| POST | `/api/doctor/records` | Doctor | Create medical record |
| GET | `/api/admin/doctors` | Admin | List doctors |
| POST | `/api/admin/doctors` | Admin | Add doctor |
| GET | `/api/admin/patients` | Admin | List patients |
| GET | `/api/admin/appointments` | Admin | List all appointments |
| PATCH | `/api/admin/appointments/{id}/status` | Admin | Update any status |

## Architecture

```
src/main/java/com/example/hospital
├── config/       Security chains (web + stateless API), JWT registration, Swagger, seed data, error handler
├── controller/   Web controllers (Auth, Admin, Doctor, Patient)
├── controller/api/ REST controllers (Auth, Patient, Doctor, Admin)
├── dto/          Request/response records with validation
├── exception/    ConflictException for appointment clashes
├── model/        User, Doctor, Patient, Appointment, MedicalRecord, enums
├── repository/   Spring Data MongoDB repositories
├── security/     JwtService and JwtAuthenticationFilter
└── service/      AppointmentService with conflict detection
src/main/resources/templates   Thymeleaf views
```

## Tests

```bash
mvn test
```

`AppointmentServiceTest` covers overlapping-slot rejection and successful booking using Mockito.

## Resume bullet points

- Built a full-stack hospital management system with Spring Boot 3, MongoDB and role-based access control for Admin, Doctor and Patient users.
- Implemented JWT authentication and separate stateless REST API and Thymeleaf web security chains using Spring Security 6.
- Developed 15 role-scoped REST endpoints documented with Swagger/OpenAPI and validated with DTO-level constraints.
- Added appointment scheduling with 30-minute conflict detection for doctors and patients, returning HTTP 409 on overlapping slots.
- Wrote unit tests with JUnit 5 and Mockito for the booking conflict logic.

## Possible enhancements

- Email/SMS appointment reminders
- Prescription PDF generation
- Payment integration for consultation fees
- Refresh tokens and token revocation
- Docker Compose for one-command startup
