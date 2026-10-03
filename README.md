# Career Connect

Career Connect is a backend-focused professional networking and recruitment platform built using Java and Spring Boot.

It provides REST APIs for professional profiles, networking, job recruitment, applications, authentication, and social features.

## Features

### Authentication & Security
- User registration and login
- JWT-based authentication
- BCrypt password hashing
- Role-based access for users and recruiters
- Stateless authentication using Spring Security

### User Profiles
- View and update professional profiles
- Add skills
- Add education
- Add work experience

### Professional Networking
- Send connection requests
- Accept or reject requests
- View connections
- View sent and pending requests

### Social Features
- Create posts
- View personal posts and feed
- Like and unlike posts
- View post likes

### Recruitment
- Recruiter company profiles
- Create, update and delete job postings
- Search and filter jobs
- Apply for jobs
- Track application status
- Recruiters can view applications and update their status

### Backend Quality
- Layered architecture
- DTO-based API design
- Request validation
- Global exception handling
- Application logging
- JPA/Hibernate persistence
- JUnit 5 and Mockito unit tests
- Swagger/OpenAPI documentation

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- Swagger / OpenAPI
- Lombok

## Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL