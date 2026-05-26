# Online Grocery Order Management System

A RESTful API service built with **Java**, **Spring Boot**, and **MongoDB** to manage an online grocery ordering system. The application supports full CRUD operations for **Customers**, **Grocery Items**, and **Orders**.

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Data Model](#data-model)
- [API Documentation](#api-documentation)
- [Sample Workflow](#sample-workflow)
- [Error Handling](#error-handling)
- [Running Tests](#running-tests)
- [Health Check](#health-check)

---

## Problem Statement

Develop a RESTful API service using Spring Boot to manage an online grocery ordering system for customers, grocery items, and orders. The system should support CRUD operations for each entity, focusing on basic data management without complex business logic.

### Entities

| Entity        | Description                              |
|---------------|------------------------------------------|
| **Customer**  | Stores customer information              |
| **Grocery Item** | Represents available grocery products |
| **Order**     | Tracks orders placed by customers        |

### Relationships

- A **customer** can place **multiple orders**.
- An **order** can contain **one or more grocery items**.
- A **grocery item** can be part of **multiple orders**.

---

## Features

- Full CRUD operations for Customers, Grocery Items, and Orders
- MongoDB persistence with Spring Data MongoDB
- Input validation using Jakarta Bean Validation
- Centralized error handling with meaningful HTTP status codes
- Automatic total price calculation when creating/updating orders
- Filter grocery items by category
- Filter orders by customer
- Unit tests for core service operations
- Spring Actuator health endpoint

---

## Tech Stack

| Technology        | Purpose                          |
|-------------------|----------------------------------|
| Java 17           | Programming language               |
| Spring Boot 4.0.6 | Application framework              |
| Spring Web MVC    | REST API layer                   |
| Spring Data MongoDB | Database access                |
| Spring Validation | Request validation               |
| Spring Security   | Security (configured as open API)|
| Lombok            | Reduces boilerplate code         |
| JUnit 5 + Mockito | Unit testing                     |
| Maven             | Build and dependency management  |
| MongoDB           | NoSQL database                   |

---

## Prerequisites

Before running the application, ensure the following are installed:

| Tool      | Version   | Check Command        |
|-----------|-----------|----------------------|
| Java JDK  | 17+       | `java -version`      |
| Maven     | 3.9+      | `./mvnw -version`    |
| MongoDB   | 4.4+      | `mongod --version`   |

> The project includes a Maven Wrapper (`mvnw`), so a separate Maven installation is optional.

---

## Project Structure

```
grocery-management/
├── src/
│   ├── main/
│   │   ├── java/com/onlinemanagementgrocery/grocerymanagement/
│   │   │   ├── GroceryManagementApplication.java   # Main entry point
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java             # Open API security config
│   │   │   ├── controller/
│   │   │   │   ├── CustomerController.java         # Customer REST endpoints
│   │   │   │   ├── GroceryItemController.java      # Grocery item REST endpoints
│   │   │   │   └── OrderController.java            # Order REST endpoints
│   │   │   ├── dto/
│   │   │   │   └── OrderRequest.java               # Order create/update payload
│   │   │   ├── exception/
│   │   │   │   ├── ErrorResponse.java              # Standard error JSON format
│   │   │   │   ├── GlobalExceptionHandler.java     # Centralized exception handler
│   │   │   │   └── ResourceNotFoundException.java  # Custom 404 exception
│   │   │   ├── model/
│   │   │   │   ├── Customer.java                   # Customer document
│   │   │   │   ├── GroceryItem.java                # Grocery item document
│   │   │   │   ├── Order.java                      # Order document
│   │   │   │   └── OrderItem.java                  # Line item within an order
│   │   │   ├── repository/
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── GroceryItemRepository.java
│   │   │   │   └── OrderRepository.java
│   │   │   └── service/
│   │   │       ├── CustomerService.java
│   │   │       ├── GroceryItemService.java
│   │   │       └── OrderService.java
│   │   └── resources/
│   │       └── application.properties              # App configuration
│   └── test/
│       ├── java/.../service/                       # Unit tests
│       └── resources/
│           └── application-test.properties
├── pom.xml
└── README.md
```

---

## Getting Started

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd grocery-management
```

### 2. Start MongoDB

**Option A — System service (Linux):**
```bash
sudo systemctl start mongod
sudo systemctl status mongod
```

**Option B — Manual start:**
```bash
mkdir -p ~/mongo-data
mongod --dbpath ~/mongo-data
```

MongoDB will listen on `localhost:27017` by default.

### 3. Build the Project

```bash
./mvnw clean package
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at:

```
http://localhost:8080
```

---

## Configuration

Configuration is defined in `src/main/resources/application.properties`:

```properties
spring.application.name=grocery-management

# MongoDB
spring.mongodb.uri=mongodb://localhost:27017/grocery-management
spring.data.mongodb.auto-index-creation=true

# Server
server.port=8080

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

| Property | Description | Default |
|----------|-------------|---------|
| `spring.mongodb.uri` | MongoDB connection string | `mongodb://localhost:27017/grocery-management` |
| `server.port` | HTTP server port | `8080` |
| `spring.data.mongodb.auto-index-creation` | Auto-create indexes (e.g. unique email) | `true` |

To use a different MongoDB host or database, update `spring.mongodb.uri` before starting the app.

---

## Data Model

### Customer

| Field   | Type   | Required | Description              |
|---------|--------|----------|--------------------------|
| id      | String | Auto     | MongoDB document ID      |
| name    | String | Yes      | Customer full name       |
| email   | String | Yes      | Unique email address     |
| address | String | Yes      | Delivery address         |
| phone   | String | Yes      | Contact phone number     |

**MongoDB collection:** `customers`

---

### Grocery Item

| Field    | Type       | Required | Description                |
|----------|------------|----------|----------------------------|
| id       | String     | Auto     | MongoDB document ID        |
| name     | String     | Yes      | Product name               |
| category | String     | Yes      | Category (e.g. Dairy)      |
| price    | BigDecimal | Yes      | Unit price (≥ 0)           |
| quantity | Integer    | Yes      | Available stock (≥ 0)      |

**MongoDB collection:** `grocery_items`

---

### Order

| Field      | Type            | Required | Description                          |
|------------|-----------------|----------|--------------------------------------|
| id         | String          | Auto     | MongoDB document ID                  |
| customerId | String          | Yes      | Reference to a Customer document     |
| items      | List<OrderItem> | Yes      | One or more grocery items in order   |
| orderDate  | LocalDateTime   | Auto     | Set automatically if not provided    |
| totalPrice | BigDecimal      | Auto     | Calculated from item prices × qty    |

**MongoDB collection:** `orders`

---

### OrderItem (embedded in Order)

| Field         | Type       | Required | Description                          |
|---------------|------------|----------|--------------------------------------|
| groceryItemId | String     | Yes      | Reference to a GroceryItem document  |
| quantity      | Integer    | Yes      | Quantity ordered (≥ 1)               |
| unitPrice     | BigDecimal | Auto     | Price snapshot at time of order      |

---

## API Documentation

Base URL: `http://localhost:8080`

All request and response bodies use `Content-Type: application/json`.

---

### Customer APIs

Base path: `/api/customers`

#### Get All Customers

```
GET /api/customers
```

**Response `200 OK`:**
```json
[
  {
    "id": "665a1b2c3d4e5f6789012345",
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main Street, City",
    "phone": "555-0100"
  }
]
```

---

#### Get Customer by ID

```
GET /api/customers/{id}
```

**Response `200 OK`:** Customer object  
**Response `404 Not Found`:** Customer does not exist

---

#### Create Customer

```
POST /api/customers
```

**Request body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "address": "123 Main Street, City",
  "phone": "555-0100"
}
```

**Response `201 Created`:** Created customer with generated `id`  
**Response `400 Bad Request`:** Validation error or duplicate email

---

#### Update Customer

```
PUT /api/customers/{id}
```

**Request body:** Same as create (all fields required)

**Response `200 OK`:** Updated customer  
**Response `404 Not Found`:** Customer does not exist

---

#### Delete Customer

```
DELETE /api/customers/{id}
```

**Response `204 No Content`:** Successfully deleted  
**Response `404 Not Found`:** Customer does not exist

---

### Grocery Item APIs

Base path: `/api/grocery-items`

#### Get All Grocery Items

```
GET /api/grocery-items
```

**Optional query parameter:**

| Parameter  | Description              | Example                          |
|------------|--------------------------|----------------------------------|
| `category` | Filter items by category | `/api/grocery-items?category=Dairy` |

**Response `200 OK`:** Array of grocery items

---

#### Get Grocery Item by ID

```
GET /api/grocery-items/{id}
```

**Response `200 OK`:** Grocery item object  
**Response `404 Not Found`:** Item does not exist

---

#### Create Grocery Item

```
POST /api/grocery-items
```

**Request body:**
```json
{
  "name": "Whole Milk",
  "category": "Dairy",
  "price": 3.50,
  "quantity": 100
}
```

**Response `201 Created`:** Created item with generated `id`

---

#### Update Grocery Item

```
PUT /api/grocery-items/{id}
```

**Request body:** Same as create

**Response `200 OK`:** Updated item

---

#### Delete Grocery Item

```
DELETE /api/grocery-items/{id}
```

**Response `204 No Content`:** Successfully deleted

---

### Order APIs

Base path: `/api/orders`

#### Get All Orders

```
GET /api/orders
```

**Optional query parameter:**

| Parameter    | Description               | Example                        |
|--------------|---------------------------|--------------------------------|
| `customerId` | Filter orders by customer | `/api/orders?customerId={id}`  |

**Response `200 OK`:**
```json
[
  {
    "id": "665a1b2c3d4e5f6789012346",
    "customerId": "665a1b2c3d4e5f6789012345",
    "items": [
      {
        "groceryItemId": "665a1b2c3d4e5f6789012347",
        "quantity": 2,
        "unitPrice": 3.50
      },
      {
        "groceryItemId": "665a1b2c3d4e5f6789012348",
        "quantity": 1,
        "unitPrice": 2.00
      }
    ],
    "orderDate": "2026-05-26T10:30:00",
    "totalPrice": 9.00
  }
]
```

---

#### Get Order by ID

```
GET /api/orders/{id}
```

**Response `200 OK`:** Order object  
**Response `404 Not Found`:** Order does not exist

---

#### Create Order

```
POST /api/orders
```

**Request body:**
```json
{
  "customerId": "665a1b2c3d4e5f6789012345",
  "items": [
    {
      "groceryItemId": "665a1b2c3d4e5f6789012347",
      "quantity": 2
    },
    {
      "groceryItemId": "665a1b2c3d4e5f6789012348",
      "quantity": 1
    }
  ]
}
```

> `orderDate` is optional. If omitted, the current date/time is used automatically.  
> `totalPrice` and `unitPrice` are calculated automatically from grocery item prices.

**Response `201 Created`:** Created order  
**Response `404 Not Found`:** Customer or grocery item not found

---

#### Update Order

```
PUT /api/orders/{id}
```

**Request body:** Same as create

**Response `200 OK`:** Updated order with recalculated total price

---

#### Delete Order

```
DELETE /api/orders/{id}
```

**Response `204 No Content`:** Successfully deleted

---

## Sample Workflow

Run these commands after starting the application and MongoDB. Replace `{id}` placeholders with actual IDs returned from previous steps.

### Step 1 — Create a Customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main Street",
    "phone": "555-0100"
  }'
```

### Step 2 — Create Grocery Items

```bash
curl -X POST http://localhost:8080/api/grocery-items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Whole Milk",
    "category": "Dairy",
    "price": 3.50,
    "quantity": 100
  }'
```

```bash
curl -X POST http://localhost:8080/api/grocery-items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Brown Bread",
    "category": "Bakery",
    "price": 2.00,
    "quantity": 50
  }'
```

### Step 3 — Place an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "<customer-id>",
    "items": [
      { "groceryItemId": "<milk-item-id>", "quantity": 2 },
      { "groceryItemId": "<bread-item-id>", "quantity": 1 }
    ]
  }'
```

Expected total price: `(3.50 × 2) + (2.00 × 1) = 9.00`

### Step 4 — View Orders for a Customer

```bash
curl http://localhost:8080/api/orders?customerId=<customer-id>
```

### Step 5 — Filter Grocery Items by Category

```bash
curl "http://localhost:8080/api/grocery-items?category=Dairy"
```

---

## Error Handling

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-05-26T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 665a1b2c3d4e5f6789012345",
  "path": "/api/customers/665a1b2c3d4e5f6789012345"
}
```

Validation errors include a `validationErrors` map:

```json
{
  "timestamp": "2026-05-26T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/customers",
  "validationErrors": {
    "email": "Email must be valid",
    "name": "Name is required"
  }
}
```

### HTTP Status Codes

| Code | Meaning              | When Used                                      |
|------|----------------------|------------------------------------------------|
| 200  | OK                   | Successful GET or PUT                          |
| 201  | Created              | Successful POST                                |
| 204  | No Content           | Successful DELETE                              |
| 400  | Bad Request          | Validation failure or duplicate email          |
| 404  | Not Found            | Customer, item, or order not found             |
| 500  | Internal Server Error| Unexpected server error                        |

---

## Running Tests

The project includes unit tests for the service layer using JUnit 5 and Mockito.

```bash
./mvnw test
```

### Test Coverage

| Test Class              | What It Tests                                      |
|-------------------------|----------------------------------------------------|
| `CustomerServiceTest`   | CRUD, not-found, duplicate email validation        |
| `GroceryItemServiceTest`| Get, update, not-found scenarios                   |
| `OrderServiceTest`      | Order creation, total price calculation, not-found |
| `GroceryManagementApplicationTests` | Application class loads correctly      |

---

## Health Check

Spring Actuator provides a health endpoint:

```
GET http://localhost:8080/actuator/health
```

Use this to verify the application is running. When MongoDB is connected, the health status will reflect database connectivity.

---

## Architecture Overview

```
Client (Postman / curl / browser)
        │
        ▼
  REST Controllers          ← HTTP request/response handling
        │
        ▼
  Service Layer             ← Business logic & validation
        │
        ▼
  Repository Layer          ← Spring Data MongoDB
        │
        ▼
  MongoDB Database          ← Persistent storage
```

---

## Notes for Submission

- Ensure **MongoDB is running** before starting the application.
- All API endpoints are **publicly accessible** (no authentication required), as this is a student data-management project.
- The database name used is `grocery-management`. Data persists across restarts unless MongoDB data is cleared.
- Use **Postman** or **curl** to test all CRUD endpoints before submitting your GitHub repository.

---

## Author

Student Project — Online Grocery Order Management System  
Course Assignment Submission
