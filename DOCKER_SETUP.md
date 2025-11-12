# Docker Setup for AcadRev Application

## Environment Variables

### Required Variables

| Variable         | Description         | Default                                  |
| ---------------- | ------------------- | ---------------------------------------- |
| `DB_PASSWORD`    | PostgreSQL password | `postgres`                               |
| `JWT_SECRET_KEY` | JWT signing secret  | See `env.example` file in root directory |

### Optional Variables

| Variable              | Description          | Default          |
| --------------------- | -------------------- | ---------------- |
| `SERVER_PORT`         | Backend server port  | `8089`           |
| `FRONTEND_PORT`       | Frontend server port | `3000`           |
| `DB_NAME`             | Database name        | `acadrev`        |
| `DB_USERNAME`         | Database username    | `postgres`       |
| `DB_SCHEMA`           | Database schema      | `acadrev_schema` |
| `JWT_EXPIRATION_TIME` | JWT expiration (ms)  | `3600000`        |
| `MAX_FILE_SIZE`       | Max upload file size | `10MB`           |

## Services

### 1. PostgreSQL Database

- **Container**: `acadrev-postgres`
- **Port**: `5432` (mapped to host)
- **Volume**: `postgres_data` (persistent storage)
- **Health Check**: Enabled

### 2. Spring Boot Backend

- **Container**: `acadrev-backend`
- **Port**: `8089` (mapped to host)
- **Depends on**: PostgreSQL
- **Health Check**: Enabled

### 3. React Frontend

- **Container**: `acadrev-frontend`
- **Port**: `80` (mapped to host port 3000)
- **Web Server**: Nginx
- **Depends on**: Backend

## Accessing the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8089
- **PostgreSQL**: localhost:5432
