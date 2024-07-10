# Use Maven image as builder
FROM maven:3.8.6-openjdk-17 AS build

# Set working directory
WORKDIR /build

# Copy Maven dependencies and build application
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Use OpenJDK 17-alpine as runtime image
FROM openjdk:17-alpine

# Set working directory in runtime image
WORKDIR /app

# Copy JAR file from build stage to runtime image
COPY --from=build /build/target/HNG2New-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port where your Spring Boot application listens
EXPOSE 8081

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
