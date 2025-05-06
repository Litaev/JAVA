# Stage 1: Build the application with Maven
FROM maven:3.8.7-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
# Сначала копируем только POM, чтобы использовать кеш слоев при неизменных зависимостях
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
RUN mkdir -p logs

ENV JAVA_OPTS="-Xmx256m -Xms128m"

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]