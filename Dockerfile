# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the correct JAR file from target/ folder
COPY target/weather-prediction-service-0.0.1-SNAPSHOT.jar weather-app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "weather-app.jar"]
