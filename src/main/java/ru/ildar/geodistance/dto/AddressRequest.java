package ru.ildar.geodistance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Первый адрес не должен быть пустым")
    private String address1;

    @NotBlank(message = "Второй адрес не должен быть пустым")
    private String address2;

}
