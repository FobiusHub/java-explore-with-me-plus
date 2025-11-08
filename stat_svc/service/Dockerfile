# syntax=docker/dockerfile:1.7
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY ewm_service/pom.xml ./ewm_service/pom.xml
COPY stat_svc/pom.xml             ./stat_svc/pom.xml
COPY stat_svc/dto/pom.xml         ./stat_svc/dto/pom.xml
COPY stat_svc/client/pom.xml      ./stat_svc/client/pom.xml
COPY stat_svc/service/pom.xml     ./stat_svc/service/pom.xml

RUN mvn -B -q -DskipTests -f stat_svc/pom.xml dependency:go-offline

COPY stat_svc/dto/src     ./stat_svc/dto/src
COPY stat_svc/client/src  ./stat_svc/client/src
COPY stat_svc/service/src ./stat_svc/service/src

RUN mvn -B -q -DskipTests -f stat_svc/pom.xml -pl service -am package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Тоже биндимся на 0.0.0.0 и активируем профиль docker
ENV SERVER_ADDRESS=0.0.0.0 \
    SERVER_PORT=9090 \
    SPRING_PROFILES_ACTIVE=docker \
    JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"

COPY --from=build /workspace/stat_svc/service/target/*.jar /app/app.jar

EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]