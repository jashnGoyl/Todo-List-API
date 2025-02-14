# 📝 To-Do List API

A simple and efficient RESTful API for managing to-do lists, built using Spring Boot and MySQL. This project follows the standard to-do list API specifications outlined at [roadmap.sh](https://roadmap.sh/projects/todo-list-api).

## ✨ Features
- 🔑 User authentication using JWT
- ✅ CRUD operations for tasks
- 📌 Task categorization and prioritization
- 🔒 Secure API endpoints
- 🐳 Dockerized deployment

## 🚀 Technologies Used
- ☕ Java 17+
- 🖥️ Spring Boot
- 🔒 Spring Security
- 🗄️ Spring Data JPA
- 🔧 Maven
- 🛢️ MySQL
- 🐳 Docker & Docker Compose

## 🛠 Getting Started
### 🐳 Run with Docker
To run the application using Docker, follow these steps:
1. Build the package using Maven:
   ```sh
   mvn clean package
   ```
2. Start the application using Docker Compose:
   ```sh
   docker compose up -d
   ```
3. To stop and remove the containers:
   ```sh
   docker compose down
   ```

### 💻 Run Locally (Without Docker)
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/todo-list-api.git
   cd todo-list-api
   ```
2. Configure the database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/todolist_db
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   ```
3. Build and run the application:
   ```sh
   mvn clean package
   java -jar target/todolist-api-0.0.1-SNAPSHOT.jar
   ```

## 📌 API Endpoints
| 🛠 Method | 🔗 Endpoint          | 📋 Description      |
|----------|------------------------|----------------------|
| POST     | `/todos/auth/register` | Register a new user  |
| POST     | `/todos/auth/login`    | Authenticate user    |
| GET      | `/todos`               | Get all tasks        |
| POST     | `/todos`               | Create a new task    |
| PUT      | `/todos/{id}`          | Update a task        |
| DELETE   | `/todos/{id}`          | Delete a task        |
