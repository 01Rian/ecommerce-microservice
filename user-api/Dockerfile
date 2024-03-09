FROM maven:3.9.6-eclipse-temurin-17
MAINTAINER Rian Santos
WORKDIR /app
COPY ./pom.xml /app/pom.xml
RUN mvn -B dependency:resolve dependency:resolve-plugins
COPY ./src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=0 /app/target/*.jar /app/api.jar
ENTRYPOINT ["java", "-jar", "api.jar"]
EXPOSE 8080