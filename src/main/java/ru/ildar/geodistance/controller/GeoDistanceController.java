package ru.ildar.geodistance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ildar.geodistance.dto.AddressRequest;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.exception.GeoServiceException;
import ru.ildar.geodistance.service.GeoDistanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/distance")  // Путь изменён под тест
@RequiredArgsConstructor
@Validated
public class GeoDistanceController {

    private final GeoDistanceService geoDistanceService;

    @PostMapping  // Теперь POST запросы идут на /api/distance
    public ResponseEntity<GeoResponse> calculateDistance(@Valid @RequestBody AddressRequest request) {
        try {
            GeoResponse response = geoDistanceService.processAddress(request.getAddress());
            return ResponseEntity.ok(response);
        } catch (GeoServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GeoResponse("Ошибка при обработке адреса: " + e.getMessage()));
        }
    }
}
