<<<<<<< HEAD
# RevWorkForce

RevWorkForce is a full-stack human resource management (HRM) web application.

## 🚀 Features

### For Employees:
*   **Secure Authentication**: Login system with role-based access control.
*   **Comprehensive Dashboards**: View key information such as leave balances, pending leaves, and company announcements.
*   **Leave Management**: 
    *   View real-time leave balances across different leave types.
    *   Apply for leaves and track approval status.
    *   Cancel pending leave requests.
*   **Performance Management**:
    *   Submit self-assessments.
    *   Set, track, and manage ongoing goals.
    *   Review manager feedback and ratings.
*   **Profile Management**: Update contact information and personal details.
*   **Real-time Notifications**: Receive system-generated notifications for leave approvals, feedback, and more.
*   **Company Directory mapping**: View departmental information and company-wide holidays.

### For Managers:
*   Includes all Employee capabilities, plus:
*   **Team Leave Management**: 
    *   Review, approve, or reject team leave requests (with necessary comments).
    *   View a consolidated team leave calendar and individual leave balances.
*   **Team Performance Review**:
    *   Review direct reports' performance documents.
    *   Provide detailed feedback and numerical ratings.
    *   Track team goals and progress.

### For Administrators:
*   Includes Manager capabilities, plus:
*   **Employee Administration**: Add, edit, manage, deactivate, and reactivate employee accounts.
*   **System Configuration**: 
    *   Manage company Departments and Designations.
    *   Post and manage company-wide Announcements.
    *   Maintain the global Holiday Calendar.
*   **Audit Logging**: View System Activity Logs for critical administrative actions.
*   **Reporting**: Global overview of system metrics (total employees, departments, leave types, pending requests).

## 🛠️ Technology Stack
*   **Backend**: Java 17+, Spring Boot
*   **Data Access**: Spring Data JPA, Hibernate
*   **Database**: Oracle (XE/PDB)
*   **Frontend**: HTML5, Thymeleaf, Vanilla CSS, Bootstrap 5, FontAwesome
*   **Build Tool**: Maven

## 📋 Running Locally
1. Ensure you have Java 17+ and Maven installed.
2. Initialize an Oracle database and connect the `application.properties` accordingly.
3. Build the application: `mvn clean install`
4. Run the application: `mvn spring-boot:run`

Access the web interface at `http://localhost:8080/`.

## 🗂️ Definitions of Done Met
*   [x] Working Web Application
*   [x] Code Repository Submission
*   [x] ERD (Entity Relationship Diagram)
*   [x] Application Architecture Diagram
*   [x] README.md
*   [x] Testing Artifacts

---
*Created as part of the P2 RevWorkForce Requirements.*
=======
# RevWorkforce – Human Resource Management System (HRMS)

## Project Overview

RevWorkforce is a full‑stack monolithic Human Resource Management System designed to manage employees, leave requests, and performance reviews inside an organization. The system provides role‑based access for Admin, Manager, and Employee users through separate dashboards.

The application helps streamline HR operations such as employee management, leave approval workflows, performance evaluation, and company announcements.

---

# Features

## Employee Features

* Secure login using email/employee ID and password
* View personal dashboard
* View and update profile information
* Apply for leave (Casual, Sick, Paid Leave)
* View leave history and status
* Cancel pending leave requests
* View company holiday calendar
* Submit performance reviews
* Create and track yearly goals
* View manager feedback
* Receive notifications

## Manager Features

Managers have access to all employee features plus:

* View team members (direct reportees)
* View and manage employee leave requests
* Approve leave with comments
* Reject leave with mandatory comments
* View team leave calendar
* View employee leave balance
* Review employee performance submissions
* Provide performance feedback and ratings
* View team goal progress

## Admin Features

Admins manage the entire system:

### Employee Management

* Add new employees
* Update employee details
* Deactivate employee accounts
* Reactivate employee accounts
* Assign reporting managers
* Search employees by name, department, designation

### Leave Management

* Create and manage leave types
* Assign leave quotas
* Adjust leave balances
* Generate leave reports
* Manage company holiday calendar

### System Configuration

* Manage departments
* Manage designations
* Create company announcements
* View system activity logs

---

# Technology Stack

## Frontend

* HTML5
* CSS3
* JavaScript
* Thymeleaf

## Backend

* Java
* Spring Boot
* Spring MVC

## Database

* Oracle Database

## Server

* Apache Tomcat

---

# Application Architecture

The application follows a layered architecture:

User Interface (Frontend)
→ Controller Layer
→ Service Layer
→ Mapper Layer
→ Repository Layer
→ Database (Oracle)

## Layer Responsibilities

Controller
Handles HTTP requests from the frontend.

Service
Contains business logic of the application.

Repository
Performs database operations.

Entity
Represents database tables.

DTO
Transfers data between frontend and backend.

Mapper
Converts Entity objects to DTO and DTO to Entity.

---

# Project Structure

com.rev.app

config – Application configuration classes

controller – Handles HTTP requests

dto – Data Transfer Objects

entity – Database entity classes

exception – Custom exception handling

mapper – Converts DTO ↔ Entity

repository – Database interaction layer

rest – REST API controllers

service – Business logic implementation

RevatureWorkForceP2Application – Main Spring Boot application

---

# Authentication

The system uses role‑based authentication.

User roles:

* Admin
* Manager
* Employee

After successful login, the user is redirected to the respective dashboard based on role.

---

# Database

Oracle database is used to store:

* Employee details
* Departments
* Designations
* Leave records
* Performance reviews
* Goals
* Notifications

---

# How to Run the Project

1. Clone the repository

2. Open the project in IDE (IntelliJ / Eclipse / STS)

3. Configure Oracle database connection in

application.properties

4. Run the main application:

RevatureWorkForceP2Application.java

5. Open browser and access:

[http://localhost:8080](http://localhost:8080)

---

# Future Enhancements

* JWT based authentication
* Email notifications
* Advanced reporting dashboard
* Mobile responsive UI improvements

---

# Author

Srinivas Mekala


>>>>>>> dev
