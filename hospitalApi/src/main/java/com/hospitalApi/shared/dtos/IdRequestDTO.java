package com.hospitalApi.shared.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class IdRequestDTO {

    @NotBlank(message = "El id no puede estar vacio.")
    String id;
}
