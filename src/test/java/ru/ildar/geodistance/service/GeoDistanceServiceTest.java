package ru.ildar.geodistance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.exception.GeoServiceException;
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
    public void testProcessAddress_success() {
        String address = "Москва, Красная площадь";

        Coordinates yandexCoords = new Coordinates(55.753215, 37.622504);
        Coordinates dadataCoords = new Coordinates(55.753220, 37.622510);

        Mockito.when(yandexGeoService.getCoordinates(address)).thenReturn(yandexCoords);
        Mockito.when(dadataGeoService.getCoordinates(address)).thenReturn(dadataCoords);

        Mockito.when(distanceCalculator.calculateDistanceMeters(
                        yandexCoords.getLatitude(), yandexCoords.getLongitude(),
                        dadataCoords.getLatitude(), dadataCoords.getLongitude()))
                .thenReturn(10.0);

        // Мокируем сохранение в репозитории, чтобы ничего не делать
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        GeoResponse response = geoDistanceService.processAddress(address);

        assertNotNull(response);
        assertEquals(address, response.getAddress());
        assertEquals(yandexCoords.getLatitude(), response.getYandexLatitude());
        assertEquals(yandexCoords.getLongitude(), response.getYandexLongitude());
        assertEquals(dadataCoords.getLatitude(), response.getDadataLatitude());
        assertEquals(dadataCoords.getLongitude(), response.getDadataLongitude());
        assertEquals(10.0, response.getDistanceMeters());
        assertEquals("Успешно", response.getMessage());
    }

    @Test
    public void testProcessAddress_yandexServiceFails() {
        String address = "Invalid address";

        Mockito.when(yandexGeoService.getCoordinates(address))
                .thenThrow(new GeoServiceException("Yandex service error"));

        GeoServiceException exception = assertThrows(GeoServiceException.class, () -> {
            geoDistanceService.processAddress(address);
        });

        assertTrue(exception.getMessage().contains("Yandex service error"));
    }
}
