# 🚀 FinFlow Loan Management System

A **microservices-based backend system** built using **Spring Boot, Spring Cloud, Docker, and RabbitMQ** to digitalize the complete loan lifecycle — from application to approval and notification.

---

## 🧠 Overview

FinFlow is designed to simulate a **real-world banking loan processing system** with:

* Applicant workflows
* Admin verification system
* Document management
* Event-driven notifications
* Secure API gateway

---

## 🏗️ Architecture

```
Client (Postman / Swagger)
        ↓
API Gateway
        ↓
------------------------------------------------
| Auth Service        | Application Service      |
| Document Service    | Admin Service            |
| Notification Service (RabbitMQ Consumer)      |
------------------------------------------------
        ↓
MySQL Database + RabbitMQ
        ↓
Eureka Service Discovery
```

---

## 🔧 Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security (JWT)
* Spring Cloud Gateway
* Spring Data JPA (Hibernate)

### Microservices & Tools

* Eureka Server (Service Discovery)
* OpenFeign (Inter-service communication)
* RabbitMQ (Async messaging)
* Docker & Docker Compose

### Database

* MySQL

### API Documentation

* Swagger (OpenAPI)

---

## 📦 Microservices

| Service              | Description                       |
| -------------------- | --------------------------------- |
| Auth Service         | User authentication & management  |
| Application Service  | Loan application lifecycle        |
| Document Service     | Upload & manage documents         |
| Admin Service        | Approval workflow & orchestration |
| Notification Service | Async notifications via RabbitMQ  |
| API Gateway          | Central entry point               |
| Eureka Server        | Service registry                  |

---

## 🔄 Workflow

### 👤 Applicant Flow

1. Signup/Login
2. Create Loan Application (DRAFT)
3. Upload Documents
4. Submit Application
5. Track Status

---

### 🧑‍💼 Admin Flow

1. View Application Queue
2. Verify Documents
3. Review Application
4. Approve / Reject
5. Trigger Notifications

---

## 🔔 Notification System (RabbitMQ)

* Admin actions publish events
* Notification Service consumes events
* Stores notifications in DB
* Users can fetch via API

---

## 🔐 Security

* JWT-based authentication
* Role-Based Access Control (RBAC)
* Roles:

  * APPLICANT
  * ADMIN

---

## 🐳 Docker Setup

### Build all services

```bash
mvn clean package
```

### Run system

```bash
docker-compose up --build
```

---

## 🌐 Services & Ports

| Service              | Port  |
| -------------------- | ----- |
| API Gateway          | 8080  |
| Auth Service         | 8081  |
| Application Service  | 8082  |
| Document Service     | 8083  |
| Admin Service        | 8084  |
| Notification Service | 8085  |
| Eureka Server        | 8761  |
| RabbitMQ UI          | 15672 |

---

## 🧪 API Testing

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

### Example APIs

#### Signup

```http
POST /auth/signup
```

#### Login

```http
POST /auth/login
```

#### Create Application

```http
POST /applications
```

#### Admin Decision

```http
POST /admin/applications/{id}/decision
```

---

## 📊 Features

* Microservices architecture
* Event-driven communication
* Real-time notifications
* Document verification workflow
* Role-based security
* Dockerized deployment

---

## 🚀 Future Enhancements

* Loan eligibility engine
* EMI & repayment module
* Email/SMS notifications
* Redis caching
* Advanced analytics dashboard

---

## 👨‍💻 Author

**Akhil Rana**

---

## ⭐ If you like this project

Give it a ⭐ on GitHub and share feedback!
