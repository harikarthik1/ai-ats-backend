# Resume Analyzer Backend

This is the backend service for the AI Resume Analyzer application, built with Spring Boot. It provides REST APIs for resume analysis, user authentication, and data management.

## Features

- Resume upload and analysis using AI
- User authentication and authorization
- Analysis history tracking
- RESTful API endpoints

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional, for containerized deployment)

## Setup

1. Clone the repository:
   ```
   git clone <repository-url>
   cd resume-analyzer
   ```

2. Configure the application:
   - Update `src/main/resources/application.properties` with your database and AI service configurations.

3. Build the project:
   ```
   mvn clean compile
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`.

## API Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/resume/upload` - Upload resume for analysis
- `GET /api/analysis/history` - Get analysis history
- `POST /api/analysis/analyze` - Perform AI analysis on resume

## Testing

Run tests with:
```
mvn test
```

## Deployment

To build a JAR file:
```
mvn clean package
```

Run the JAR:
```
java -jar target/resume-analyzer-0.0.1-SNAPSHOT.jar
```

For Docker deployment, use the provided Dockerfile.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Submit a pull request

## License

[Specify license if applicable]