package com.hospitalApi.surgery.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSugeryRequestDTO {
    @NotBlank(message = "El id de la consulta es requerido")
    private String consultId;
    @NotBlank(message = "El id del tipo de cirugía es requerido")
    private String surgeryTypeId;
}
