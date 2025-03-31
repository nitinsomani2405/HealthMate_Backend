FROM openjdk:23
WORKDIR /app
ARG JAR_FILE
COPY target/HealthMate_Backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
