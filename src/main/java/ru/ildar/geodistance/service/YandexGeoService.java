package ru.ildar.geodistance.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.ildar.geodistance.exception.GeoServiceException;

import java.util.List;
import java.util.Map;

@Service
public class YandexGeoService {

    private final WebClient webClient;

    @Value("${app.yandex.api-key}")
    private String apiKey;

    public YandexGeoService(@Qualifier("yandexWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Coordinates getCoordinates(String address) {
        try {
            Map<String, Object> responseMap = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geocode")
                            .queryParam("apikey", apiKey)
                            .queryParam("format", "json")
                            .queryParam("geocode", address)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (responseMap == null) {
                throw new GeoServiceException("Empty response from Yandex API");
            }

            Map<String, Object> response = castToMap(responseMap.get("response"));
            Map<String, Object> geoObjectCollection = castToMap(response.get("GeoObjectCollection"));
            List<Map<String, Object>> featureMember = castToList(geoObjectCollection.get("featureMember"));

            if (featureMember.isEmpty()) {
                throw new GeoServiceException("No GeoObjects found in Yandex API response");
            }

            Map<String, Object> firstFeature = featureMember.get(0);
            Map<String, Object> geoObject = castToMap(firstFeature.get("GeoObject"));
            Map<String, Object> point = castToMap(geoObject.get("Point"));

            String pos = (String) point.get("pos");
            String[] coords = pos.split(" ");
            if (coords.length != 2) {
                throw new GeoServiceException("Invalid coordinates format from Yandex API");
            }

            double longitude = Double.parseDouble(coords[0]);
            double latitude = Double.parseDouble(coords[1]);

            return new Coordinates(latitude, longitude);

        } catch (Exception e) {
            throw new GeoServiceException("Failed to get coordinates from Yandex API", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        throw new GeoServiceException("Expected Map but got " + (obj == null ? "null" : obj.getClass()));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castToList(Object obj) {
        if (obj instanceof List) {
            return (List<Map<String, Object>>) obj;
        }
        throw new GeoServiceException("Expected List but got " + (obj == null ? "null" : obj.getClass()));
    }
}
