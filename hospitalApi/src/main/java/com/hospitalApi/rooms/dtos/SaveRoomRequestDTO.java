package com.hospitalApi.rooms.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveRoomRequestDTO {

    @NotBlank(message = "El número de habitación es obligatorio")
    @Size(max = 50, message = "El número de habitación no puede exceder los 50 caracteres")
    private String number;

    @NotNull(message = "El costo de mantenimiento diario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo de mantenimiento debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El costo de mantenimiento puede tener hasta 2 decimales")
    private BigDecimal dailyMaintenanceCost;

    @NotNull(message = "El precio diario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio diario debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio diario puede tener hasta 2 decimales")
    private BigDecimal dailyPrice;

}
