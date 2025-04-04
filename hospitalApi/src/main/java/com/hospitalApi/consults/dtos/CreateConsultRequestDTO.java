package com.hospitalApi.consults.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsultRequestDTO {

    @NotBlank(message = "El id del paciente es requerido")
    String patientId;

    @NotNull(message = "El costo de la consulta es requerido")
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo de la consulta debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El costo de la consulta debe tener un máximo de 10 dígitos enteros y 2 decimales")
    BigDecimal costoConsulta;

    @NotBlank(message = "EL id del empleado es requerido")
    String employeeId;

}
