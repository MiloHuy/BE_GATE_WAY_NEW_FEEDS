FROM maven:3.9.7-eclipse-temurin-21 AS deps
WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd ./

COPY api-gateway/pom.xml          api-gateway/pom.xml
COPY discovery-server/pom.xml     discovery-server/pom.xml
COPY identity-service/pom.xml     identity-service/pom.xml
COPY post-service/pom.xml         post-service/pom.xml
COPY proto-common/pom.xml         proto-common/pom.xml

RUN mvn dependency:go-offline -q --no-transfer-progress

FROM deps AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine AS discovery-server
WORKDIR /app
COPY --from=build /app/discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS identity-service
WORKDIR /app
COPY --from=build /app/identity-service/target/identity-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
EXPOSE 9090
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS api-gateway
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS post-service
WORKDIR /app
COPY --from=build /app/post-service/target/post-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
