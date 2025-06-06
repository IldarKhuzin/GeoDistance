package ru.ildar.geodistance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.dto.TwoAddressRequest;
import ru.ildar.geodistance.exception.GeoServiceException;
import ru.ildar.geodistance.service.GeoDistanceService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GeoDistanceControllerTest {

    private MockMvc mockMvc;
    private GeoDistanceService geoDistanceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        geoDistanceService = Mockito.mock(GeoDistanceService.class);
        GeoDistanceController controller = new GeoDistanceController(geoDistanceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCalculateDistance_success() throws Exception {
        TwoAddressRequest request = new TwoAddressRequest();
        request.setYandexAddress("Москва, Красная площадь");
        request.setDadataAddress("Санкт-Петербург, Невский проспект");

        GeoResponse response = new GeoResponse(
                request.getYandexAddress(),
                55.753215,
                37.622504,
                request.getDadataAddress(),
                59.934280,
                30.335099,
                635000.0,
                "Успешно"
        );

        Mockito.when(geoDistanceService.processAddresses(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address1").value(request.getYandexAddress()))
                .andExpect(jsonPath("$.address2").value(request.getDadataAddress()))
                .andExpect(jsonPath("$.distanceMeters").value(635000.0))
                .andExpect(jsonPath("$.message").value("Успешно"));
    }

    @Test
    public void testCalculateDistance_serviceException() throws Exception {
        TwoAddressRequest request = new TwoAddressRequest();
        request.setYandexAddress("Некорректный адрес");
        request.setDadataAddress("Адрес 2");

        Mockito.when(geoDistanceService.processAddresses(anyString(), anyString()))
                .thenThrow(new GeoServiceException("Ошибка сервиса"));

        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // По твоему коду сервис не выбрасывает исключение, но в контроллере есть catch GeoServiceException.
                // В твоём сервисе сейчас исключения не выбрасываются, а возвращается GeoResponse с сообщением об ошибке.
                // Поэтому такой тест может не сработать, если не изменить сервис.
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Ошибка при обработке адресов: Ошибка сервиса")));
    }

    @Test
    public void testCalculateDistance_validationFails() throws Exception {
        // Передаем пустой объект - @Valid должен сработать и вернуть 400 Bad Request
        TwoAddressRequest request = new TwoAddressRequest();

        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
