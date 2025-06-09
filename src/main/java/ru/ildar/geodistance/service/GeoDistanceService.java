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
            System.out.println("Обрабатываем адрес: " + address);

            Coordinates yandexCoords = yandexGeoService.getCoordinates(address);
            System.out.println("Yandex coords: " + yandexCoords);

            Coordinates dadataCoords = dadataGeoService.getCoordinates(address);
            System.out.println("Dadata coords: " + dadataCoords);

            double distance = distanceCalculator.calculateDistanceMeters(
                    yandexCoords.getLatitude(),
                    yandexCoords.getLongitude(),
                    dadataCoords.getLatitude(),
                    dadataCoords.getLongitude()
            );

            AddressEntity entity = AddressEntity.builder()
                    .address(address)
                    .yandexLatitude(yandexCoords.getLatitude())
                    .yandexLongitude(yandexCoords.getLongitude())
                    .dadataLatitude(dadataCoords.getLatitude())
                    .dadataLongitude(dadataCoords.getLongitude())
                    .distanceMeters(distance)
                    .build();

            addressRepository.save(entity);

            return new GeoResponse(
                    address,
                    yandexCoords.getLatitude(),
                    yandexCoords.getLongitude(),
                    address,
                    dadataCoords.getLatitude(),
                    dadataCoords.getLongitude(),
                    distance,
                    "Успешно"
            );
        } catch (Exception e) {
            return new GeoResponse("Ошибка при обработке адреса: " + e.getMessage());
        }
    }


}
