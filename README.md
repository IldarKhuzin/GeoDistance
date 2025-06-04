📍 DistCalc

GeoDistance — это Spring Boot приложение для сравнения координат,
полученных от двух геокодеров (Yandex и Dadata), с вычислением расстояния между ними и сохранением
результата в базу данных MySQL. Проект также включает мониторинг через Prometheus и визуализацию метрик
в Grafana.

------------------------------------------------------------

🚀 Возможности

- Приём адреса через REST API
- Получение координат из:
    - 🗺️ Yandex Maps API
    - 📍 Dadata API
- Расчёт расстояния между координатами (в метрах)
- Сохранение результатов в базу данных
- Мониторинг метрик Prometheus + Grafana
- Контейнеризация через Docker

------------------------------------------------------------

🧱 Технологии

- Java 17
- Spring Boot 3
- Spring Web + WebClient
- Spring Data JPA (MySQL)
- Docker + Docker Compose
- Prometheus + Grafana
- Lombok

------------------------------------------------------------

📦 Структура проекта

GeoDistance-service/
├── src/

│   ├── main/
│   │   ├── java/ru/ildar/geodistance/
│   │   │   ├── config/
│   │   │   │   ├── WebClientConfig.java
│   │   │   │   ├── PrometheusConfig.java
│   │   │   │   └── AppProperties.java
│   │   │   ├── controller/
│   │   │   │   └── GeoDistanceController.java
│   │   │   ├── dto/
│   │   │   │   ├── AddressRequest.java
│   │   │   │   ├── GeoResponse.java
│   │   │   │   └── ApiErrorResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── GeoServiceException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   └── AddressEntity.java
│   │   │   ├── repository/
│   │   │   │   └── AddressRepository.java
│   │   │   ├── service/
│   │   │   │   ├── GeoDistanceService.java
│   │   │   │   ├── YandexGeoService.java
│   │   │   │   ├── DadataGeoService.java
│   │   │   │   └── DistanceCalculator.java
│   │   │   └── GeoDistanceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/ru/ildar/geodistance/
├── docker/
│   ├── prometheus/
│   │   └── prometheus.yml
│   ├── grafana/
│   │   └── dashboards/
│   │       ├── spring-boot.json
│   │       └── jvm-metrics.json
│   ├── Dockerfile
│   └── docker-compose.yml
├── .gitignore
├── pom.xml
└── README.md

------------------------------------------------------------

⚙️ Запуск проекта

1. Клонируйте репозиторий:
   git clone https://github.com/IldarKhuzin/geodistance.git
   cd geodistance

2. Соберите проект:
   cd geodistance-service
   ./mvnw clean package
   cd ..

3. Запустите всё через Docker Compose:
   docker-compose up --build

------------------------------------------------------------

🔗 REST API

POST /api/address

{
"address": "Москва, Красная площадь, 1"
}

Пример ответа:
{
"yandexCoordinates": { "lat": 55.7539, "lon": 37.6208 },
"dadataCoordinates": { "lat": 55.7540, "lon": 37.6210 },
"distanceMeters": 17.2
}

------------------------------------------------------------

📊 Мониторинг

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
    - Login: admin / admin
    - Dashboard с основными метриками по REST-запросам и ошибкам

------------------------------------------------------------

📝 TODO

- Обработка ошибок при запросах к внешним API
- Покрытие тестами
- Интеграция OpenAPI/Swagger

------------------------------------------------------------

🧑‍💻 Автор

Ильдар, 2025  
Проект выполнен в рамках технического задания для прохождения собеседования.
