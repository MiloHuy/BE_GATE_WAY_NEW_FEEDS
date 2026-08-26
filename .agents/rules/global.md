# Global Project Rules

This is a Spring Boot multi-module microservice backend system using gRPC for inter-service communication, Eureka for discovery, and API Gateway for routing and authentication.

## 📁 Repository Structure
- **[api-gateway](file:///Users/quanghuy/NEW_FEEDS_BE_PER/api-gateway)**: Handles public routing, CORS, and JWT authentication checks.
- **[discovery-server](file:///Users/quanghuy/NEW_FEEDS_BE_PER/discovery-server)**: Eureka registry.
- **[identity-service](file:///Users/quanghuy/NEW_FEEDS_BE_PER/identity-service)**: Handles user authentication, database persistence, and exposes a gRPC server for user retrieval.
- **[post-service](file:///Users/quanghuy/NEW_FEEDS_BE_PER/post-service)**: Handles posts and interactions, uses a gRPC client to query identity-service.
- **[proto-common](file:///Users/quanghuy/NEW_FEEDS_BE_PER/proto-common)**: Holds `.proto` definitions and generates Java gRPC classes.

---

## 🛠️ Build & Development Workflow
- **Dependency Build Order**: Any changes to `.proto` files in `proto-common` require a full rebuild and install of `proto-common` to local repository first:
  ```bash
  ./mvnw clean install -pl proto-common -am
  ```
- **Local Database Port**: MySQL is mapped to `3307` on host. Local applications must connect to `localhost:3307`. Docker containers connect to `mysql:3306`.
- **Local Redis Port**: Redis is mapped to `6379`.
- **Environment Variables**: Local runs require system environment variables. Inside Docker containers, environment variables are loaded via `.env` files in each service subdirectory.
