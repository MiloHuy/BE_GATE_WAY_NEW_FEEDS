# Identity Service Development Rules

This service handles user credentials, registrations, authentication, profile updates, and exposes user information to other microservices via gRPC.

## 📁 Package Structure (`com.example.identity`)
- **`controller`**: REST controllers exposing endpoints for `/api/auth/**` (auth request, refresh, register) and `/api/users/**` (profile, details).
- **`service`**: Business logic. Exposes interfaces and implementation classes. Uses `@Service`.
- **`database.entity`**: JPA database entity classes mapping to tables.
- **`database.repository`**: Interfaces extending `JpaRepository`.
- **`grpc`**: gRPC service implementation class (`UserServiceImpl`) extending `UserServiceGrpc.UserServiceImplBase`.
- **`dto`**: Data transfer objects separating API request/response format from database entities.
- **`security`**: Security configurations (Spring Security, JWT Provider filter, etc.).
- **`exception`**: Custom exception handlers (`GlobalExceptionHandler`, `AppException`).

---

## 🔒 Security & JWT
- JWT keys, expiration times, and database connections are loaded using system environment variables, matching `identity-service/.env`.
- Any authentication logic or credentials verification must run through `AuthService` and `JwtProvider`.

---

## 🔌 gRPC Server Configuration
- The service acts as a **gRPC Server** running on port `9090` (configured in `application.yml`).
- If you add or modify RPC methods, you **must**:
  1. Modify `proto-common/src/main/proto/user.proto`.
  2. Run `./mvnw clean install -pl proto-common -am` to generate the code classes.
  3. Implement the interface/logic inside `com.example.identity.grpc.UserServiceImpl`.
