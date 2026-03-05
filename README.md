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


