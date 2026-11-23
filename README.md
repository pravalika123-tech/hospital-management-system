# Hospital Management System

A role-based Hospital Management System built with **Spring Boot 3**, **MongoDB**, **Spring Security**, and **Thymeleaf**. It supports three roles — Administrator, Doctor, and Patient — each with its own dashboard and permissions.

## Features

### Administrator
- System dashboard with doctor, patient, appointment, and record counts
- Add / view / delete doctors (a doctor login is created automatically)
- View all registered patients
- View and update appointment statuses across the hospital

### Doctor
- Personal dashboard with assigned appointments and records
- Approve, complete, or cancel their appointments
- Create medical records (diagnosis, prescription, notes) for patients
- View their full record history

### Patient
- Self-registration with a patient profile
- Browse doctors and request appointments
- Track appointment status (pending, approved, completed, cancelled)
- View personal medical records

### Security
- Form-based login with BCrypt password hashing
- Role-based authorization (`/admin/**`, `/doctor/**`, `/patient/**`)
- Role-aware post-login redirects and access-denied handling

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2 |
| Database | MongoDB (Spring Data MongoDB) |
| Security | Spring Security 6, BCrypt |
| View | Thymeleaf, Bootstrap 5 |
| Build | Maven |

## Prerequisites

- JDK 17 or newer
- Apache Maven 3.9+
- MongoDB running on `localhost:27017` (or a MongoDB Atlas URI)

## Run locally

```bash
# 1. Start MongoDB (example with Docker)
docker run -d -p 27017:27017 --name hospital-mongo mongo:7

# 2. Build
mvn clean package

# 3. Run
java -jar target/hospital-management-system-0.0.1-SNAPSHOT.jar
```

Open **http://localhost:8080**.

To use MongoDB Atlas instead:

```bash
set MONGODB_URI=mongodb+srv://<user>:<password>@cluster.mongodb.net/hospitaldb
```

On first startup, sample users, doctors, a patient, an appointment, and a medical record are seeded automatically (only when the database is empty).

## Demo credentials

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `admin123` |
| Doctor | `doctor` | `doctor123` |
| Patient | `patient` | `patient123` |

New patients registered through the UI use their phone number as the initial password.

## Project structure

```
src/main/java/com/example/hospital
├── config/       Security, user details, role redirect, seed data
├── controller/   Auth, Admin, Doctor, Patient web controllers
├── model/        User, Doctor, Patient, Appointment, MedicalRecord, enums
└── repository/   Spring Data MongoDB repositories
src/main/resources
├── application.properties
└── templates/    Login, registration, dashboards, forms, tables
```

## Resume bullet points

- Built a full-stack hospital management web application with Spring Boot 3 and MongoDB, supporting Admin, Doctor, and Patient roles.
- Implemented secure authentication and role-based authorization using Spring Security 6 with BCrypt password encoding and custom success handling.
- Modeled domain entities and built data access layers with Spring Data MongoDB, including derived query methods for appointments and records.
- Designed responsive Thymeleaf + Bootstrap interfaces for appointment booking, status workflows, and electronic medical records.
- Added idempotent seed data initialization to make the application demo-ready on first run.

## Possible enhancements

- Email/SMS appointment reminders
- Prescription PDF generation
- Payment integration for consultation fees
- REST API + JWT for mobile clients
- Docker Compose for one-command startup
