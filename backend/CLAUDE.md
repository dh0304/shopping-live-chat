# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot REST API project for a shopping live service. The project uses:
- Java 21 with Spring Boot 3.5.4
- Gradle build system
- Lombok for boilerplate reduction
- JUnit 5 for testing

## Build and Development Commands

### Building the project
```bash
./gradlew build
```

### Running the application
```bash
./gradlew bootRun
```

### Running tests
```bash
./gradlew test
```

### Running a single test
```bash
./gradlew test --tests "com.shoppinglive.api.ShoppingLiveChatApplicationTests"
```

### Clean build
```bash
./gradlew clean build
```

## Project Structure

- **Main application**: `src/main/java/com/shoppinglive/api/ApiApplication.java`
- **Package structure**: All code under `com.shoppinglive.api` package
- **Resources**: Configuration in `src/main/resources/application.properties`
- **Tests**: Located in `src/test/java/com/shoppinglive/api/`

The project follows standard Spring Boot conventions with a single main application class and standard Maven/Gradle directory structure.

## Key Technologies

- **Spring Boot**: Web framework with auto-configuration
- **Spring Web**: For REST API endpoints
- **Lombok**: For reducing boilerplate code (getters, setters, constructors)
- **JUnit Platform**: Testing framework

## Application Properties

The application name is configured as "api" in `application.properties`. Default Spring Boot configuration applies for most settings.