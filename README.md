# Student and Fee Microservices Project

This project implements two Spring Boot microservices: **Student Service** and **Fee Service**, for managing students, collecting fees, and viewing receipts. It follows REST API best practices, includes failure handling, inter-service communication, unit tests, and OpenAPI documentation.

## Project Structure
- `student-service/`: Manages student records (port 8081).
- `fee-service/`: Handles fee collection and receipts (port 8082).
- `postman/`: Postman collections for API testing.
- `openapi/`: OpenAPI YAML specifications.

## Features
- **REST APIs**: CRUD for students, fee collection, receipt viewing.
- **Resilience**: Retry, circuit breaker, validation, and custom error handling.
- **Inter-service Communication**: Fee Service queries Student Service via RestTemplate.
- **Unit Tests**: JUnit 5 and Mockito for controllers and services.
- **Documentation**: OpenAPI (Swagger) and Postman collections.
- **Best Practices**: Proper resource naming, HTTP methods, status codes, HATEOAS.

## Prerequisites
- OpenJDK 17
- Maven 3.8.1+
- Postman (for testing)
- IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)

## Setup Instructions
1. **Verify OpenJDK 17**:
   - Run `java -version` (e.g., `openjdk 17.0.12 2024-07-16`).
   - Set `JAVA_HOME`: `export JAVA_HOME=/path/to/jdk-17`.
   - Verify Maven: `mvn -version` (should use JDK 17).

2. **Build and Run Student Service**:
   - Navigate to `student-service/`.
   - Run `mvn clean install`.
   - Start: `mvn spring-boot:run`.
   - Swagger UI: `http://localhost:8081/swagger-ui.html`.
   - Import `postman/StudentService.postman_collection.json`.

3. **Build and Run Fee Service**:
   - Navigate to `fee-service/`.
   - Run `mvn clean install`.
   - Start: `mvn spring-boot:run`.
   - Swagger UI: `http://localhost:8082/swagger-ui.html`.
   - Import `postman/FeeService.postman_collection.json`.

## APIs
### Student Service (http://localhost:8081/api)
- `POST /students`: Create student (201 Created).
- `GET /students`: List students (200 OK).
- `GET /students/{id}`: Get student (200 OK, 404 Not Found).
- `PUT /students/{id}`: Update student (200 OK, 404 Not Found).
- `DELETE /students/{id}`: Delete student (204 No Content, 404 Not Found).

### Fee Service (http://localhost:8082/api)
- `POST /fees`: Collect fee (201 Created, 400 Bad Request).
- `GET /fees`: List fees (200 OK).
- `GET /fees/{id}`: Get fee (200 OK, 404 Not Found).
- `GET /fees/student/{studentId}`: List fees for student (200 OK, 404 Not Found).
- `GET /receipts/{feeId}`: View receipt (200 OK, 404 Not Found).

## Resilience
- **Validation**: Bean Validation for request bodies.
- **Retry**: Resilience4j retries for inter-service calls (3 attempts).
- **Circuit Breaker**: Prevents cascading failures in Fee Service.
- **Error Handling**: Standardized JSON error responses.

## Inter-service Communication
- Fee Service calls Student Service (`GET /api/students/{id}`) to validate `studentId` and fetch details for receipts.

## Unit Tests
- Run: `mvn test` in `student-service/` or `fee-service/`.
- Coverage: Controller and service layers using JUnit 5 and Mockito.

## Troubleshooting
- **"Cannot resolve symbol 'hateoas'"**:
  - Verify `spring-boot-starter-hateoas` in `pom.xml`.
  - Run `mvn clean install -U`.
- **"Cannot resolve symbol 'ServiceUnavailableException'"**:
  - Verify `ServiceUnavailableException.java` in `fee-service/src/main/java/com/example/feeservice/exception`.
  - Ensure `FeeService.java` uses correct import.
- **"Cannot find symbol"**:
  - Verify `Student.java` has `setStudentId(Long)` (~line 20).
  - Clear cache: `rm -rf ~/.m2/repository`.
  - Run: `mvn clean install -U`.
  - Debug: `mvn clean compile -X > build.log` and share `build.log`.
- **Inter-service Failure**:
  - Ensure Student Service is running before Fee Service.
  - Check logs for Resilience4j retry/circuit breaker events.
- **Java Version Mismatch**:
  - Confirm JDK 17: `java -version`.
  - Update IDE: IntelliJ (`File > Project Structure > SDK: 17`), Eclipse (`Project > Properties > Java Compiler > 17`).
- **Dependencies**:
  - Resolve conflicts: `mvn dependency:tree`.

## Swagger Documentation
- Student Service: `http://localhost:8081/swagger-ui.html`
- Fee Service: `http://localhost:8082/swagger-ui.html`

## Postman
- Import `postman/StudentService.postman_collection.json` and `postman/FeeService.postman_collection.json`.
