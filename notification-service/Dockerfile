FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml pom.xml
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-ubi9-minimal

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
