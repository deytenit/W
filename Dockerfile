# Use an official Maven image to build the application
FROM maven:3.9-eclipse-temurin-23-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the source code into the container
COPY . /app

# Run Maven to package the application (this will generate a JAR file in the target/ folder)
RUN mvn clean package -DskipTests

# Use a lightweight JDK image to run the app
FROM eclipse-temurin:23-alpine

# Set the working directory for the final image
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar /app/w-server.jar

# Expose port of Spring Boot app
EXPOSE 8090

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "w-server.jar"]
