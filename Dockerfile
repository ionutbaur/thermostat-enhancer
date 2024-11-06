# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS build

LABEL org.opencontainers.image.authors="Ionut Baur"

WORKDIR /app
COPY . .

# Download dependencies (this step is cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Use Maven to extract the artifact version and build the project
RUN VERSION=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version) && \
    mvn clean install -DskipTests && \
    mv target/thermostat-enhancer-${VERSION}-runner.jar target/app.jar

FROM eclipse-temurin:21-jdk

LABEL org.opencontainers.image.authors="Ionut Baur"

# Copy the uber-jar from the build stage
COPY --from=build /app/target/app.jar /thermostat-enhancer.jar
CMD ["java", "-jar", "thermostat-enhancer.jar"]