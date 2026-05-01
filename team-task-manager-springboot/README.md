# Team Task Manager

A full-stack team task management web application built with **Spring Boot 3**, **Spring Security + JWT**, **Spring Data JPA**, and **MySQL**. Features role-based access control (Admin/Member), project management, task assignment, status tracking, and a comprehensive dashboard.

---

## Features

- **Authentication & Authorization**
  - JWT-based stateless authentication
  - Role-based access control (ADMIN, MEMBER)
  - Secure password hashing with BCrypt

- **Project Management**
  - Create, read, update, delete projects
  - Add/remove team members to projects
  - Project admin controls

- **Task Management**
  - Create tasks within projects
  - Assign tasks to team members
  - Track task status (TODO, IN_PROGRESS, DONE, CANCELLED)
  - Priority levels (LOW, MEDIUM, HIGH, URGENT)
  - Overdue task detection

- **Dashboard**
  - Overview statistics (total projects, tasks, completion rate)
  - Recent tasks
  - Upcoming deadlines
  - Overdue tasks alert

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.5 |
| Security | Spring Security + JWT |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Java Version | 17 |

---

## Project Structure

```
team-task-manager/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/taskmanager/
│       │   ├── TeamTaskManagerApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── ProjectController.java
│       │   │   ├── TaskController.java
│       │   │   └── DashboardController.java
│       │   ├── dto/
│       │   │   ├── AuthResponse.java
│       │   │   ├── LoginRequest.java
│       │   │   ├── RegisterRequest.java
│       │   │   ├── UserDto.java
│       │   │   ├── UserSummaryDto.java
│       │   │   ├── ProjectDto.java
│       │   │   ├── ProjectRequest.java
│       │   │   ├── ProjectMemberDto.java
│       │   │   ├── AddMemberRequest.java
│       │   │   ├── TaskDto.java
│       │   │   ├── TaskRequest.java
│       │   │   ├── TaskStatusUpdateRequest.java
│       │   │   └── DashboardStatsDto.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Project.java
│       │   │   ├── ProjectMember.java
│       │   │   └── Task.java
│       │   ├── enums/
│       │   │   ├── Role.java
│       │   │   ├── TaskStatus.java
│       │   │   └── TaskPriority.java
│       │   ├── exception/
│       │   │   ├── BadRequestException.java
│       │   │   ├── ErrorResponse.java
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   ├── ResourceNotFoundException.java
│       │   │   └── UnauthorizedException.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── ProjectRepository.java
│       │   │   ├── ProjectMemberRepository.java
│       │   │   └── TaskRepository.java
│       │   ├── security/
│       │   │   ├── JwtFilter.java
│       │   │   ├── JwtUtil.java
│       │   │   ├── UserDetailsImpl.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── ProjectService.java
│       │   │   ├── TaskService.java
│       │   │   ├── DashboardService.java
│       │   │   └── impl/
│       │   │       ├── AuthServiceImpl.java
│       │   │       ├── ProjectServiceImpl.java
│       │   │       ├── TaskServiceImpl.java
│       │   │       └── DashboardServiceImpl.java
│       └── resources/
│           ├── application.properties
│           └── application-dev.properties
```

---

## Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.0+

---

## Setup Instructions

### 1. Clone & Navigate

```bash
git clone <your-repo-url>
cd team-task-manager
```

### 2. Create MySQL Database

```sql
CREATE DATABASE taskmanager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure Environment Variables

Create a `.env` file in the project root or set environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=taskmanager
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_super_secret_jwt_key_min_32_chars_long
export JWT_EXPIRATION=86400000
export CORS_ORIGINS=http://localhost:3000,http://localhost:5173
```

### 4. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login and get JWT | No |
| GET | `/api/auth/me` | Get current user | Yes |
| GET | `/api/auth/users` | List all users | Admin only |

### Projects

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/projects` | Create project | Yes |
| GET | `/api/projects/{id}` | Get project by ID | Yes (member+) |
| GET | `/api/projects/my` | Get my projects | Yes |
| GET | `/api/projects` | Get all projects | Admin only |
| PUT | `/api/projects/{id}` | Update project | Yes (admin) |
| DELETE | `/api/projects/{id}` | Delete project | Yes (admin) |
| POST | `/api/projects/{id}/members` | Add member | Yes (admin) |
| DELETE | `/api/projects/{id}/members/{memberId}` | Remove member | Yes (admin) |

### Tasks

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/tasks/project/{projectId}` | Create task | Yes (member+) |
| GET | `/api/tasks/{id}` | Get task | Yes |
| GET | `/api/tasks/project/{projectId}` | List project tasks | Yes (member+) |
| GET | `/api/tasks/my` | Get my assigned tasks | Yes |
| PUT | `/api/tasks/{id}` | Update task | Yes (creator/admin) |
| PATCH | `/api/tasks/{id}/status` | Update status | Yes (member+) |
| DELETE | `/api/tasks/{id}` | Delete task | Yes (creator/admin) |
| GET | `/api/tasks/overdue` | Get overdue tasks | Yes |

### Dashboard

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/dashboard/stats` | Dashboard statistics | Yes |
| GET | `/api/dashboard/recent-tasks` | Recent tasks | Yes |
| GET | `/api/dashboard/upcoming-tasks` | Upcoming deadlines | Yes |

---

## Authentication

All protected endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

---

## Role-Based Access

| Role | Permissions |
|------|------------|
| **ADMIN** | Full access to all projects, tasks, and user management |
| **MEMBER** | Can view assigned projects, create/update assigned tasks, view own dashboard |

Project-level roles:
- **Project Admin**: Full control over a specific project and its tasks
- **Project Member**: Can view project, create tasks, update assigned tasks

---

## Database Schema

The application uses JPA/Hibernate with `ddl-auto=update` for automatic schema generation.

**Tables:**
- `users` - Registered users
- `projects` - Projects created by users
- `project_members` - Many-to-many junction for project membership
- `tasks` - Tasks belonging to projects

---

## Deployment

### Railway (Recommended)

1. Push code to GitHub
2. Connect Railway to your GitHub repo
3. Add MySQL plugin in Railway
4. Set environment variables in Railway dashboard
5. Deploy

### Docker (Optional)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/team-task-manager-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Demo Video

Record a 2-5 minute demo showing:
1. User registration and login
2. Creating a project
3. Adding team members
4. Creating and assigning tasks
5. Updating task status
6. Viewing the dashboard
7. Overdue task alerts

---

## License

MIT License
