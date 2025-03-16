FROM ubuntu:latest AS build

RUN apt-get update && apt-get install -y openjdk-21-jdk maven

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

EXPOSE 8080

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

#FROM eclipse-temurin:21-jdk

#WORKDIR /app

#COPY target/*.jar app.jar

#EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]