package com.hospitalApi.patients.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePatientRequestDTO {
    @NotBlank(message = "El nombre del paciente es requerido")
    private String firstnames;

    @NotBlank(message = "Los apellidos del paciente son requeridos")
    private String lastnames;

    @NotBlank(message = "El número de DPI del paciente es requerido")
    @Pattern(regexp = "\\d{13}", message = "El número de DPI debe contener solo números y tener 13 dígitos")
    private String dpi;

}
