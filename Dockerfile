#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn -P prod clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
LABEL maintainer="kjetil.torvund.minde@ngn.no"
WORKDIR /usr/local/lib
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]