package com.hospitalApi.consults.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateConsultRequestDTO {

    @DecimalMin(value = "0.01", inclusive = true, message = "El costo de la consulta debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El costo de la consulta debe tener un máximo de 10 dígitos enteros y 2 decimales")
    BigDecimal costoConsulta;
}
