# ISO 20022 REST API

A Spring Boot application that implements REST APIs for ISO 20022 message processing with PostgreSQL database support.

## Features

- ISO 20022 message type management
- Financial message processing
- XML payload validation
- Message status tracking
- PostgreSQL persistence
- Flyway database migrations

## Technology Stack

- Kotlin 1.9.25
- Spring Boot 3.3.5
- PostgreSQL
- Flyway
- JUnit 5
- Testcontainers

## Prerequisites

- JDK 21
- PostgreSQL 14+
- Docker (for running tests)

## Setup

1. Clone the repository
2. Configure database connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/iso20022db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## Database Migration

The application uses Flyway for database migrations. Migrations are automatically applied on startup.

## API Documentation

### Message Types

#### List all message types
```http
GET /api/v1/message-types
```

#### Get message type by ID
```http
GET /api/v1/message-types/{id}
```

#### Get message type by code
```http
GET /api/v1/message-types/by-code/{typeCode}
```

#### Create message type
```http
POST /api/v1/message-types
Content-Type: application/json

{
    "typeCode": "PACS.008.001.08",
    "description": "FIToFICustomerCreditTransfer",
    "schemaVersion": "8.0"
}
```

#### Update message type
```http
PUT /api/v1/message-types/{id}
Content-Type: application/json

{
    "typeCode": "PACS.008.001.08",
    "description": "Updated description",
    "schemaVersion": "8.1"
}
```

#### Validate message type code
```http
GET /api/v1/message-types/validate/{typeCode}
```

### Financial Messages

#### Create message
```http
POST /api/v1/messages
Content-Type: application/json

{
    "messageTypeId": 1,
    "businessMessageIdentifier": "BMI-123",
    "messageDefinitionIdentifier": "pacs.008.001.08",
    "payload": "<?xml version=\"1.0\"?><Document>...</Document>"
}
```

#### Get message by ID
```http
GET /api/v1/messages/{id}
```

#### Get message by business identifier
```http
GET /api/v1/messages/by-bmi/{bmi}
```

#### Get messages by status
```http
GET /api/v1/messages/by-status/{status}
```

#### Process message
```http
POST /api/v1/messages/{id}/process
```

#### Update message status
```http
PATCH /api/v1/messages/{id}/status
Content-Type: application/json

{
    "status": "PROCESSING",
    "errorDescription": null
}
```

## Message Statuses

- `RECEIVED` - Initial status when message is created
- `VALIDATED` - Message passed XML validation
- `PROCESSING` - Message is being processed
- `COMPLETED` - Processing completed successfully
- `ERROR` - Error occurred during processing

## Testing

Run tests with:
```bash
./gradlew test
```

The tests use Testcontainers to spin up a PostgreSQL instance automatically.

## Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/example/iso/
│   │       ├── controller/
│   │       ├── domain/
│   │       ├── dto/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── db/migration/
│       └── application.properties
└── test/
    ├── kotlin/
    │   └── com/example/iso/
    │       ├── controller/
    │       ├── repository/
    │       └── config/
    └── resources/
        └── application-test.properties
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
