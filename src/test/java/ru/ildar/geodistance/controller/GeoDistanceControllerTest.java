package ru.ildar.geodistance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ildar.geodistance.dto.AddressRequest;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.exception.GeoServiceException;
import ru.ildar.geodistance.service.GeoDistanceService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GeoDistanceController.class)
class GeoDistanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoDistanceService geoDistanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculateDistance_success() throws Exception {
        // given
        AddressRequest request = new AddressRequest("Москва, Красная площадь, 1");

        GeoResponse response = new GeoResponse(
                "Москва, Красная площадь, 1",
                55.7558, 37.6173,
                "Москва, Красная площадь, 1",
                55.7560, 37.6200,
                190.0,
                "Успешно"
        );

        Mockito.when(geoDistanceService.processAddress(request.getAddress()))
                .thenReturn(response);

        // when / then
        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Успешно"))
                .andExpect(jsonPath("$.address1").value("Москва, Красная площадь, 1"))
                .andExpect(jsonPath("$.address2").value("Москва, Красная площадь, 1"))
                .andExpect(jsonPath("$.distanceMeters").value(190.0))
                .andExpect(jsonPath("$.yandexLatitude").value(55.7558))
                .andExpect(jsonPath("$.dadataLatitude").value(55.7560));
    }

    @Test
    void calculateDistance_failure() throws Exception {
        // given
        AddressRequest request = new AddressRequest("Невалидный адрес");

        Mockito.when(geoDistanceService.processAddress(request.getAddress()))
                .thenThrow(new GeoServiceException("Некорректный адрес"));

        // when / then
        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ошибка при обработке адреса: Некорректный адрес"));
    }
}
