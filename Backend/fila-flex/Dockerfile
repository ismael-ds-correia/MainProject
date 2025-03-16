FROM ubuntu:latest AS build

RUN apt-get update

RUN apt-get install -y openjdk-21-jdk

COPY . .

RUN apt-get install -y maven

RUN mvn clean install

FROM openjdk:21-jdk-slim

EXPOSE 8080

COPY --from=build /target/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]

#FROM eclipse-temurin:21-jdk

#WORKDIR /app

#COPY target/*.jar app.jar

#EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]