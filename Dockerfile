FROM eclipse-temurin:21


WORKDIR /app

# Copy the JAR file to the container
COPY target/todolist-api-0.0.1-SNAPSHOT.jar /app/todolist-api-0.0.1-SNAPSHOT.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/todolist-api-0.0.1-SNAPSHOT.jar"]