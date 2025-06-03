package ru.ildar.geodistance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.ildar.geodistance.exception.GeoServiceException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DadataGeoService {

    private final WebClient dadataWebClient;

    @Value("${app.dadata.api-key}")
    private String apiKey;

    public Coordinates getCoordinates(String address) {
        try {
            var response = dadataWebClient.post()
                    .uri("/clean/address")
                    .header(HttpHeaders.AUTHORIZATION, "Token " + apiKey)
                    .bodyValue(Map.of("query", address))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Обработка ответа и извлечение координат
            // Примерно: parse response JSON
            var suggestions = (java.util.List<Map<String, Object>>) response.get("suggestions");
            if (suggestions.isEmpty()) {
                throw new GeoServiceException("Dadata не вернула координаты для адреса");
            }
            Map<String, Object> data = (Map<String, Object>) suggestions.get(0).get("data");
            double latitude = Double.parseDouble(data.get("geo_lat").toString());
            double longitude = Double.parseDouble(data.get("geo_lon").toString());

            return new Coordinates(latitude, longitude);

        } catch (Exception e) {
            throw new GeoServiceException("Ошибка при вызове Dadata API: " + e.getMessage(), e);
        }
    }
}
