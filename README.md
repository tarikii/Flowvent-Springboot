# Flowvent

Flowvent is a REST API for event management and ticket purchases, built with Spring Boot.

The API allows users to register, authenticate with JWT, browse events, buy tickets, manage ticket ownership, and expose event availability information such as sold and available tickets.

---

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* Spring Security
* JWT Authentication
* PostgreSQL
* Docker Compose
* Swagger / OpenAPI
* JUnit 5
* Mockito

---

## Features

* User registration and login
* JWT-based authentication
* Role-based authorization with `ADMIN` and `CLIENT`
* Event CRUD operations
* Event search with dynamic filters
* Paginated event and ticket listings
* Ticket purchase flow
* Ticket ownership validation
* Prevention of duplicate seats for the same event
* Prevention of ticket purchases for past events
* Event availability metrics:

    * `soldTickets`
    * `availableTickets`
* Global exception handling
* Request validation with clear error responses
* Unit and controller tests

---

## Roles

### ADMIN

An admin can:

* Create, update and delete events
* List all clients
* List all tickets
* List tickets by event
* Manage tickets

### CLIENT

A client can:

* Browse public events
* Buy tickets
* View their own tickets
* Update or delete only their own tickets

---

## Requirements

* Java 21 or newer
* Docker Desktop
* Maven Wrapper included in the project

---

## Environment Variables

The application uses environment variables for database and JWT configuration.

Create a `.env` file based on `.env.example`.

### Example `.env.example`

```env
DB_URL=jdbc:postgresql://localhost:5432/flowvent
DB_USER=postgres
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400000
```

Make sure the real `.env` file is not committed to Git.

### `.gitignore`

```gitignore
.env
```

### Example `application.properties`

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/flowvent}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

jwt.secret=${JWT_SECRET:flowventSecretKeyFlowventSecretKeyFlowventSecretKey123}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

---

## Database Setup

Start PostgreSQL using Docker Compose:

```bash
docker compose up -d
```

PostgreSQL will be available at:

```text
localhost:5432
```

Default database name:

```text
flowvent
```

---

## Run the Application

From the project root:

```bash
.\mvnw spring-boot:run
```

The API will run at:

```text
http://localhost:8081
```

---

## Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```

---

## Authentication Flow

### Register

```http
POST /api/auth/register
```

#### Request

```json
{
  "username": "client",
  "email": "client@flowvent.com",
  "password": "password123",
  "role": "CLIENT"
}
```

#### Response

```json
{
  "token": "jwt-token"
}
```

---

### Login

```http
POST /api/auth/login
```

#### Request

```json
{
  "email": "client@flowvent.com",
  "password": "password123"
}
```

#### Response

```json
{
  "token": "jwt-token"
}
```

Use the JWT token in Swagger by clicking **Authorize**.

---

## Main API Endpoints

### Auth

| Method | Endpoint             | Description                 | Auth   |
| ------ | -------------------- | --------------------------- | ------ |
| POST   | `/api/auth/register` | Register a new user         | Public |
| POST   | `/api/auth/login`    | Login and receive JWT token | Public |

---

### Events

| Method | Endpoint               | Description                 | Auth   |
| ------ | ---------------------- | --------------------------- | ------ |
| GET    | `/api/events`          | List events with pagination | Public |
| GET    | `/api/events/{id}`     | Get event by ID             | Public |
| GET    | `/api/events/upcoming` | List upcoming events        | Public |
| GET    | `/api/events/search`   | Search events with filters  | Public |
| POST   | `/api/events`          | Create event                | ADMIN  |
| PUT    | `/api/events/{id}`     | Update event                | ADMIN  |
| DELETE | `/api/events/{id}`     | Delete event                | ADMIN  |

---

### Tickets

| Method | Endpoint                       | Description                       | Auth           |
| ------ | ------------------------------ | --------------------------------- | -------------- |
| GET    | `/api/tickets`                 | List all tickets                  | ADMIN          |
| GET    | `/api/tickets/me`              | List authenticated user's tickets | CLIENT / ADMIN |
| GET    | `/api/tickets/event/{eventId}` | List tickets by event             | ADMIN          |
| POST   | `/api/tickets`                 | Buy ticket                        | CLIENT / ADMIN |
| PUT    | `/api/tickets/{id}`            | Update ticket seat                | Owner / ADMIN  |
| DELETE | `/api/tickets/{id}`            | Delete ticket                     | Owner / ADMIN  |

