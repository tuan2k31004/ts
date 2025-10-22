FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy parent pom
COPY ../pom.xml ../pom.xml

# Copy common module
COPY ../common ../common

# Copy project-service
COPY . .

# Build common first, then project-service
RUN mvn -B -DskipTests clean install -pl ../common
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
