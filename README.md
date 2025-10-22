# hTask - Modern Project & Task Management Platform

hTask is a comprehensive project and task management platform built with a modern microservices architecture. It enables teams to collaborate effectively, manage projects, assign tasks, and track progress in real-time through an intuitive Kanban-style interface.

## Features

- **User Authentication & Authorization**: JWT-based secure authentication with role-based access control (Admin, Manager, Member)
- **Project Management**: Create, update, and manage projects with team member assignments
- **Kanban Board**: Intuitive drag-and-drop interface for task management with multiple status columns (To Do, In Progress, In Review, Done)
- **Task Management**: Create, assign, and track tasks with priorities, due dates, and descriptions
- **Real-time Updates**: Modern reactive UI with instant feedback
- **Role-based Permissions**: Fine-grained access control for different user roles
- **Responsive Design**: Works seamlessly across desktop and mobile devices

## Architecture

### Backend - Microservices Architecture

The backend is built with **Spring Boot** and follows a microservices pattern:

1. **API Gateway** (Port 8080)
   - Routes requests to appropriate microservices
   - Handles CORS configuration
   - Built with Spring Cloud Gateway

2. **Auth Service** (Port 8081)
   - User authentication and JWT token generation
   - Password encryption with BCrypt
   - User registration and login

3. **User Service** (Port 8082)
   - User profile management
   - Role and permission management
   - User listing and search

4. **Project Service** (Port 8083)
   - Project CRUD operations
   - Project member management
   - Project status tracking

5. **Task Service** (Port 8084)
   - Task CRUD operations
   - Kanban board support
   - Task assignment and status updates

6. **Common Module**
   - Shared DTOs and utilities
   - JWT utilities
   - Global exception handling

### Frontend - React TypeScript

- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **State Management**: Zustand
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Drag & Drop**: @dnd-kit
- **Styling**: Tailwind CSS

### Database

- **PostgreSQL**: Separate databases for each microservice
  - htask_auth
  - htask_users
  - htask_projects
  - htask_tasks

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Cloud Gateway
- Spring Data JPA
- Spring Security
- JWT (jjwt 0.12.3)
- PostgreSQL 16
- Lombok
- Maven

### Frontend
- React 18
- TypeScript
- Vite
- Zustand (State Management)
- React Router
- Axios
- @dnd-kit (Drag & Drop)
- Tailwind CSS

### DevOps
- Docker
- Docker Compose
- Nginx

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- OR
- Java 17+ and Maven
- Node.js 18+
- PostgreSQL 16+

### Quick Start with Docker

1. Clone the repository:
```bash
git clone <repository-url>
cd htask
```

2. Start all services:
```bash
docker-compose up --build
```

3. Access the application:
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Auth Service: http://localhost:8081
- User Service: http://localhost:8082
- Project Service: http://localhost:8083
- Task Service: http://localhost:8084

### Manual Setup

#### Backend Setup

1. Start PostgreSQL and create databases:
```bash
psql -U postgres
CREATE DATABASE htask_auth;
CREATE DATABASE htask_users;
CREATE DATABASE htask_projects;
CREATE DATABASE htask_tasks;
```

2. Build and run each service:
```bash
cd backend

# Build common module first
cd common
mvn clean install

# Run API Gateway
cd ../api-gateway
mvn spring-boot:run

# Run Auth Service
cd ../auth-service
mvn spring-boot:run

# Run User Service
cd ../user-service
mvn spring-boot:run

# Run Project Service
cd ../project-service
mvn spring-boot:run

# Run Task Service
cd ../task-service
mvn spring-boot:run
```

#### Frontend Setup

1. Install dependencies:
```bash
cd frontend
npm install
```

2. Create environment file:
```bash
cp .env.example .env
```

3. Start development server:
```bash
npm run dev
```

## API Documentation

### Authentication Endpoints

#### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "uuid",
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "role": "MEMBER"
  }
}
```

### Project Endpoints

#### Get User Projects
```
GET /api/projects/user/{userId}
Authorization: Bearer {token}
```

#### Create Project
```
POST /api/projects
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "New Project",
  "description": "Project description",
  "ownerId": "user-uuid",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

### Task Endpoints

#### Get Kanban Board
```
GET /api/tasks/project/{projectId}/kanban
Authorization: Bearer {token}
```

#### Create Task
```
POST /api/tasks
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "New Task",
  "description": "Task description",
  "projectId": "project-uuid",
  "creatorId": "user-uuid",
  "priority": "HIGH",
  "dueDate": "2024-12-31"
}
```

#### Move Task
```
PUT /api/tasks/{taskId}/move?status=IN_PROGRESS&position=0
Authorization: Bearer {token}
```

## Project Structure

```
htask/
├── backend/
│   ├── api-gateway/          # API Gateway service
│   ├── auth-service/         # Authentication service
│   ├── user-service/         # User management service
│   ├── project-service/      # Project management service
│   ├── task-service/         # Task management service
│   ├── common/               # Shared utilities and DTOs
│   └── pom.xml              # Parent POM
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable React components
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   ├── stores/          # Zustand stores
│   │   ├── types/           # TypeScript types
│   │   └── App.tsx          # Main application component
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml        # Docker Compose configuration
└── README.md                # This file
```

## Default User Roles

- **ADMIN**: Full system access, can manage all projects and users
- **MANAGER**: Can create projects and manage assigned projects
- **MEMBER**: Can view and work on assigned tasks

## Security

- JWT tokens with 24-hour expiration
- Password hashing with BCrypt
- CORS configuration for secure cross-origin requests
- Role-based access control (RBAC)
- Stateless session management

## Development

### Running Tests
```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm test
```

### Building for Production
```bash
# Backend
cd backend
mvn clean package

# Frontend
cd frontend
npm run build
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please open an issue on the GitHub repository.

## Roadmap

- [ ] Real-time notifications with WebSocket
- [ ] File attachments for tasks
- [ ] Comments and activity feed
- [ ] Sprint planning and time tracking
- [ ] Analytics and reporting dashboard
- [ ] Mobile application (React Native)
- [ ] Email notifications
- [ ] Integration with external tools (Slack, GitHub, etc.)

## Authors

Built with Spring Boot, React, and TypeScript.
