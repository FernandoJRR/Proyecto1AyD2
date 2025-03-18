package com.hospitalApi.patients.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePatientRequestDTO {
    @NotBlank(message = "El nombre del paciente es requerido")
    @Size(min = 1, max = 250, message = "El nombre del paciente debe tener entre 1 y 250 caracteres")
    @Pattern(regexp = "^[a-zA-Z]+(\\s+[a-zA-Z]+)*$", message = "El nombre del paciente debe contener solo letras y espacios")
    String firstnames;

    @NotBlank(message = "Los apellidos del paciente son requeridos")
    @Size(min = 1, max = 250, message = "Los apellidos del paciente deben tener entre 1 y 250 caracteres")
    @Pattern(regexp = "^[a-zA-Z]+(\\s+[a-zA-Z]+)*$", message = "Los apellidos del paciente deben contener solo letras y espacios")
    String lastnames;

    @NotBlank(message = "El número de DPI del paciente es requerido")
    @Pattern(regexp = "\\d{13}", message = "El número de DPI debe contener solo números y tener 13 dígitos")
    String dpi;
}
