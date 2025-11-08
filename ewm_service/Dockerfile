# syntax=docker/dockerfile:1.7
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /src

# POM'ы для прогрева кэша
COPY pom.xml .
COPY stat_svc/pom.xml stat_svc/pom.xml
COPY stat_svc/dto/pom.xml stat_svc/dto/pom.xml
COPY stat_svc/client/pom.xml stat_svc/client/pom.xml
COPY stat_svc/service/pom.xml stat_svc/service/pom.xml
COPY ewm_service/pom.xml ewm_service/pom.xml
RUN mvn -B -q -DskipTests dependency:go-offline

# Исходники и сборка только модуля ewm_service (с зависимостями)
COPY stat_svc/dto/src stat_svc/dto/src
COPY stat_svc/client/src stat_svc/client/src
COPY ewm_service/src ewm_service/src
RUN mvn -B -DskipTests -pl ewm_service -am package spring-boot:repackage \
 && cp ewm_service/target/*-exec.jar /src/app.jar 2>/dev/null || cp ewm_service/target/*.jar /src/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
