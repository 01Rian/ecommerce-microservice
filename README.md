# Ecommerce Microservices APIs

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Docker](https://img.shields.io/badge/Docker-blue)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=black)

A complete e-commerce application based on microservices architecture, composed of three independent services: User API, Product API, and Shopping API.

## 🔍 Overview

This project implements an e-commerce system using a microservices architecture, where each component operates independently and communicates through REST APIs. The application follows Clean Architecture and Domain-Driven Design principles and is containerized with Docker for easy deployment and environment management.

## 🏗️ Architecture

The system consists of three main microservices:

### 🧑‍💼 User API (Port 8080)
- User management (registration, updates, queries)
- Personal data and address storage
- Authentication and authorization

### 📦 Product API (Port 8081)
- Product registration and management
- Categorization
- Inventory control
- Advanced search with pagination and HATEOAS support

### 🛒 Shopping API (Port 8082)
- Shopping cart
- Order processing
- Sales reports
- User purchase history
- Integration with other microservices

Each service has its own database and can be deployed and scaled independently.

## 🔧 Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL** for data persistence
- **Spring WebFlux** for asynchronous communication between microservices
- **Docker** & **Docker Compose** for containerization
- **Flyway** for database migrations
- **Swagger** for API documentation
- **JUnit 5** & **Mockito** for unit and integration testing
- **WireMock** for service integration testing

## ⚙️ Prerequisites

### Required
- Docker installed: [Docker Installation Guide](https://docs.docker.com/get-docker/)
- Docker Compose installed: [Docker Compose Installation Guide](https://docs.docker.com/compose/install/)

### Optional
- Python 3.11+ (recommended for running automation scripts)
- Java 17+ and Maven (for development)

## 🚀 How to Run

### Method 1: Using Python Scripts (Recommended)

This method is ideal for Linux environments and offers a simplified experience:

1. To start all services:
   ```bash
   python deploy.py
   ```

2. To shut down all containers:
   ```bash
   python down.py
   ```

### Method 2: Using Docker Compose Manually

If you prefer not to use Python scripts or are on a different operating system:

1. Create a Docker network for the microservices:
   ```bash
   docker network create ecommerce
   ```

2. Start each service individually:
   ```bash
   # In the project root
   docker compose up -d
   
   # Or navigate to each directory and run
   cd user-api
   docker compose up -d
   
   cd ../product-api
   docker compose up -d
   
   cd ../shopping-api
   docker compose up -d
   ```

## 📊 Key Features

- **RESTful Design** with DTOs and object-relational mapping
- **Robust Validation** of input data
- **Global Exception Handling** with standardized error messages
- **Pagination and Sorting** for resource lists
- **Resilient Communication** between microservices
- **Sales Reports** with custom filters
- **Comprehensive Testing** unit and integration (90%+ coverage)

## 📘 API Documentation

All services have Swagger documentation available:

### User API
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

### Product API
```
http://localhost:8081/api/v1/swagger-ui/index.html
```

### Shopping API
```
http://localhost:8082/api/v1/swagger-ui/index.html
```

## 🧪 Testing

The project includes an extensive test suite:

- **Unit Tests** for all service classes and controllers
- **Integration Tests** to validate communication between microservices
- **Repository Tests** to ensure correct data persistence
- **Mocks and Stubs** to isolate components during testing

## 💾 Database

Each microservice uses its own PostgreSQL database. Migrations are managed automatically during startup using Flyway.

---

Developed with ❤️ as a microservices architecture demonstration project.