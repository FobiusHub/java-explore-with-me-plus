# syntax=docker/dockerfile:1.7

FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

# POM'ы
COPY pom.xml                               ./pom.xml
COPY stat_svc/pom.xml                      ./stat_svc/pom.xml
COPY stat_svc/dto/pom.xml                  ./stat_svc/dto/pom.xml
COPY stat_svc/client/pom.xml               ./stat_svc/client/pom.xml
COPY stat_svc/service/pom.xml              ./stat_svc/service/pom.xml
COPY ewm_service/pom.xml                   ./ewm_service/pom.xml

RUN mvn -B -q -DskipTests dependency:go-offline

# Исходники
COPY stat_svc/dto/src                      ./stat_svc/dto/src
COPY stat_svc/client/src                   ./stat_svc/client/src
COPY ewm_service/src                       ./ewm_service/src

RUN mvn -B -DskipTests -pl ewm_service -am package spring-boot:repackage

RUN set -eux; \
    JAR="$(ls ewm_service/target/*-exec.jar 2>/dev/null || ls ewm_service/target/*.jar | head -n1)"; \
    echo "Using JAR: $JAR"; \
    cp "$JAR" /workspace/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java","-jar","/app/app.jar"]
