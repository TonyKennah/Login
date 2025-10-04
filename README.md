# Login Application

This is a simple Spring Boot application demonstrating JWT-based authentication.

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
