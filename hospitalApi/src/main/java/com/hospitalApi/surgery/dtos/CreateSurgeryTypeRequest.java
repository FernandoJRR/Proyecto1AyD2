package com.hospitalApi.surgery.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSurgeryTypeRequest {
    @NotBlank(message = "El nombre del tipo de cirugía es requerido")
    @Size(min = 3, max = 100, message = "El nombre del tipo de cirugía debe tener entre 3 y 100 caracteres")
    private String type;

    @NotBlank(message = "La descripción del tipo de cirugía es requerida")
    private String description;

    @NotNull(message = "El pago al especialista es requerido")
    @DecimalMin(value = "0.01", message = "El pago al especialista debe ser mayor a 0")
    private BigDecimal specialistPayment;

    @NotNull(message = "El costo del hospital es requerido")
    @DecimalMin(value = "0.01", message = "El costo del hospital debe ser mayor a 0")
    private BigDecimal hospitalCost;

    @NotNull(message = "El costo de la cirugía es requerido")
    @DecimalMin(value = "0.01", message = "El costo de la cirugía debe ser mayor a 0")
    private BigDecimal surgeryCost;
}
