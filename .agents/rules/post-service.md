# Post Service Development Rules

This service handles posts, user interactions (news feed, likes, actions), media uploads, and friendships.

## 📁 Package Structure (`com.example.post`)
- **`controller`**: REST controllers exposing endpoints for `/api/posts/**`, `/api/friendships/**`, `/api/notifications/**`, `/api/actions/**`, and `/api/media/**`.
- **`service`**: Business logic implementations.
- **`client`**: gRPC client classes used to fetch data from `identity-service` (e.g. fetching author username/email for a post).
- **`config`**: Configuration files (e.g., Cloudinary config for media uploads).
- **`database.entity`**: JPA entities like `Post`, `Action`, `Friendship`, `NewsFeed`, `Notification`.

---

## 🔌 gRPC Client Configuration
- Post Service acts as a **gRPC Client** to fetch user profile info.
- It connects to `identity-service` via Eureka service discovery name: `discovery:///identity-service`.
- The connection stub is initialized in `UserClient` or similar class. Do not hardcode hostnames or ports.

---

## ☁️ Media Uploads (Cloudinary)
- Image/video media uploads are handled through Cloudinary API.
- Credentials must be configured using variables: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, and `CLOUDINARY_API_SECRET`.
- When dealing with media uploads, verify the size limits (configured up to 10MB in `application.yml`).
