# SafePoint

<p align="center">
    <img alt = "Spring Framework" src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"/>
    <img alt = "Bootstrap" src="https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white"/>
    <img alt = "Thymeleaf" src="https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white"/>
    <img alt = "Hibernate ORM" src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"/>
    <img alt = "Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"/>
</p>

![SafePoint app screenshot](screenshot.png)

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

This project requires a file named `src/main/resources/application-dev.properties` for local development. This file contains sensitive configuration (such as email credentials) and is excluded from version control. 

The [EmailService](src/main/java/com/keiaa/voiz/service/EmailService.java) uses mail-related properties to send a copy of a report's unique reference ID. The `admin.key` is used for authenticating users on the administrative website.

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

## License

This repository is licensed under Mozilla Public License 2.0. Please refer to the [LICENSE](LICENSE) file for full details. Other resources included, such as images, are licensed under [Creative Commons Attribution Non-Commercial Share-Alike 4.0 International](https://creativecommons.org/licenses/by-nc-sa/4.0/).