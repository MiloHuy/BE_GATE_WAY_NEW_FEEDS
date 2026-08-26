# API Gateway Development Rules

The API Gateway is the single entry point for all client requests. It manages security routing, JWT authentication, and CORS.

## 📁 Package Structure (`com.example.src`)
- **`security`**: Security configurations and filters.
  - `SecurityConfig`: Configures JWT resource server validation.
  - `AuthenticationFilter`: Spring Cloud Gateway filter handling authentication logic or path validation.
- **`config`**: `CorsConfig` and `RedisConfig`.
- **`services`**: Contains `RedisService` to handle blacklisted tokens, session caching, etc.
- **`controller`**: Health check or testing endpoints.

---

## 🚦 Routing Rules (`application.yml`)
When adding a new route, make sure to add it under `spring.cloud.gateway.routes` in the main configuration file:
- **`identity-service`**: Routes `/api/auth/**`, `/api/admin/**`, `/api/users/**`
- **`post-service`**: Routes `/api/posts/**`, `/api/friendships/**`, `/api/notifications/**`, `/api/actions/**`, `/api/media/**`

---

## 🔒 Security
- All incoming requests (except public endpoints like auth register/login) are validated against JWT using the shared `JWT_SECRET` key via Spring Security OAuth2 resource server.
- Redis is utilized on host port `6379` (inside container: host `redis`) for token validations or rate-limiting if implemented.
