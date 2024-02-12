#
# Build stage
# Set arguments with --build-arg [arg-variable]=[value]
FROM maven:3.8-openjdk-17-slim AS build
ARG ENVIRONMENT=dev
COPY src /home/app/src
COPY pom.xml /home/app
WORKDIR /home/app
RUN echo "Building docker image, environment set to: $ENVIRONMENT" | mvn clean package -P$ENVIRONMENT -DskipTests

#
# Package stage
#
FROM openjdk:17-slim
LABEL maintainer="kjetil.torvund.minde@ngn.no"
WORKDIR /usr/local/lib
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]