FROM openjdk:17-jdk-slim

WORKDIR /app

# Установка netcat
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

COPY target/geodistance-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
