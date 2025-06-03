package ru.ildar.geodistance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoResponse {

    private String address;
    private double yandexLatitude;
    private double yandexLongitude;
    private double dadataLatitude;
    private double dadataLongitude;
    private double distanceMeters;
    private String message; // для ошибок или дополнительной информации

    // Конструктор для случаев с сообщением об ошибке
    public GeoResponse(String message) {
        this.message = message;
    }
}
