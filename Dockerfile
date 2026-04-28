FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS discovery-server
WORKDIR /app
COPY --from=build /app/discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS identity-service
WORKDIR /app
COPY --from=build /app/identity-service/target/identity-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS api-gateway
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 5: Post Service
FROM eclipse-temurin:21-jre-alpine AS post-service
WORKDIR /app
COPY --from=build /app/post-service/target/post-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
