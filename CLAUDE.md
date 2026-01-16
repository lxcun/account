# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Full-stack personal accounting application with Spring Boot backend and React frontend. Manages assets, liabilities, and financial transactions with automatic balance updates and comprehensive audit logging.

## Build & Development Commands

### Backend (Spring Boot)
```bash
# Build and run
mvn clean install
mvn spring-boot:run

# Run tests
mvn test

# Package for deployment
mvn clean package
java -jar target/accounting-app-1.0.0.jar
```

### Frontend (React + Vite)
```bash
cd frontend
npm install        # Install dependencies
npm run dev        # Development server (http://localhost:5173)
npm run build      # Production build (outputs to frontend/dist)
```

### Database Setup
```bash
mysql -u root -p
source src/main/resources/schema.sql
```

Configure database connection in `src/main/resources/application.yml`.

## Architecture

### Layered Architecture
```
Controller (REST API)
  ↓
Service (Business Logic)
  ↓
Mapper (MyBatis Data Access)
  ↓
MySQL Database
```

### Key Architectural Patterns

**MyBatis Configuration:**
- XML-based mapping files in `src/main/resources/mapper/`
- Separate interfaces (`Mapper.java`) and XML implementations (`Mapper.xml`)
- Automatic underscore-to-camelCase column conversion enabled
- Type aliases configured for `com.accounting.entity` package

**Authentication & Security:**
- JWT-based stateless authentication (24h expiration)
- Custom `CustomUserDetailsService` implements Spring Security's `UserDetailsService`
- BCrypt password encoding
- Role-based access control (RBAC) with `User`, `Role`, `UserRole` entities
- CORS enabled permissively for development
- Most API endpoints currently permitAll (auth disabled - see `SecurityConfig.java:41-54`)

**API Response Standardization:**
- All endpoints return `Result<T>` wrapper: `{code, message, data}`
- Success: `code: 200`, message: "success"
- Error: `code: 500`, custom message

**Operation Logging (AOP):**
- Custom annotation `@OperationLog` with `OperationType` and `OperationModule`
- AOP aspect in `OperationLogAspect.java` automatically captures:
  - User info from security context
  - Request/response data
  - IP addresses
  - Timestamps
  - Success/failure status

### Domain Model

**Three Core Entities:**

1. **Asset** - Financial assets (cash, bank, Alipay, WeChat, stocks, funds)
   - Types: `CASH`, `BANK`, `ALIPAY`, `WECHAT`, `STOCK`, `FUND`, `OTHER`
   - Tracks current balance

2. **Liability** - Debts (credit cards, mortgages, car loans, personal loans)
   - Types: `CREDIT_CARD`, `MORTGAGE`, `CAR_LOAN`, `PERSONAL_LOAN`, `OTHER`
   - Tracks outstanding balance

3. **TransactionRecord** - Income/expense records
   - Types: `INCOME`, `EXPENSE`
   - Foreign keys to `asset_id` and `liability_id`
   - `account_change_type` enum: `ASSET_INCREASE`, `ASSET_DECREASE`, `LIABILITY_INCREASE`, `LIABILITY_DECREASE`
   - **Critical**: Creating transactions automatically updates associated asset/liability balances

### Package Structure

```
com.accounting/
├── AccountingApplication.java    # Spring Boot entry point
├── controller/                    # REST endpoints
├── service/                       # Business logic layer
├── mapper/                        # MyBatis data access interfaces
├── entity/                        # JPA/MyBatis entities
├── dto/                           # Data transfer objects
├── common/                        # Result<T>, shared utilities
├── config/                        # Spring Security, OpenAPI, Web config
├── security/                      # JWT filter, CustomUserDetails
├── util/                          # JwtUtil, PasswordEncoderUtil
├── annotation/                    # @OperationLog, OperationType, OperationModule
└── aspect/                        # OperationLogAspect (AOP)
```

## Development Notes

### Code Conventions
- Lombok annotations reduce boilerplate (`@Data`, `@RequiredArgsConstructor`, etc.)
- MyBatis mappers require both interface (`Mapper.java`) and XML (`Mapper.xml`) files
- When modifying database queries, update both the interface method and corresponding XML

### Transaction Flow
When a transaction is created/updated:
1. `TransactionService` handles business logic
2. Asset/liability balances are automatically adjusted based on `account_change_type`
3. Both transaction record and affected account balances are updated atomically

### Security Configuration
- JWT secret and expiration in `application.yml`
- Public endpoints include auth, Swagger, and all API endpoints (currently permitAll)
- To add authentication to endpoints, modify `SecurityConfig.java:41-56`

### API Documentation
- Swagger UI available at: `http://localhost:8080/swagger-ui.html`
- OpenAPI 3 configuration in `OpenApiConfig.java`
- All DTOs include `@Schema` annotations for documentation

### Testing
- Test directory exists (`src/test/`) but is currently sparse (test files were deleted per git status)
- Use `mvn test` to run tests
- Spring Security Test dependency included for secured endpoint testing

### Configuration Locations
- Database: `src/main/resources/application.yml`
- MyBatis mappers: `src/main/resources/mapper/*.xml`
- Schema: `src/main/resources/schema.sql`
- Frontend API base: `frontend/src/api/` (Axios with interceptors)
