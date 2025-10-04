# Login Application

This is a simple Login Spring Boot application using JWT-based authentication.

## Configuration

The application can be configured via the `src/main/resources/application.properties` file.

### Dynamic Frontend URLs

The "Register New User" and "Forgotten Password" buttons on the frontend can be configured to point to specific URLs. This is handled by the `/auth/config` endpoint, which reads properties from the `application.properties` file.
-   `register.url`: The URL for the user registration page.
    -   **Default:** `register` (becomes a relative path: `/register`)
    -   **Example:** `register.url=https://my-app.com/signup`

-   `forgotten.url`: The URL for the password reset page.
    -   **Default:** `forgotten` (becomes a relative path: `/forgotten`)
    -   **Example:** `forgotten.url=https://my-app.com/reset-password`

If these properties are not set, the application will use the default relative paths. If they are set to full URLs, the frontend will open those URLs in a new tab.




## Project Structure

```
src
├── main
│   ├── java
│   │   └── uk
│   │       └── org
│   │           └── kennah
│   │               └── login
│   │                   ├── controller
│   │                   │   └── AuthController.java
│   │                   ├── model
│   │                   │   ├── JwtResponse.java
│   │                   │   ├── LoginRequest.java
│   │                   │   └── User.java
│   │                   ├── repository
│   │                   │   └── UserRepo.java
│   │                   ├── security
│   │                   │   ├── JwtUtil.java
│   │                   │   └── SecurityConfig.java
│   │                   └── LoginApplication.java
│   └── resources
│       ├── static
│       │   └── index.html
│       └── application.properties
└── test
```

## How to Build and Run

### Prerequisites
*   Java 17+
*   Maven 3.6+

### Running the application

You can run the application directly using the Maven Spring Boot plugin:
```sh
mvn spring-boot:run
```
The application will start and be accessible at `http://localhost:8080`.

### Building the application
To build the project and create an executable JAR file, run:
```sh
mvn clean package
```
The resulting JAR file will be located in the `target` directory.
