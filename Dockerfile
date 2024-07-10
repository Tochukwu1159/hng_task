FROM openjdk:17-jdk-alpine AS builder

# Set working directory for the builder stage
WORKDIR /app

# Copy the project directory from the context to the working directory
COPY . .

# Use a multi-stage build to create a slimmer image
FROM openjdk:17-alpine AS runner

# Set working directory for the runner stage
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /target/*.jar app.jar


# Expose the port where your Spring Boot application listens (replace 8080 with your actual port)
EXPOSE 8081

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]