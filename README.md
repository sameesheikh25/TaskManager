# Task Management API

A simplified backend Task Management System built with Java (Spring Boot), organized using DDD principles and developed with TDD.

## Requirements covered
- CRUD REST endpoints for tasks
- Task entity with id, title, description, status, due_date
- DDD layers: domain, application (service), infrastructure (repository), api (controller), config (wiring)
- In-memory repository
- Validation: title and due_date required; due_date must be in the future
- List with pagination and status filter; sorted by due_date
- Unit and integration tests

## Prerequisites
- Java 23 (or compatible toolchain)
- Gradle (wrapper provided)

## Run tests
```powershell
./gradlew.bat test
```

## Run the application
```powershell
./gradlew.bat bootRun
```

The server starts on http://localhost:8080.

## API Endpoints

### Create Task
- **POST** `/tasks`
- **Request Body:**
  ```json
  {
    "title": "Task title",         // required
    "description": "Details...",   // optional
    "status": "PENDING",           // optional (PENDING, IN_PROGRESS, DONE)
    "dueDate": "2026-01-15"        // required, ISO-8601, must be in the future
  }
  ```
- **Success Response:**
  - **Status:** 201 Created
  - **Body:**
    ```json
    {
      "id": "string",
      "title": "Task title",
      "description": "Details...",
      "status": "PENDING",
      "dueDate": "2026-01-15"
    }
    ```
- **Failure Scenarios:**
  - 400 Bad Request: missing title/dueDate, invalid date, or past dueDate
  - 400 Bad Request: malformed JSON

### Get Task
- **GET** `/tasks/{id}`
- **Success Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "id": "string",
      "title": "Task title",
      "description": "Details...",
      "status": "PENDING",
      "dueDate": "2026-01-15"
    }
    ```
- **Failure Scenarios:**
  - 404 Not Found: task does not exist

### Update Task
- **PUT** `/tasks/{id}`
- **Request Body:**
  ```json
  {
    "title": "New title",          // optional
    "description": "New desc",     // optional
    "status": "IN_PROGRESS",       // optional
    "dueDate": "2026-01-20"        // optional, must be in the future if provided
  }
  ```
- **Success Response:**
  - **Status:** 200 OK
  - **Body:** (same as Get Task)
- **Failure Scenarios:**
  - 404 Not Found: task does not exist
  - 400 Bad Request: invalid or past dueDate

### Delete Task
- **DELETE** `/tasks/{id}`
- **Success Response:**
  - **Status:** 204 No Content
- **Failure Scenarios:**
  - 404 Not Found: task does not exist

### List All Tasks
- **GET** `/tasks`
- **Query Params:**
  - `status` (optional): filter by status (PENDING, IN_PROGRESS, DONE)
  - `page` (optional, default 0): page number (0-based)
  - `size` (optional, default 50): page size
- **Success Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "content": [
        {
          "id": "string",
          "title": "Task title",
          "description": "Details...",
          "status": "PENDING",
          "dueDate": "2026-01-15"
        }
        // ...
      ],
      "pageable": { ... },
      "totalElements": 1,
      "totalPages": 1,
      "last": true,
      "size": 50,
      "number": 0,
      ...
    }
    ```
- **Failure Scenarios:**
  - 400 Bad Request: invalid query params

---

**Dates use ISO-8601 (e.g., 2026-01-15).**
**All error responses:**
```json
{
  "error": "error message",
  "status": 400
}
```
May include `correlation_id` for internal errors.
