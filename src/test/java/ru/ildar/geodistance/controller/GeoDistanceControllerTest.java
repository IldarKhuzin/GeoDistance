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
import ru.ildar.geodistance.service.GeoDistanceService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GeoDistanceController.class)
public class GeoDistanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeoDistanceService geoDistanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testProcessAddress_success() throws Exception {
        AddressRequest request = new AddressRequest("Москва, Красная площадь");

        GeoResponse response = new GeoResponse();
        response.setAddress("Москва, Красная площадь");
        response.setYandexLatitude(55.753215);
        response.setYandexLongitude(37.622504);
        response.setDadataLatitude(55.753215);
        response.setDadataLongitude(37.622504);
        response.setDistanceMeters(0.0);
        response.setMessage("Успешно");

        Mockito.when(geoDistanceService.processAddress(Mockito.anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())  // Добавлен вывод лога
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Москва, Красная площадь"))
                .andExpect(jsonPath("$.yandexLatitude").value(55.753215))
                .andExpect(jsonPath("$.yandexLongitude").value(37.622504))
                .andExpect(jsonPath("$.dadataLatitude").value(55.753215))
                .andExpect(jsonPath("$.dadataLongitude").value(37.622504))
                .andExpect(jsonPath("$.distanceMeters").value(0.0))
                .andExpect(jsonPath("$.message").value("Успешно"));
    }

    @Test
    public void testProcessAddress_badRequest() throws Exception {
        AddressRequest request = new AddressRequest("");

        mockMvc.perform(post("/api/distance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
