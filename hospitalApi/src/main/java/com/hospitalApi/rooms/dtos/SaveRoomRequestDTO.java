package com.hospitalApi.rooms.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveRoomRequestDTO {

    @NotBlank(message = "El número de habitación es obligatorio")
    @Size(max = 100, message = "El número de habitación no puede exceder los 50 caracteres")
    private String number;

    @NotNull(message = "El costo de mantenimiento diario es obligatorio")
    @Min(value = 0, message = "El costo de mantenimiento diario debe ser mayor o igual a 0")
    private BigDecimal dailyMaintenanceCost;

    @NotNull(message = "El precio diario es obligatorio")
    @Min(value = 0, message = "El precio diario debe ser mayor o igual a 0")
    private BigDecimal dailyPrice;

}
