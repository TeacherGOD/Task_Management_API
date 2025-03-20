
FROM openjdk:21-jdk
COPY .env /app/.env
WORKDIR /app
COPY target/TaskManagement-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]