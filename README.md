# Hướng Dẫn Khởi Chạy Hệ Thống Microservices (Spring Boot)

Dự án này là một hệ thống microservices được viết bằng **Spring Boot (Java 21)** và sử dụng **gRPC** để giao tiếp giữa các service.

## 🛠️ Thành phần hệ thống

1. **discovery-server**: Eureka Server để đăng ký và phát hiện dịch vụ (cổng `8761`).
2. **api-gateway**: API Gateway quản lý routing và phân quyền bằng JWT (cổng `8080`).
3. **identity-service**: Quản lý tài khoản, phân quyền, authentication, tích hợp gRPC server (cổng REST `8081`, cổng gRPC `9090`).
4. **post-service**: Quản lý bài đăng, tương tác, tích hợp Cloudinary (cổng `8082`).
5. **proto-common**: Thư viện dùng chung chứa định nghĩa gRPC (.proto) và các class sinh ra tự động.
6. **mysql**: Cơ sở dữ liệu cho các service (cổng `3307` trên host).
7. **redis**: Bộ nhớ đệm cache (cổng `6379` trên host).

---

## 📋 Yêu cầu hệ thống

Trước khi chạy hệ thống, hãy đảm bảo máy tính đã cài đặt:
- **Java 21**
- **Docker** và **Docker Compose**
- **Maven** (hoặc sử dụng wrapper `./mvnw` có sẵn trong dự án)

---

## 🔑 Cấu hình file môi trường (`.env`)

Dự án sử dụng các file `.env` để cấu hình biến môi trường khi chạy bằng Docker. Các file này đã được bỏ qua trong `.gitignore` để bảo mật.

1. **`identity-service/.env`**: Đã cấu hình sẵn kết nối Database và khóa bảo mật JWT.
2. **`api-gateway/.env`**: Đã cấu hình sẵn kết nối Redis và khóa bảo mật JWT (khóa JWT phải khớp với `identity-service`).
3. **`post-service/.env`**: Hãy tạo file này từ template `post-service/.env.example` và điền tài khoản Cloudinary của bạn (nếu có, hoặc giữ nguyên mock nếu chưa dùng đến):
   ```env
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_API_KEY=your_api_key
   CLOUDINARY_API_SECRET=your_api_secret
   ```

---

## 🚀 Hướng Dẫn Khởi Chạy

### Cách 1: Chạy toàn bộ hệ thống bằng Docker Compose (Khuyên dùng)

Đây là cách nhanh nhất và đơn giản nhất vì Docker Compose tự động xây dựng mã nguồn và chạy các dịch vụ theo đúng thứ tự.

1. **Khởi chạy hệ thống**:
   Mở terminal tại thư mục gốc của dự án và chạy lệnh sau:
   ```bash
   docker compose up --build
   ```
   *Lệnh này sẽ tải các base image, compile code bằng Maven (Multi-stage build) và khởi chạy toàn bộ 6 container.*

2. **Kiểm tra trạng thái các service**:
   Mở trình duyệt truy cập vào Eureka Dashboard:
   👉 **[http://localhost:8761](http://localhost:8761)**
   
   Khi tất cả các service hiển thị trạng thái `UP` trên Eureka Dashboard, hệ thống đã sẵn sàng hoạt động.

3. **Dừng hệ thống**:
   ```bash
   docker compose down
   ```

---

### Cách 2: Chạy trực tiếp trên máy cục bộ (Dành cho Lập trình viên / Debug)

Nếu bạn muốn chạy hoặc debug code trực tiếp bằng IDE (như IntelliJ IDEA, Eclipse, VS Code):

#### Bước 1: Khởi chạy MySQL và Redis bằng Docker
Chỉ chạy hai container chứa cơ sở dữ liệu và cache:
```bash
docker compose up -d mysql redis
```

#### Bước 2: Compile & Install gRPC module (`proto-common`)
Vì các microservices giao tiếp qua gRPC, bạn cần sinh ra các class Java từ file `.proto` trước:
```bash
./mvnw clean install -pl proto-common -am
```
*(Nếu dùng Windows, thay thế `./mvnw` bằng `mvnw.cmd`)*

#### Bước 3: Cấu hình biến môi trường cục bộ
Nếu chạy trực tiếp, các service sẽ đọc cấu hình từ phần default trong `application.yml`:
- Đối với **`identity-service`**, bạn cần thiết lập các biến môi trường sau cho IDE hoặc terminal khi chạy:
  - `DB_USER=root`
  - `DB_PWD=leovmika68`
  - `DB_EXTERNAL_URL=jdbc:mysql://localhost:3307/identity_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
  - `JWT_SECRET=2a0e929f81eb06a0b9eba5a39e24e91966608eee54bcb9ab`
  - `JWT_EXPIRATION=86400000`
  - `JWT_REFRESH_EXPIRATION=604800000`
- Đối với **`post-service`**, thiết lập các biến Cloudinary (nếu cần):
  - `CLOUDINARY_CLOUD_NAME=...`
  - `CLOUDINARY_API_KEY=...`
  - `CLOUDINARY_API_SECRET=...`
- Đối với **`api-gateway`**, thiết lập:
  - `PORT=8080`
  - `JWT_SECRET=2a0e929f81eb06a0b9eba5a39e24e91966608eee54bcb9ab`

#### Bước 4: Khởi chạy các service theo đúng thứ tự
Mở các tab terminal riêng biệt và chạy các lệnh dưới đây (hoặc chạy trực tiếp nút Run/Debug trên IDE):

1. **Khởi chạy Discovery Server**:
   ```bash
   ./mvnw spring-boot:run -pl discovery-server
   ```
2. **Khởi chạy Identity Service**:
   ```bash
   ./mvnw spring-boot:run -pl identity-service
   ```
3. **Khởi chạy Post Service**:
   ```bash
   ./mvnw spring-boot:run -pl post-service
   ```
4. **Khởi chạy API Gateway**:
   ```bash
   ./mvnw spring-boot:run -pl api-gateway
   ```

---

## 🔗 Danh Sách Các Cổng Kết Nối (Ports)

| Dịch vụ | Cổng trên Host | URL / Chi tiết |
| :--- | :--- | :--- |
| **API Gateway** | `8080` | `http://localhost:8080` (Điểm đầu nhận mọi request API) |
| **Discovery Server** | `8761` | `http://localhost:8761` (Eureka Dashboard UI) |
| **Identity Service** | `8081` / `9090` | REST API / gRPC Server |
| **Post Service** | `8082` | REST API |
| **MySQL Database** | `3307` | Kết nối qua database client: `localhost:3307` |
| **Redis Cache** | `6379` | Kết nối qua redis client: `localhost:6379` |
