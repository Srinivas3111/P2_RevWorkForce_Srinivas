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
