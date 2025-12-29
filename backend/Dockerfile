# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY backend/build.gradle backend/
COPY frontend/build.gradle frontend/
COPY settings.gradle .
# Pre-fetch dependencies
RUN ./gradlew :backend:dependencies || true

COPY backend backend
COPY spec spec
RUN ./gradlew :backend:build -x test

# Run stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
