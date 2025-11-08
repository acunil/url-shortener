# ---------- build stage ----------
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# copy maven wrapper and pom first for caching
COPY pom.xml mvnw ./

# copy sources and build
COPY src ./src
RUN mvn -B -DskipTests package

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
ARG JAR_GLOB="*.jar"
WORKDIR /app

# create data dir for H2 and ensure it is writable by the app user
RUN mkdir -p /data/h2 \
 && addgroup --system app && adduser --system --ingroup app app \
 && chown -R app:app /data /app

# copy the built jar from the build stage (matches any jar produced by mvn package)
COPY --from=build /app/target/*.jar ./app.jar

USER app
EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
