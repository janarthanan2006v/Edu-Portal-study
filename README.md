# Prince Study Material Portal — Full Stack Application

A production-ready study material portal designed for educational institutions, built with a modern **Spring Boot 3** REST API backend and an interactive **HTML5 / JavaScript** frontend.

---

## 🌟 Key Highlights & Features

- **Robust Spring Boot 3 Backend**:
  - Stateless JWT Authentication (HMAC-SHA256) & Spring Security 6.
  - Role-based Access Control (`STUDENT`, `FACULTY`, `ADMIN`).
  - Out-of-the-box Zero Configuration Execution using persistent embedded H2 database or production MySQL.
  - Automatic Database Seeding on startup via `DataInitializer` (creates default users, roles, and sample study materials).
  - Open & Configurable CORS for seamless frontend interaction.
  - Interactive **OpenAPI 3 / Swagger UI** documentation with integrated JWT Authorization.
  - Complete automated test suite covering authentication, authorization, and CRUD operations.
- **Frontend Portal**:
  - Clean student portal with subject and semester search (`Sem1` / `Sem2`).
  - Faculty management dashboard with real-time add, edit, delete, and filtering.
  - Responsive UI with modal alerts and dynamic transitions.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend Framework** | Spring Boot 3.4.3 (Java 17+) |
| **Security & Auth** | Spring Security 6 + JJWT (JSON Web Token) |
| **Data Layer** | Spring Data JPA / Hibernate ORM |
| **Databases** | H2 (Default out-of-the-box) & MySQL 8.0+ (Production Profile) |
| **API Documentation** | SpringDoc OpenAPI 3 / Swagger UI (v2.8.5) |
| **Frontend** | HTML5, Vanilla JavaScript, TailwindCSS, FontAwesome |

---

## 🚀 Quick Start Guide

### 1. Run the Backend (Spring Boot)

You can run the backend immediately without configuring MySQL — it automatically initializes the embedded database and sample records!

**On macOS / Linux:**
```bash
cd backend
./mvnw spring-boot:run
```

**On Windows:**
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Or run the compiled production JAR:
```bash
cd backend
java -jar target/material-portal-1.0.0.jar
```

- **Backend Base URL**: `http://localhost:8080`
- **Swagger API Documentation**: `http://localhost:8080/swagger-ui/index.html`
- **H2 Database Console**: `http://localhost:8080/h2-console` *(JDBC URL: `jdbc:h2:file:./data/material_portal`, User: `sa`, Password: empty)*

---

### 2. Optional: Run with MySQL Database

To connect to a local or remote MySQL database:

1. Create the MySQL database:
   ```sql
   CREATE DATABASE prince_material_portal;
   ```
2. Run with the `mysql` profile:
   ```bash
   cd backend
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
   *You can override MySQL credentials via environment variables:*
   ```bash
   SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/prince_material_portal
   SPRING_DATASOURCE_USERNAME=root
   SPRING_DATASOURCE_PASSWORD=root
   ```

---

### 3. Run the Frontend

Open `frontend/index.html` in your favorite local server:

- **Using Python 3:**
  ```bash
  python3 -m http.server 5500 --directory frontend
  ```
- **Using VS Code Live Server:** Right-click `frontend/index.html` → *Open with Live Server* (`http://127.0.0.1:5500`).

---

## 👥 Default Login Accounts

The system automatically initializes these demo accounts on startup:

| Role | Email / Username | Password | Access |
|---|---|---|---|
| **Student** | `psvpec.2025.students@gmail.com` | `password` | Student Portal, Material Search |
| **Faculty** | `psvpec.2025.staffs@gmail.com` | `password` | Faculty Dashboard, Add / Edit / Delete Materials |
| **Student Demo** | `student@prince.edu` | `password` | Student Portal |
| **Faculty Demo** | `faculty@prince.edu` | `password` | Faculty Dashboard |

---

## 📡 REST API Reference

All protected endpoints require the HTTP header:
`Authorization: Bearer <token>`

