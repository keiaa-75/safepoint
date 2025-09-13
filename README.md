# SafePoint

<p align="center">
    <img alt = "Spring Framework" src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"/>
    <img alt = "Bootstrap" src="https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white"/>
    <img alt = "Thymeleaf" src="https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white"/>
    <img alt = "Hibernate ORM" src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"/>
    <img alt = "Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"/>
</p>

SafePoint is a simple, full-stack web application to provide a platform for students to report bullying incidents and schedule one-on-one counseling sessions.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6.0 or higher

**1. Clone the repository:**

```sh
git clone https://github.com/keiaa-75/voiz.git
cd voiz
```

**2. Build the project:**

```sh
mvn clean install
```

**3. Run the application:**

From your IDE, you can run the `VoizApplication.java` file as a Spring Boot app. Alternatively, you can use the command line:

```sh
mvn spring-boot:run
```

The application will start on port `9090`.


## Development Properties

This project requires a file named `src/main/resources/application-dev.properties` for local development. This file contains sensitive configuration (such as email credentials) and is excluded from version control (`.gitignore`).

**You must create this file before running the app.**

### Sample `application-dev.properties`

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

**Note:**
- Use an [App Password](https://support.google.com/accounts/answer/185833) for Gmail if 2FA is enabled.
- Never commit real credentials to version control.
- The `admin.key` is used for admin authentication.

## Usage

1. **Access the application:** Open your web browser and navigate to `http://localhost:9090`.
2. **Submit a report:** Fill out the report form and click "Submit Report." A confirmation modal will appear, and the data will be saved to the H2 Database.
3. **Schedule a session:** Navigate to the counseling scheduler page at `http://localhost:9090/schedule`. Fill out the form to request a session.
4. **View the database:** You can view the stored reports and appointments by navigating to the H2 console at `http://localhost:9090/h2-console`. Use the JDBC URL configured in the `application.properties` file to connect.
