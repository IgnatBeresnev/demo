# Spring Starter Demo Application

## Project Overview
This is a minimal Spring Boot starter application template built with Kotlin. It provides a foundation for building Spring-based applications with modern tools and practices.

## Technology Stack
- **Language**: Kotlin 1.9.25
- **Platform**: Java 21
- **Framework**: Spring Boot 3.3.5
- **Build Tool**: Gradle (with Kotlin DSL)
- **Testing Framework**: JUnit 5

## Project Structure
```
spring-starter-demo-app/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/example/iso/
│   │   │       └── Main.kt
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── build.gradle.kts
├── settings.gradle.kts
└── .junie/
    └── guidelines.md
```

## Setup Instructions
1. Ensure you have JDK 21 installed
2. Clone the repository
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
4. Run tests:
   ```bash
   ./gradlew test
   ```

## Development Guidelines
### Code Style
- Follow Kotlin coding conventions
- Use meaningful names for classes, functions, and variables
- Write unit tests for new functionality

### Git Workflow
- Create feature branches from main
- Write descriptive commit messages
- Submit pull requests for code review

### Dependencies
The project includes:
- spring-boot-starter: Core Spring Boot functionality
- kotlin-reflect: Kotlin reflection library
- spring-boot-starter-test: Testing framework
- Optional (commented out):
  - spring-boot-starter-web: For web applications
  - spring-boot-starter-data-jpa: For database operations

## Configuration
- Application properties are stored in `src/main/resources/application.properties`
- The application name is set to "demo"

## Building and Testing
- Build: `./gradlew build`
- Test: `./gradlew test`
- Clean: `./gradlew clean`

## Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
