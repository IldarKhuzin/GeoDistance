package ru.ildar.geodistance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.model.AddressEntity;
import ru.ildar.geodistance.repository.AddressRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeoDistanceServiceTest {

    @Mock
    private YandexGeoService yandexGeoService;

    @Mock
    private DadataGeoService dadataGeoService;

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private GeoDistanceService geoDistanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessAddress_success() {
        String address = "Москва, Красная площадь, 1";

        Coordinates yandexCoords = new Coordinates(55.7558, 37.6173);
        Coordinates dadataCoords = new Coordinates(55.7557, 37.6175);
        double distance = 20.0;

        when(yandexGeoService.getCoordinates(address)).thenReturn(yandexCoords);
        when(dadataGeoService.getCoordinates(address)).thenReturn(dadataCoords);
        when(distanceCalculator.calculateDistanceMeters(
                yandexCoords.getLatitude(),
                yandexCoords.getLongitude(),
                dadataCoords.getLatitude(),
                dadataCoords.getLongitude())
        ).thenReturn(distance);

        // Сохраняем адрес — ничего не возвращает
        doAnswer(invocation -> {
            AddressEntity entity = invocation.getArgument(0);
            assertEquals(address, entity.getAddress());
            assertEquals(yandexCoords.getLatitude(), entity.getYandexLatitude());
            assertEquals(yandexCoords.getLongitude(), entity.getYandexLongitude());
            assertEquals(dadataCoords.getLatitude(), entity.getDadataLatitude());
            assertEquals(dadataCoords.getLongitude(), entity.getDadataLongitude());
            assertEquals(distance, entity.getDistanceMeters());
            return null;
        }).when(addressRepository).save(any(AddressEntity.class));

        GeoResponse response = geoDistanceService.processAddress(address);

        assertNotNull(response);
        assertEquals(address, response.getAddress1());
        assertEquals(yandexCoords.getLatitude(), response.getYandexLatitude());
        assertEquals(yandexCoords.getLongitude(), response.getYandexLongitude());
        assertEquals(address, response.getAddress2());
        assertEquals(dadataCoords.getLatitude(), response.getDadataLatitude());
        assertEquals(dadataCoords.getLongitude(), response.getDadataLongitude());
        assertEquals(distance, response.getDistanceMeters());
        assertEquals("Успешно", response.getMessage());
    }

    @Test
    void testProcessAddress_exception() {
        String address = "Неизвестный адрес";

        when(yandexGeoService.getCoordinates(address)).thenThrow(new RuntimeException("Ошибка Yandex"));

        GeoResponse response = geoDistanceService.processAddress(address);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("Ошибка при обработке адреса"));
    }
}
