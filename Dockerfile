# Используем официальный образ OpenJDK
FROM openjdk:17-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем собранный .jar файл в контейнер
COPY target/geodistance-*.jar app.jar

# Указываем порт, который будет использоваться внутри контейнера
EXPOSE 8081

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
