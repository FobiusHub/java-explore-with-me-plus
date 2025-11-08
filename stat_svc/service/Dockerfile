# syntax=docker/dockerfile:1.7
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /src

COPY pom.xml .
COPY stat_svc/pom.xml stat_svc/pom.xml
COPY stat_svc/dto/pom.xml stat_svc/dto/pom.xml
COPY stat_svc/client/pom.xml stat_svc/client/pom.xml
COPY stat_svc/service/pom.xml stat_svc/service/pom.xml
RUN mvn -B -q -DskipTests -f stat_svc/pom.xml dependency:go-offline

COPY stat_svc/dto/src stat_svc/dto/src
COPY stat_svc/client/src stat_svc/client/src
COPY stat_svc/service/src stat_svc/service/src
RUN mvn -B -DskipTests -f stat_svc/pom.xml -pl service -am package \
 && cp stat_svc/service/target/*.jar /src/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/app.jar /app/app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
