# 🚀 FinFlow Loan Management System

A **microservices-based backend system** built using Spring Boot, Spring Cloud, Docker, RabbitMQ, and monitoring tools like **Prometheus & Grafana** to digitalize the complete loan lifecycle — from application to approval and notification.

---

# 🧠 Overview

FinFlow simulates a **real-world banking loan processing system** with:

* Applicant workflows
* Admin verification system
* Document management
* Event-driven notifications
* Secure API gateway
* **Real-time monitoring & observability**

---

# 🏗️ Architecture

```
Client (Postman / Swagger)
        ↓
API Gateway (JWT + Routing)
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
        ↓
Prometheus (Metrics Collection)
        ↓
Grafana (Visualization Dashboard)
```

---

# 🔧 Tech Stack

## Backend

* Java 17
* Spring Boot
* Spring Security (JWT)
* Spring Cloud Gateway
* Spring Data JPA (Hibernate)

## Microservices & Tools

* Eureka Server (Service Discovery)
* OpenFeign (Inter-service communication)
* RabbitMQ (Async messaging)
* Docker & Docker Compose

## Monitoring & Observability

* Prometheus (Metrics collection)
* Grafana (Dashboard visualization)
* Spring Boot Actuator

## Database

* MySQL

## API Documentation

* Swagger (OpenAPI)

---

# 📦 Microservices

| Service              | Description                          |
| -------------------- | ------------------------------------ |
| Auth Service         | User authentication & JWT generation |
| Application Service  | Loan application lifecycle           |
| Document Service     | Upload & manage documents            |
| Admin Service        | Approval workflow & orchestration    |
| Notification Service | Async notifications via RabbitMQ     |
| API Gateway          | Central entry point + security       |
| Eureka Server        | Service registry                     |

---

# 🔄 Workflow

## 👤 Applicant Flow

* Signup/Login
* Create Loan Application (DRAFT)
* Upload Documents
* Submit Application
* Track Status

---

## 🧑‍💼 Admin Flow

* View Application Queue
* Verify Documents
* Review Application
* Approve / Reject
* Trigger Notifications

---

## 🔔 Notification System (RabbitMQ)

* Admin actions publish events
* Notification Service consumes events
* Stores notifications in DB
* Users fetch notifications via API

---

# 🔐 Security

* JWT-based authentication
* Role-Based Access Control (RBAC)

### Roles:

* APPLICANT
* ADMIN

---

# 📊 Monitoring & Observability

## 🔹 Spring Boot Actuator

Each service exposes metrics via:

```
/actuator/prometheus
```

---

## 🔹 Prometheus

* Collects metrics from all services
* Scrapes data at regular intervals

---

## 🔹 Grafana

* Visualizes metrics in dashboards
* Tracks:

  * Request count
  * Response time
  * Error rate
  * JVM memory & CPU usage

---

## 🔄 Monitoring Flow

```
Service → Actuator → Prometheus → Grafana Dashboard
```

---

# 🐳 Docker Setup

## 🔹 Build all services

```
mvn clean package
```

## 🔹 Run system

```
docker-compose up --build
```

---

# 🌐 Services & Ports

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
| Prometheus           | 9090  |
| Grafana              | 3000  |

---

# 🧪 API Testing

## Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## Example APIs

### Signup

```
POST /auth/signup
```

### Login

```
POST /auth/login
```

### Create Application

```
POST /applications
```

### Admin Decision

```
POST /admin/applications/{id}/decision
```

---

# 📊 Features

* Microservices architecture
* Event-driven communication (RabbitMQ)
* Real-time notifications
* Document verification workflow
* Role-based security
* Centralized API Gateway
* Service discovery using Eureka
* **Monitoring with Prometheus & Grafana**
* Dockerized deployment

---

# 🚀 Future Enhancements

* Loan eligibility engine
* EMI & repayment module
* Email/SMS notifications
* Redis caching
* Alerting system (Grafana alerts)
* Distributed tracing (Zipkin)

---

# 👨‍💻 Author

**Akhil Rana**

---

# ⭐ If you like this project

Give it a ⭐ on GitHub and share feedback!
