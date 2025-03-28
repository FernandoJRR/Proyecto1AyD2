package com.hospitalApi.surgery.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSurgeryTypeRequestDTO {
    @Size(min = 3, max = 100, message = "El nombre del tipo de cirugía debe tener entre 3 y 100 caracteres")
    private String type;

    private String description;

    @DecimalMin(value = "0.01", message = "El pago al especialista debe ser mayor a 0")
    private Double specialistPayment;

    @DecimalMin(value = "0.01", message = "El costo del hospital debe ser mayor a 0")
    private Double hospitalCost;

    @DecimalMin(value = "0.01", message = "El costo de la cirugía debe ser mayor a 0")
    private Double surgeryCost;
}
