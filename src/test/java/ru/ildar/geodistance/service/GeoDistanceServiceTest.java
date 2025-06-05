package ru.ildar.geodistance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.exception.GeoServiceException;
import ru.ildar.geodistance.model.AddressEntity;
import ru.ildar.geodistance.repository.AddressRepository;

import static org.junit.jupiter.api.Assertions.*;

public class GeoDistanceServiceTest {

    private GeoDistanceService geoDistanceService;

    private YandexGeoService yandexGeoService;
    private DadataGeoService dadataGeoService;
    private DistanceCalculator distanceCalculator;
    private AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        yandexGeoService = Mockito.mock(YandexGeoService.class);
        dadataGeoService = Mockito.mock(DadataGeoService.class);
        distanceCalculator = Mockito.mock(DistanceCalculator.class);
        addressRepository = Mockito.mock(AddressRepository.class);

        geoDistanceService = new GeoDistanceService(yandexGeoService, dadataGeoService, distanceCalculator, addressRepository);
    }

    @Test
    public void testProcessAddresses_success() {
        String address1 = "Москва, Красная площадь";
        String address2 = "Санкт-Петербург, Невский проспект";

        Coordinates yandexCoords = new Coordinates(55.753215, 37.622504);
        Coordinates dadataCoords = new Coordinates(59.934280, 30.335099);

        Mockito.when(yandexGeoService.getCoordinates(address1)).thenReturn(yandexCoords);
        Mockito.when(dadataGeoService.getCoordinates(address2)).thenReturn(dadataCoords);

        Mockito.when(distanceCalculator.calculateDistanceMeters(
                        yandexCoords.getLatitude(), yandexCoords.getLongitude(),
                        dadataCoords.getLatitude(), dadataCoords.getLongitude()))
                .thenReturn(635000.0);

        Mockito.when(addressRepository.save(Mockito.any(AddressEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        GeoResponse response = geoDistanceService.processAddresses(address1, address2);

        assertNotNull(response);
        assertEquals(address1, response.getAddress1());
        assertEquals(yandexCoords.getLatitude(), response.getYandexLatitude(), 0.00001);
        assertEquals(yandexCoords.getLongitude(), response.getYandexLongitude(), 0.00001);
        assertEquals(address2, response.getAddress2());
        assertEquals(dadataCoords.getLatitude(), response.getDadataLatitude(), 0.00001);
        assertEquals(dadataCoords.getLongitude(), response.getDadataLongitude(), 0.00001);
        assertEquals(635000.0, response.getDistanceMeters(), 0.1);
        assertEquals("Успешно", response.getMessage());
    }

    @Test
    public void testProcessAddresses_yandexServiceFails() {
        String address1 = "Некорректный адрес";
        String address2 = "Корректный адрес";

        Mockito.when(yandexGeoService.getCoordinates(address1))
                .thenThrow(new GeoServiceException("Ошибка Yandex сервиса"));

        GeoResponse response = geoDistanceService.processAddresses(address1, address2);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("Ошибка Yandex сервиса"));
    }

    @Test
    public void testProcessAddresses_dadataServiceFails() {
        String address1 = "Корректный адрес 1";
        String address2 = "Некорректный адрес 2";

        Coordinates yandexCoords = new Coordinates(55.753215, 37.622504);
        Mockito.when(yandexGeoService.getCoordinates(address1)).thenReturn(yandexCoords);

        Mockito.when(dadataGeoService.getCoordinates(address2))
                .thenThrow(new GeoServiceException("Ошибка Dadata сервиса"));

        GeoResponse response = geoDistanceService.processAddresses(address1, address2);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("Ошибка Dadata сервиса"));
    }
}
