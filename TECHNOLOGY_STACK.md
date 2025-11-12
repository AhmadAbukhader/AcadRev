# Technology Stack Documentation - AcadRev Application

## Overview

This document describes all the technologies, frameworks, and tools used to build the AcadRev application.

---

## Backend Technologies

### Core Framework

- **Spring Boot 3.5.6** - Main application framework
- **Java 17** - Programming language

### Spring Boot Modules Used

#### 1. **Spring Boot Starter Web**

- RESTful API development
- Embedded Tomcat server
- HTTP request/response handling

#### 2. **Spring Boot Starter Data JPA**

- Database ORM (Object-Relational Mapping)
- Entity management
- Repository pattern implementation

#### 3. **Spring Boot Starter Security**

- Authentication and authorization
- JWT token-based security
- Role-based access control (RBAC)

#### 4. **Spring Boot Starter Test**

- Unit and integration testing
- Spring Security testing support

### Database

- **PostgreSQL** - Relational database management system
  - Schema: `acadrev_schema`
  - Connection via JDBC driver

### Security & Authentication

- **JWT (JSON Web Tokens)** - Token-based authentication
  - Library: `io.jsonwebtoken` (jjwt) version 0.11.5
    - `jjwt-api`
    - `jjwt-impl`
    - `jjwt-jackson`
- **BCrypt** - Password hashing (via Spring Security)
- **Spring Security** - Security framework
  - Custom JWT authentication filter
  - CORS configuration
  - Session management (STATELESS)

### Build Tool

- **Maven** - Dependency management and build tool
  - Maven Wrapper (mvnw) included

### Code Generation & Utilities

- **Lombok** - Reduces boilerplate code
  - Version: 1.18.42
  - Annotation processor for:
    - `@Data`, `@Builder`, `@RequiredArgsConstructor`, etc.

### Spring Boot Configuration

#### Configuration Files

1. **application.properties** - Main configuration file

   - Database connection settings
   - JWT configuration
   - File upload settings
   - Hibernate/JPA settings
   - Logging configuration

2. **Java Configuration Classes:**
   - `SecurityConfiguration.java` - Security and CORS configuration
   - `ApplicationConfiguration.java` - Bean definitions for authentication

#### Key Configuration Settings:

- **Server Port:** 8089
- **Database:** PostgreSQL (localhost:5432/acadrev)
- **JWT Expiration:** 1 hour (3600000 ms)
- **File Upload:** Max 10MB per file
- **Hibernate DDL:** `none` (manual schema management)
- **SQL Logging:** Enabled for debugging

---

## Frontend Technologies

### Core Framework

- **React 19.1.1** - UI library
- **React DOM 19.1.1** - React rendering

### Build Tool & Dev Server

- **Vite 7.1.7** - Fast build tool and dev server
  - Development server on port 3000
  - Hot Module Replacement (HMR)
  - Fast builds

### Routing

- **React Router DOM 7.9.4** - Client-side routing
  - Navigation between pages
  - Protected routes

### HTTP Client

- **Axios 1.12.2** - HTTP client for API calls
  - Interceptors for JWT token handling
  - Request/response handling

### UI & Styling

- **Tailwind CSS 3.4.18** - Utility-first CSS framework
  - Responsive design
  - Custom styling
- **PostCSS 8.5.6** - CSS processing
- **Autoprefixer 10.4.21** - CSS vendor prefixing

### Icons

- **Lucide React 0.548.0** - Icon library
  - Modern, customizable icons

### Code Quality

- **ESLint 9.36.0** - JavaScript/React linting
  - `@eslint/js`
  - `eslint-plugin-react-hooks`
  - `eslint-plugin-react-refresh`
- **TypeScript Types** (for better IDE support)
  - `@types/react`
  - `@types/react-dom`

### Development Tools

- **Vite React Plugin 5.0.4** - React support for Vite
- **Globals 16.4.0** - Global variable definitions

---

## Architecture Pattern

### Backend Architecture

- **Layered Architecture:**
  - **Controller Layer** - REST API endpoints
  - **Service Layer** - Business logic
  - **Repository Layer** - Data access (Spring Data JPA)
  - **Model Layer** - Entity classes
  - **DTO Layer** - Data Transfer Objects
  - **Security Layer** - Authentication & authorization

### Frontend Architecture

- **Component-Based Architecture:**
  - Functional components with React Hooks
  - Separation of concerns:
    - Pages (Login, CompanyDashboard, AuditorDashboard)
    - Components (RequirementsTabs, DocumentPreviewModal, PDFPreviewModal)
    - API layer (company-api.js, auditor-api.js, api.js)
    - Utilities (auth.js)

---

## Database Schema

- **PostgreSQL** with custom schema: `acadrev_schema`
- Tables include:
  - Users, Roles, Company Profiles
  - Requirements, Sections
  - Documents, Audit Reviews
  - Requirement Responses, Requirement Status
  - Requirement Auditing

---

## Development Environment

### Backend

- **Java 17**
- **Maven** (via Maven Wrapper)
- **Spring Boot 3.5.6**
- **PostgreSQL** database

### Frontend

- **Node.js** (for npm package management)
- **Vite** dev server
- **Modern browsers** (Chrome, Firefox, Edge, Safari)

---

## Key Features Implemented

1. **Authentication & Authorization**

   - JWT-based authentication
   - Role-based access (Company Owner, Auditor, Admin)
   - Protected API endpoints

2. **File Management**

   - PDF document upload/download
   - Document metadata management
   - File size validation (10MB max)

3. **Requirement Management**

   - ISO 9001 requirement tracking
   - Document association with requirements
   - Status tracking (No/TSE/Yes)

4. **Audit System**

   - Document review by auditors
   - Review ratings (ACCEPTED/REJECTED)
   - Audit status tracking

5. **Response System**
   - Company owner responses to requirements
   - Auditor replies to responses
   - Threaded conversation system

---

## Configuration Summary

### Spring Boot Configuration Methods:

1. **application.properties** - Property-based configuration
2. **Java Configuration Classes** - Programmatic configuration
   - `@Configuration` classes
   - `@Bean` definitions
3. **Annotations** - Declarative configuration
   - `@EnableWebSecurity`
   - `@EnableMethodSecurity`
   - `@Entity`, `@Repository`, `@Service`, `@RestController`

### No External Configuration Tools Used:

- ❌ No Spring Cloud Config
- ❌ No external configuration server
- ❌ No YAML configuration files
- ✅ Standard Spring Boot configuration approach

---

## Build & Deployment

### Backend Build

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend Build

```bash
npm install
npm run dev      # Development
npm run build    # Production build
```

---

## Summary

**Backend Stack:**

- Spring Boot 3.5.6 + Java 17
- PostgreSQL + Spring Data JPA
- Spring Security + JWT
- Maven + Lombok

**Frontend Stack:**

- React 19 + Vite 7
- Tailwind CSS + Lucide Icons
- Axios + React Router
- ESLint for code quality

**Configuration:**

- Standard Spring Boot configuration (application.properties + Java Config)
- No external configuration management tools
- Manual database schema management
