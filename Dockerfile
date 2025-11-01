# Build stage
FROM maven:3.8.8-eclipse-temurin-17 as build
WORKDIR /app

# Copy pom + sources
COPY pom.xml .
COPY src ./src

# Build jar (skip tests during build in Docker by default to speed up)
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# copy built jar (the jar name may vary; use glob)
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

# Java options can be adjusted via env JAVA_OPTS
ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]