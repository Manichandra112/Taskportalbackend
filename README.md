# AI-Powered Task Management Portal — Backend API

This repository contains the Java Spring Boot backend for the AI-Powered Task Management Portal. It provides secure REST APIs for user authentication, JWT handling, task CRUD operations, and Google Gemini AI content generation.

## 🛠️ Technology Stack

* **Core Framework:** Spring Boot 3.4.x / Java 17
* **Security:** Spring Security & JWT (JSON Web Tokens)
* **Data Access:** Spring Data JPA (Hibernate)
* **Database:** MySQL 8.x
* **AI Integration:** Google Gemini API (via HTTP Client integration)
* **Build Tool:** Maven

---

## 🚀 Key Features

* **JWT Stateless Authentication:** Secure signup/signin endpoints that issue self-signed JWT keys for subsequent authorization.
* **Task CRUD:** Full REST coverage for tasks (Create, Retrieve, Update, Delete) mapped to specific authenticated users.
* **AI Task Generation:** Integrates Google Gemini models to automatically generate actionable descriptions, estimate effort, and suggest task priorities based on a user-submitted title.
* **Automatic Database Migration:** Uses Hibernate `ddl-auto=update` to instantly create/modify database tables (`users` and `tasks`) on start.
* **Global Exception Handling:** Clean JSON error mapping for validation failures, bad credentials, and resource not found exceptions.

---

## ⚙️ Configuration & Prerequisites

### 1. Database Setup
Create a MySQL database named `taskmanager`.
Ensure your MySQL username and password match those in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=MyNewPassword123!
```

### 2. Gemini API Credentials
The backend makes standard HTTPS requests to Google's Generative Language API. Configure your API key in `application.properties`:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
```

---

## 💻 Running the Application

Use the Maven wrapper to build and start the dev server:

```bash
# Clean and compile the project
./mvnw clean compile

# Run the Spring Boot application
./mvnw spring-boot:run
```
The server will boot on `http://localhost:8080`.

---

## 🗺️ API Documentation

### Authentication (`/api/auth`)
* `POST /api/auth/register`: Create a new user account.
* `POST /api/auth/login`: Login with email and password to receive a JWT access token.

### Tasks (`/api/tasks`) — *Requires Bearer Token Authorization*
* `GET /api/tasks`: Fetch all tasks for the logged-in user (supports status query parameter).
* `GET /api/tasks/{id}`: Get a single task detail.
* `POST /api/tasks`: Create a new task (validates `dueDate` to prevent past dates).
* `PUT /api/tasks/{id}`: Edit an existing task.
* `PATCH /api/tasks/{id}/status`: Quick update status (transition between `TODO`, `IN_PROGRESS`, `DONE`).
* `DELETE /api/tasks/{id}`: Delete a task.

### AI Assist (`/api/ai`) — *Requires Bearer Token Authorization*
* `POST /api/ai/generate`: Given a title, prompts Gemini to suggest descriptions, priorities, and effort times.
