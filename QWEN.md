# SafePoint Application Context

## Project Overview
SafePoint is a full-stack web application built with Spring Boot that provides a platform for students to report bullying incidents and schedule one-on-one counseling sessions. The application is built using Java 17, Spring Boot 3.x, Thymeleaf, Hibernate/JPA, and H2 database.

Key technologies used:
- **Backend:** Spring Boot 3.x, Java 17
- **Frontend:** Thymeleaf templating engine, Bootstrap CSS
- **Database:** H2 (file-based for local development)
- **ORM:** Hibernate JPA
- **Email:** Spring Boot Mail starter for notifications
- **Build:** Apache Maven

## Project Structure
```
src/
├── main/
│   ├── java/com/keiaa/voiz/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # Spring MVC controllers
│   │   ├── exception/        # Custom exceptions
│   │   ├── model/            # Entity models and DTOs
│   │   ├── repository/       # JPA repositories
│   │   ├── service/          # Business logic services
│   │   └── VoizApplication.java # Main application class
│   ├── resources/
│   │   ├── emails/          # Email templates
│   │   ├── static/          # Static assets (CSS, JS, images)
│   │   ├── templates/       # Thymeleaf templates
│   │   └── application.properties # Configuration properties
│   └── test/                # Unit and integration tests
```

## Key Features

### Reporting System
- Students can submit bullying incident reports with:
  - Name and email (optional)
  - Category and detailed description
  - Evidence files (up to 10MB each)
  - External links for additional context
- Each report is assigned a unique ID for tracking
- Reports are stored in the database with status tracking
- Report confirmation emails with attachments are sent to submitters

### Counseling Appointment System
- Students can schedule one-on-one counseling sessions
- Appointment requests include name, email, preferred date/time, and reason
- Appointment status tracking (pending, confirmed, completed)
- Automated email notifications for appointment confirmations, rescheduling, and completion

### Administrative Dashboard
- Admin interface for managing reports and appointments
- Report review and status updates
- Appointment management and scheduling
- Authentication via admin key property

### File Management
- File upload/download functionality for evidence
- Secure file storage with path protection
- Attachment handling for email notifications

## Data Models

### Report
- ID (auto-generated)
- Unique report ID (generated daily with limit)
- User information (name, email)
- Report details (category, description)
- Evidence files (file paths array)
- External link
- Timestamp and status tracking
- History tracking through ReportHistory

### Appointment
- ID (auto-generated)
- User information (name, email)
- Preferred date/time
- Reason for appointment
- Status tracking (pending, confirmed, completed)

## Configuration

### Required Local Configuration
The application requires a `src/main/resources/application-dev.properties` file for local development with sensitive configuration values (email credentials, admin key). This file is excluded from version control.

Sample content:
```properties
# Email properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Custom properties
admin.key=your-admin-key
```

### Database
- Uses H2 file-based database stored at `./data/voizdb`
- Connection URL: `jdbc:h2:file:./data/voizdb`
- Automatically creates and updates schema (`spring.jpa.hibernate.ddl-auto=update`)

### Server
- Runs on port 9090
- File upload limit: 10MB per file, 10MB per request

## Building and Running

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6.0 or higher

### Build Commands
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run from IDE by executing VoizApplication.java as Spring Boot app
```

### Accessing the Application
- Application: http://localhost:9090
- H2 Console: http://localhost:9090/h2-console
- Admin Dashboard: http://localhost:9090/admin

## Development Conventions

### Code Style
- Uses Lombok for reducing boilerplate code
- Standard Java naming conventions
- Spring Boot best practices for dependency injection
- RESTful URL patterns for API endpoints

### Testing
- Uses JUnit 5 for unit testing
- Spring Boot Test for integration testing
- Test classes follow the pattern `*Tests.java`

### Security
- Input validation through Spring's built-in validation
- File upload restrictions (size limits)
- Admin authentication through properties
- H2 console disabled in production

### Error Handling
- Custom exceptions for specific scenarios
- User-friendly error messages displayed to end users
- Proper logging for debugging

## Key Services

### EmailService
Handles all email communications including:
- Report confirmations with attachments
- Appointment confirmations, rescheduling, and completion notifications
- Template-based HTML emails with dynamic content

### FileStorageService
Manages file uploads and downloads:
- Secure file storage and retrieval
- Path protection to prevent directory traversal
- Integration with email service for attachment handling

### ReportIdGenerator
Generates unique daily report IDs with daily limits to prevent spam

## Controllers

### ReportController
Manages report submission and tracking functionality:
- Report form display and submission
- File uploads and evidence handling
- Report tracking by unique ID
- File download endpoints

### AppointmentController
Handles counseling appointment requests:
- Appointment form display and submission
- Email confirmations
- Appointment management in admin dashboard

### AdminController
Provides administrative functionality:
- Report and appointment management
- Status updates and tracking
- Authentication protection
- Reporting and analytics

## Testing Strategy
- Basic context loading test implemented
- Integration tests for controllers and services likely needed
- Unit tests for service layer business logic
- Manual testing for UI components and workflows