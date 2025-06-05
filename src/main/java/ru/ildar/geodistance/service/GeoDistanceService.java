package ru.ildar.geodistance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.exception.GeoServiceException;
import ru.ildar.geodistance.model.AddressEntity;
import ru.ildar.geodistance.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class GeoDistanceService {

    private final YandexGeoService yandexGeoService;
    private final DadataGeoService dadataGeoService;
    private final DistanceCalculator distanceCalculator;
    private final AddressRepository addressRepository;

    /**
     * Основной метод обработки адреса:
     * 1) Получить координаты из Yandex и Dadata
     * 2) Рассчитать расстояние между координатами
     * 3) Сохранить в БД
     * 4) Вернуть результат
     */
    public GeoResponse processAddresses(String address1, String address2) {
        try {
            // Получаем координаты для первого адреса через Yandex
            Coordinates yandexCoords = yandexGeoService.getCoordinates(address1);

            // Получаем координаты для второго адреса через Dadata
            Coordinates dadataCoords = dadataGeoService.getCoordinates(address2);

            // Рассчитываем расстояние
            double distance = distanceCalculator.calculateDistanceMeters(
                    yandexCoords.getLatitude(),
                    yandexCoords.getLongitude(),
                    dadataCoords.getLatitude(),
                    dadataCoords.getLongitude()
            );

            // Сохраняем в базу (если нужно)
            AddressEntity entity = AddressEntity.builder()
                    .address(address1 + " | " + address2)
                    .yandexLatitude(yandexCoords.getLatitude())
                    .yandexLongitude(yandexCoords.getLongitude())
                    .dadataLatitude(dadataCoords.getLatitude())
                    .dadataLongitude(dadataCoords.getLongitude())
                    .distanceMeters(distance)
                    .build();

            addressRepository.save(entity);

            // Формируем ответ
            return new GeoResponse(
                    address1,
                    yandexCoords.getLatitude(),
                    yandexCoords.getLongitude(),
                    address2,
                    dadataCoords.getLatitude(),
                    dadataCoords.getLongitude(),
                    distance,
                    "Успешно"
            );
        } catch (Exception e) {
            return new GeoResponse("Ошибка при обработке адресов: " + e.getMessage());
        }
    }

}
