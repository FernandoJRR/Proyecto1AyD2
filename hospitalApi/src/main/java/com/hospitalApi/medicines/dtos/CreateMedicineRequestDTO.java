package com.hospitalApi.medicines.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicineRequestDTO {

    @NotBlank(message = "El nombre del medicamento es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre del medicamento debe tener entre 1 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción del medicamento es obligatoria")
    @Size(min = 1, message = "La descripción del medicamento debe tener al menos 1 caracter")
    private String description;

    @NotNull(message = "La cantidad del medicamento es obligatoria")
    @Min(value = 0, message = "La cantidad del medicamento no puede ser menor a 0")
    private Integer quantity;

    @NotNull(message = "La cantidad mínima del medicamento es obligatoria")
    @Min(value = 0, message = "La cantidad mínima del medicamento no puede ser menor a 0")
    private Integer minQuantity;

    @NotNull(message = "El precio del medicamento es obligatorio")
    @DecimalMin(value = "1.0", inclusive = true, message = "El precio del medicamento no puede ser menor a Q1.00")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener un máximo de 10 dígitos enteros y 2 decimales")
    private BigDecimal price;

    @NotNull(message = "El costo del medicamento es requerido")
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo del medicamento debe ser mayor a 0")
    private BigDecimal cost;
}
