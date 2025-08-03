# Workforce Management API – Backend Engineer Challenge 🚀

Welcome! This project is my submission for the Backend Engineer Challenge at Railse.

---

## 🔍 Overview

This is a task-tracking API for managers and operations teams to create, assign, prioritize, and track tasks for staff members. The goal of this project was to:
- Refactor a monolithic Java file into a professional Spring Boot project
- Fix existing bugs
- Implement new business-critical features such as smart task filtering, task priority, and comments with activity logs

---

## 🛠 Tech Stack

- **Language**: Java 17  
- **Framework**: Spring Boot 3.0.4  
- **Build Tool**: Gradle  
- **Data Storage**: In-memory using Java `Map` and `List`  
- **Libraries**:
  - Lombok – to reduce boilerplate
  - MapStruct – for DTO-to-Model conversion
  - Spring Web – for API development

---

## 📂 Project Structure

src/
└── main/
    ├── java/
    │   └── com.railse.hiring.workforcemgmt/
    │       ├── common/
    │       │   ├── exception/
    │       │   │   ├── CustomExceptionHandler.java
    │       │   │   └── ResourceNotFoundException.java
    │       │   └── model/
    │       │       ├── enums/
    │       │       │   └── ReferenceType.java
    │       │       └── response/    (for API response wrappers or response DTOs)
    │       ├── controller/
    │       │   └── TaskManagementController.java
    │       ├── dto/                 (for Data Transfer Objects)
    │       ├── mapper/              (MapStruct or manual mappers)
    │       │   └── ITaskManagementMapper.java
    │       ├── model/               (for JPA Entities)
    │       ├── repository/          (Spring Data JPA Repositories)
    │       ├── service/
    │       │   ├── impl/
    │       │   │   └── TaskManagementService.java
    │       │   └── (interface for TaskManagementService)
    │       └── WorkforceMgmtApplication.java
    └── resources/
        ├── application.properties (or application.yml)
        └── static/, templates/, etc.

