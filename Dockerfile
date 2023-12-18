# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY build/libs/accountbalanceservice-0.0.1-SNAPSHOT.jar /app/accountbalanceservice-0.0.1-SNAPSHOT.jar

# Expose the port that your application will run on
EXPOSE 8080

# Specify the command to run your application
CMD ["java", "-jar", "accountbalanceservice-0.0.1-SNAPSHOT.jar"]
