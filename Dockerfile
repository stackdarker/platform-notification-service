# ---- build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests clean package

RUN set -eux; \
    JAR="$(ls -1 target/*.jar | grep -v '\.jar\.original$' | head -n 1)"; \
    cp "$JAR" /app/app.jar; \
    echo "Using jar: $JAR"

# ---- runtime stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl ca-certificates \
  && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
