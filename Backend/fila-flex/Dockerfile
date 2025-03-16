FROM ubuntu:latest AS build

WORKDIR /app

RUN apt-get update && apt-get install openjdk-21-jdk -y

COPY . .

RUN apt-get update && apt-get install -y maven

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk

WORKDIR /app
EXPOSE 8080

COPY --from=build /app/target/fila-flex-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

#FROM eclipse-temurin:21-jdk

#WORKDIR /app

#COPY target/*.jar app.jar

#EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]