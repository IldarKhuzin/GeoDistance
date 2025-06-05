package ru.ildar.geodistance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.ildar.geodistance.exception.GeoServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DadataGeoService {

    private final WebClient dadataWebClient;
    private static final Logger log = LoggerFactory.getLogger(DadataGeoService.class);

    @Value("${app.dadata.api-key}")
    private String apiKey;

    @Value("${app.dadata.secret}")
    private String secretKey;

    public Coordinates getCoordinates(String address) {
        try {
            var response = dadataWebClient.post()
                    .uri("")
                    .header(HttpHeaders.AUTHORIZATION, "Token " + apiKey)
                    .header("X-Secret", secretKey)  // Добавляем секретный ключ
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .bodyValue(List.of(address))
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response == null || response.isEmpty()) {
                log.warn("Пустой ответ от Dadata для адреса: {}", address);
                throw new GeoServiceException("Dadata не вернула координаты для адреса");
            }

            Map<String, Object> data = (Map<String, Object>) response.get(0);

            // Проверяем наличие и корректность координат
            if (data.get("geo_lat") == null || data.get("geo_lon") == null) {
                log.warn("Координаты не найдены в ответе Dadata для адреса: {}. Ответ: {}", address, data);
                throw new GeoServiceException("Dadata не вернула координаты для адреса");
            }

            try {
                double latitude = Double.parseDouble(data.get("geo_lat").toString());
                double longitude = Double.parseDouble(data.get("geo_lon").toString());

                // Дополнительная валидация координат
                if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
                    throw new GeoServiceException("Некорректные координаты от Dadata");
                }

                return new Coordinates(latitude, longitude);

            } catch (NumberFormatException e) {
                log.error("Ошибка парсинга координат из ответа Dadata: {}", data, e);
                throw new GeoServiceException("Некорректный формат координат от Dadata");
            }

        } catch (GeoServiceException e) {
            throw e; // Пробрасываем уже обработанные ошибки
        } catch (Exception e) {
            log.error("Ошибка при вызове Dadata API для адреса [{}]: {}", address, e.getMessage(), e);
            throw new GeoServiceException("Ошибка при вызове Dadata API: " + e.getMessage(), e);
        }
    }
}