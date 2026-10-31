FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl \
    && addgroup -S app \
    && adduser -S -G app -h /app app
WORKDIR /app
COPY --from=build /workspace/target/champions-lab5.jar /app/champions-lab5.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/champions-lab5.jar"]
