package ru.ildar.geodistance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ildar.geodistance.dto.GeoResponse;
import ru.ildar.geodistance.dto.TwoAddressRequest;
import ru.ildar.geodistance.exception.GeoServiceException;
import ru.ildar.geodistance.service.GeoDistanceService;

@RestController
@RequestMapping("/api/distance")
@RequiredArgsConstructor
@Validated
public class GeoDistanceController {

    private final GeoDistanceService geoDistanceService;

    @PostMapping
    public ResponseEntity<GeoResponse> calculateDistance(@Valid @RequestBody TwoAddressRequest request) {
        try {
            GeoResponse response = geoDistanceService.processAddresses(
                    request.getYandexAddress(),
                    request.getDadataAddress()
            );
            return ResponseEntity.ok(response);
        } catch (GeoServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GeoResponse("Ошибка при обработке адресов: " + e.getMessage()));
        }
    }
}