---

### Clients

| Method | Endpoint            | Description   | Auth  |
| ------ | ------------------- | ------------- | ----- |
| GET    | `/api/clients`      | List clients  | ADMIN |
| POST   | `/api/clients`      | Create client | ADMIN |
| PUT    | `/api/clients/{id}` | Update client | ADMIN |
| DELETE | `/api/clients/{id}` | Delete client | ADMIN |

---

## Event Search

Example:

```http
GET /api/events/search?title=strokes&fromDate=2029-01-01&toDate=2029-12-31&minPrice=20&maxPrice=100&page=0&size=10
```

### Supported Filters

| Parameter  | Description           |
| ---------- | --------------------- |
| `title`    | Event title           |
| `fromDate` | Minimum date          |
| `toDate`   | Maximum date          |
| `minPrice` | Minimum price         |
| `maxPrice` | Maximum price         |
| `page`     | Page number           |
| `size`     | Page size             |
| `sort`     | Sorting configuration |

### Example Response

```json
{
  "id": 1,
  "title": "The Strokes Concert",
  "description": "Come enjoy the incredible songs of this band!",
  "date": "2029-12-21",
  "maximumCapacity": 100,
  "ticketPrice": 45.67,
  "soldTickets": 1,
  "availableTickets": 99
}
```

---

## Ticket Purchase

```http
POST /api/tickets
```

### Request

```json
{
  "eventId": 1,
  "seatNumber": 12
}
```

### Response

```json
{
  "id": 1,
  "clientName": "client",
  "clientEmail": "client@flowvent.com",
  "eventId": 1,
  "eventTitle": "The Strokes Concert",
  "eventDate": "2029-12-21",
  "seat": 12,
  "ticketPrice": 45.67,
  "purchaseDate": "2026-06-21T19:53:36.312255"
}
```

---

## Pagination

```http
GET /api/events?page=0&size=10
GET /api/events/search?page=0&size=10
GET /api/events/upcoming?page=0&size=10
GET /api/tickets?page=0&size=10
GET /api/tickets/me?page=0&size=10
GET /api/tickets/event/{eventId}?page=0&size=10
```

### Sorting Examples

```http
GET /api/events?page=0&size=5&sort=date,asc
```

```http
GET /api/tickets?page=0&size=5&sort=purchaseDate,desc
```

---

## Validation Errors

Example response:

```json
{
  "timestamp": "2026-06-21T21:23:32.5869783",
  "status": 400,
  "error": "Validation failed",
  "messages": {
    "ticketPrice": "Ticket price cannot be negative",
    "description": "Description is required",
    "maximumCapacity": "Maximum capacity must be at least 1",
    "date": "Event date must be in the future",
    "title": "Title is required"
  }
}
```

---

## Business Rules

* No ticket purchases for past events
* No purchases when event is full
* No duplicate seats per event
* Users can only modify their own tickets
* Admins can manage everything
* CLIENT users must have a client profile

---

## Run Tests

```bash
.\mvnw test
```

### If Java is not detected (PowerShell)

```powershell
$env:JAVA_HOME="C:\Users\beryp\.jdks\openjdk-23.0.1"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw test
```

---

## Test Coverage

* Ticket purchase flow
* Ticket business rules
* Ticket ownership
* Event creation and update
* Event availability calculation
* Authentication (register & login)
* Controller validation

---

## Useful Docker Commands

### Start database

```bash
docker compose up -d
```

### Stop database

```bash
docker compose down
```

### Remove volumes

```bash
docker compose down -v
```

### Check containers

```bash
docker ps
```

### View logs

```bash
docker logs flowvent-postgres
```

---

## Project Status

Flowvent includes authentication, authorization, event management, ticket purchases, pagination, validation, exception handling, and automated tests.

The backend is ready to be connected to a frontend client.
