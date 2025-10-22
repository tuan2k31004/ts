# Multi-stage build for Java services
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and common module first
COPY pom.xml .
COPY common ./common

# Build common module
RUN mvn -B -DskipTests clean install -pl common

# Copy service-specific code
ARG SERVICE_NAME
COPY ${SERVICE_NAME} ./${SERVICE_NAME}

# Build the specific service
RUN mvn -B -DskipTests clean package -pl ${SERVICE_NAME}

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
