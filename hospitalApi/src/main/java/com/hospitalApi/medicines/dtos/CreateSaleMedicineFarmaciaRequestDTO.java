package com.hospitalApi.medicines.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSaleMedicineFarmaciaRequestDTO {
    
    @NotBlank(message = "El id del medicamento es requerido")
    private String medicineId;

    @NotBlank(message = "La cantidad del medicamento es requerida")
    @Min(value = 1, message = "La cantidad del medicamento no puede ser menor a 1")
    @Digits(integer = 10, fraction = 2, message = "La cantidad debe tener un máximo de 10 dígitos enteros y 2 decimales")
    private Integer quantity;
}
