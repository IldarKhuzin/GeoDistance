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
    public GeoResponse processAddress(String address) {
        try {
            var yandexCoords = yandexGeoService.getCoordinates(address);
            var dadataCoords = dadataGeoService.getCoordinates(address);

            double distance = distanceCalculator.calculateDistanceMeters(
                    yandexCoords.getLatitude(),
                    yandexCoords.getLongitude(),
                    dadataCoords.getLatitude(),
                    dadataCoords.getLongitude()
            );

            // Сохраняем в БД
            AddressEntity entity = new AddressEntity();
            entity.setAddress(address);
            entity.setYandexLatitude(yandexCoords.getLatitude());
            entity.setYandexLongitude(yandexCoords.getLongitude());
            entity.setDadataLatitude(dadataCoords.getLatitude());
            entity.setDadataLongitude(dadataCoords.getLongitude());
            entity.setDistanceMeters(distance);

            addressRepository.save(entity);

            return new GeoResponse(
                    address,
                    yandexCoords.getLatitude(),
                    yandexCoords.getLongitude(),
                    dadataCoords.getLatitude(),
                    dadataCoords.getLongitude(),
                    distance,
                    "Успешно"
            );
        } catch (Exception e) {
            throw new GeoServiceException("Ошибка при обработке адреса: " + e.getMessage(), e);
        }
    }
}
