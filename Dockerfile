# ---- Stage 1: build the jar with Maven ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# copy pom first so dependencies are cached between builds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---- Stage 2: small runtime image ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# demo user (do not run as root)
ARG USER_ID=1000
ARG GROUP_ID=1000
RUN addgroup -g ${GROUP_ID} demo \
 && adduser -D demo -u ${USER_ID} -G demo -s /bin/sh

COPY --from=build --chown=demo /build/target/*.jar app.jar

USER demo
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
