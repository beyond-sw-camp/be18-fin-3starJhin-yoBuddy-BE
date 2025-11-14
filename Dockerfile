FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY .env /app/.env

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
