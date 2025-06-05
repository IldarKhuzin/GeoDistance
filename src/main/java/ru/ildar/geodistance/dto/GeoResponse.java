package ru.ildar.geodistance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoResponse {

    private String address1;
    private double yandexLatitude;
    private double yandexLongitude;

    private String address2;
    private double dadataLatitude;
    private double dadataLongitude;

    private double distanceMeters;

    private String message; // для ошибок или статуса

    // Конструктор только с сообщением
    public GeoResponse(String message) {
        this.message = message;
    }
}
