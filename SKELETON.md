# Walking Skeleton Plan

## Overview

The goal of our Walking Skeleton is to verify that the three main components of our application can successfully communicate with each other:

**Front End → Back End → Database → Back End → Front End**

The Walking Skeleton is not intended to implement the full application. Instead, it will provide one simple end-to-end feature that proves our core technology stack is correctly integrated.

## Technology Stack

| Component | Technology | Purpose |
|---|---|---|
| Front End | React, Vite, TypeScript | User interface and user interactions |
| Back End | Java, Spring Boot | Application logic and REST API |
| Database | MySQL | Persistent application data |
| Communication | REST API / JSON | Communication between frontend and backend |
| Infrastructure | Docker, CS VM | Running and deploying application components |
| Future Integration | GitLab API | Retrieve GitLab project and development data |

## Walking Skeleton Feature

For the Walking Skeleton, we will implement a simple **Project Registration** feature.

A user will enter a GitLab project name in the React frontend and click an **Add Project** button.

The frontend will send the project name to the Spring Boot backend using a REST API request. Spring Boot will store the project in MySQL and return the saved project as JSON. The frontend will then display the returned project information.

### End-to-End Flow

```text
User
 ↓
React Frontend
 ↓ REST / JSON
Spring Boot
 ↓ SQL / JDBC
MySQL
 ↑
Spring Boot
 ↓ JSON
React Frontend
 ↓
User
```

This feature is intentionally simple. Its purpose is to confirm that all three major components of the application can communicate successfully.

## Component Responsibilities

| Component | Walking Skeleton Responsibility |
|---|---|
| React Frontend | Accept a project name and send it to the backend |
| Spring Boot Backend | Receive the request, process it, and communicate with MySQL |
| MySQL Database | Store the project information |
| Spring Boot Backend | Return the stored project as JSON |
| React Frontend | Display the returned project information |

## Front End

The React interface will contain:

- A text input for the project name
- An **Add Project** button
- A section displaying the backend response

Example:

```text
GitLab Project Dashboard

Project Name:
[ CS506 Team Project ]

[ Add Project ]

Project successfully added!
```

When the user clicks **Add Project**, React will send the entered project name to the Spring Boot backend.

## Back End and REST API

The Spring Boot backend will expose a simple REST endpoint:

```http
POST /api/projects
```

Example request:

```json
{
  "name": "CS506 Team Project"
}
```

The backend will:

1. Receive the request from React.
2. Store the project in MySQL.
3. Return the saved project as JSON.

Example response:

```json
{
  "id": 1,
  "name": "CS506 Team Project"
}
```

We may also implement:

```http
GET /api/projects
```

to retrieve previously stored projects.

## Database

For the Walking Skeleton, we only need a minimal database table.

| Field | Type | Description |
|---|---|---|
| `id` | INT | Unique project identifier |
| `name` | VARCHAR(255) | Project name |

Example:

```sql
CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

This is enough to verify that Spring Boot can successfully write data to and retrieve data from MySQL.

## System Architecture

```text
                         Future Integration

                         ┌──────────────┐
                         │  GitLab API  │
                         └──────▲───────┘
                                │
                                │ API
                                │
                                ▼

┌─────────────────┐   REST/JSON   ┌─────────────────┐   SQL/JDBC   ┌─────────────────┐
│  React + Vite   │ ────────────> │   Spring Boot   │ ───────────> │      MySQL      │
│   TypeScript    │ <──────────── │      Java       │ <─────────── │    Database     │
└─────────────────┘      JSON      └─────────────────┘              └─────────────────┘
        ▲
        │
        │
      User
```

The main communication path is:

```text
Frontend ──REST──> Spring Boot ──SQL──> MySQL
   ↑                    |
   └────── JSON ────────┘
```

## Docker and Deployment

The Spring Boot backend and MySQL database will run using Docker.

```text
CS VM
│
└── Docker Network
    ├── Spring Boot Container
    └── MySQL Container
```

Spring Boot will connect to the MySQL container through the Docker network.

The application will later run on the CS VM.

## GitLab API Integration

The final application will also retrieve data from the GitLab API through the Spring Boot backend.

```text
                  GitLab API
                      ↑↓
                      │
React ──REST──> Spring Boot ──SQL──> MySQL
  ↑                 |
  └──── JSON ───────┘
```

GitLab API integration is not required for the initial Walking Skeleton.

Our first priority is proving that the following core flow works:

**React → Spring Boot → MySQL → Spring Boot → React**

Once this works, GitLab API integration, analytics, and data visualization can be added.

## Implementation Plan

| Step | Task | Expected Result |
|---|---|---|
| 1 | Set up MySQL | Database is running |
| 2 | Connect Spring Boot to MySQL | Backend can access the database |
| 3 | Implement `POST /api/projects` | Backend can store a project |
| 4 | Create React project input form | User can enter a project name |
| 5 | Connect React to Spring Boot | Frontend can send REST requests |
| 6 | Return stored project as JSON | Backend sends data back to frontend |
| 7 | Display response in React | User can see the stored project |
| 8 | Run backend and MySQL with Docker | Components work together in containers |
| 9 | Verify on CS VM | Complete stack works in the target environment |

## Success Criteria

The Walking Skeleton will be complete when:

- [ ] React accepts user input.
- [ ] React sends a REST request to Spring Boot.
- [ ] Spring Boot receives and processes the request.
- [ ] Spring Boot stores the project in MySQL.
- [ ] Spring Boot returns the stored project as JSON.
- [ ] React displays the returned result.
- [ ] Spring Boot and MySQL run successfully with Docker.
- [ ] The complete end-to-end workflow can be demonstrated.

### Final Demonstration

```text
Enter Project Name
        ↓
     React
        ↓
 POST /api/projects
        ↓
   Spring Boot
        ↓
      MySQL
        ↓
   Stored Data
        ↓
   Spring Boot
        ↓
 JSON Response
        ↓
     React
        ↓
Display Project
```

Completing this flow will demonstrate that our core frontend, backend, and database architecture is successfully integrated and ready for further development.