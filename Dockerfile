#
# Build stage
#
FROM maven:3.6-jdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn -Pdev clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
WORKDIR /usr/local/lib
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]