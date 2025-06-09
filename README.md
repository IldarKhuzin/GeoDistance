# 📍 GeoDistance

**GeoDistance** — это Spring Boot приложение, которое по одному адресу получает координаты из двух разных 
геокодеров (Yandex и Dadata), сравнивает их и вычисляет расстояние между этими точками. 
Результаты сохраняются в базу данных MySQL. Проект также включает мониторинг через Prometheus и визуализацию метрик в Grafana.

---

## 🚀 Возможности

- Приём одного адреса через REST API
- Получение координат из:
    - 🗺️ Yandex Maps API
    - 📍 Dadata API
- Расчёт расстояния между координатами
- Сохранение результатов в базу данных
- Мониторинг метрик (Prometheus + Grafana)
- Контейнеризация через Docker

---

## 🧱 Технологии

- Java 17
- Spring Boot 3
- Spring Web + WebClient
- Spring Data JPA (MySQL)
- Docker + Docker Compose
- Prometheus + Grafana
- Lombok

---

## ⚙️ Запуск проекта

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/IldarKhuzin/geodistance.git
   cd geodistance
   ```

2. Соберите проект:

   ```bash
   cd geodistance-service
   ./mvn clean package
   cd ..
   ```

3. Запустите всё через Docker Compose:

   ```bash
   docker-compose up --build
   ```

---

## 🔗 REST API

**POST** `/api/address`

**Пример запроса:**

```json
{
  "address": "Москва, Костромская улица, 10"
}
```

**Пример ответа:**

```json
{
  "address1": "Москва, Костромская улица, 10",
  "yandexLatitude": 55.886418,
  "yandexLongitude": 37.595357,
  "address2": "Москва, Костромская улица, 10",
  "dadataLatitude": 55.8863445,
  "dadataLongitude": 37.5952686,
  "distanceMeters": 9.85830457256176,
  "message": "Успешно"
}
```

---

## 📊 Мониторинг

- **Prometheus:** http://localhost:9091
- **Grafana:** http://localhost:3001
    - Логин: `admin` / `admin`
    - Предустановленный дашборд с метриками REST-запросов и ошибок

---

## 📝 TODO

- Улучшенная обработка ошибок при вызовах внешних API
- Покрытие тестами
- Интеграция OpenAPI / Swagger

---

## 🧑‍💻 Автор

Ильдар, 2025  
Проект реализован в рамках технического задания для прохождения собеседования.