### 1. Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/login` | Authenticate user and receive JWT token | No |
| `POST` | `/api/auth/register` | Register new student or faculty user | No |
| `GET` | `/api/auth/me` | Get current authenticated user profile | Yes |
| `GET` | `/api/auth/status` | Auth service health check | No |

**Login Request Body:**
```json
{
  "username": "psvpec.2025.staffs@gmail.com",
  "password": "password"
}
```

**Login Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "role": "FACULTY",
    "username": "psvpec.2025.staffs@gmail.com",
    "fullName": "PSVPEC Faculty Staff",
    "expiresIn": 86400000
  },
  "timestamp": "2026-09-30T13:45:12",
  "status": 200
}
```

---

### 2. Student Materials (`/api/materials`)

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `GET` | `/api/materials/search?subjectName={name}&semester={sem1\|sem2}` | Search active study material | `STUDENT`, `FACULTY` |
| `GET` | `/api/materials` | Retrieve list of all materials | `STUDENT`, `FACULTY` |
| `GET` | `/api/materials/{id}` | Get material by ID | `STUDENT`, `FACULTY` |
| `GET` | `/api/materials/department/{department}` | Filter materials by department | `STUDENT`, `FACULTY` |

---

### 3. Faculty Management (`/api/faculty/materials`)

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `GET` | `/api/faculty/materials` | Get all materials for dashboard table | `FACULTY`, `ADMIN` |
| `GET` | `/api/faculty/materials/{id}` | Get single material record | `FACULTY`, `ADMIN` |
| `POST` | `/api/faculty/materials` | Create a new study material record | `FACULTY`, `ADMIN` |
| `PUT` | `/api/faculty/materials/{id}` | Update study material record | `FACULTY`, `ADMIN` |
| `DELETE` | `/api/faculty/materials/{id}` | Delete study material record | `FACULTY`, `ADMIN` |
| `GET` | `/api/faculty/materials/stats` | Get portal summary analytics | `FACULTY`, `ADMIN` |

**Create / Update Material Request Body:**
```json
{
  "subjectName": "Computer Networks",
  "department": "Information Technology",
  "courseYear": "3rd Year",
  "sem1": "https://drive.google.com/cn-sem1",
  "sem2": "https://drive.google.com/cn-sem2",
  "activeStatus": true
}
```

---

### 4. Health & Diagnostics (`/api/health`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | System health check and uptime status |

---

## 🧪 Running Automated Tests

Run the full JUnit 5 and MockMvc integration test suite:

```bash
cd backend
./mvnw clean test
```

Build the production JAR package:
```bash
cd backend
./mvnw clean package
```

---

## 📁 Project Architecture

```
Edu_portal/
├── backend/
│   ├── mvnw / mvnw.cmd              ← Cross-platform Maven Wrappers
│   ├── pom.xml                      ← Maven dependencies & build configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/prince/materialportal/
│       │   │   ├── MaterialPortalApplication.java
│       │   │   ├── config/          ← SecurityConfig, JwtAuthFilter, CorsConfig, SwaggerConfig, DataInitializer
│       │   │   ├── controller/      ← AuthController, MaterialController, FacultyMaterialController, HealthController
│       │   │   ├── dto/             ← Request/Response models (ApiResponse, LoginRequest, etc.)
│       │   │   ├── entity/          ← JPA Entities (User, Role, Material)
│       │   │   ├── exception/       ← GlobalExceptionHandler & custom exceptions
│       │   │   ├── repository/      ← UserRepository, RoleRepository, MaterialRepository
│       │   │   ├── service/         ← AuthService, MaterialService, JwtService, CustomUserDetailsService
│       │   │   └── util/            ← ResponseBuilder, DateUtil
│       │   └── resources/
│       │       ├── application.properties         ← Default H2 file-persistence configuration
│       │       └── application-mysql.properties   ← MySQL profile configuration
│       └── test/
│           ├── java/com/prince/materialportal/    ← Integration tests (AuthControllerTest, MaterialServiceTest)
│           └── resources/application.properties   ← In-memory test configuration
└── frontend/
    ├── index.html                   ← Student and Faculty UI portal
    ├── script.js                    ← Client-side API integration logic
    ├── Prince-background-img.jpg
    └── prince-header-img.png
```
