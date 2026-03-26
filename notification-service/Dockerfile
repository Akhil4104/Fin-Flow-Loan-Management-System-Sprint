FROM public.ecr.aws/amazoncorretto/amazoncorretto:21

WORKDIR /app

ARG JAR_FILE=target/notification-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
