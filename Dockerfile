FROM maven:3.9.11-eclipse-temurin-26 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -q test package
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/target/taskflow-ai-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
