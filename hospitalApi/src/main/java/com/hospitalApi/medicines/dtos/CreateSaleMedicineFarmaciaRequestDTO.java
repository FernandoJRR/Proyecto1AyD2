package com.hospitalApi.medicines.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSaleMedicineFarmaciaRequestDTO {
    
    @NotBlank(message = "El id del medicamento es requerido")
    private String medicineId;

    @NotNull(message = "La cantidad del medicamento es requerida")
    @Min(value = 1, message = "La cantidad del medicamento no puede ser menor a 1")
    private Integer quantity;
}
