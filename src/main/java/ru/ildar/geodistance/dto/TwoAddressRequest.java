package ru.ildar.geodistance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoAddressRequest {

    @NotBlank(message = "Адрес для Yandex не должен быть пустым")
    private String yandexAddress;

    @NotBlank(message = "Адрес для Dadata не должен быть пустым")
    private String dadataAddress;
}
