# Используем официальный образ OpenJDK
FROM openjdk:17-jdk-slim

# Создаем рабочую директорию
WORKDIR /app

# Копируем JAR файл в контейнер
COPY target/*.jar app.jar

# Указываем порт, который будет использоваться приложением
EXPOSE 8081

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
