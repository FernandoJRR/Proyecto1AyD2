package com.hospitalApi.medicines.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateMedicineRequestDTO {

    @Size(min = 1, max = 100, message = "El nombre del medicamento debe tener entre 1 y 100 caracteres")
    private String name;

    @Size(min = 1, message = "La descripción del medicamento debe tener al menos 1 caracter")
    private String description;

    @Min(value = 0, message = "La cantidad del medicamento no puede ser menor a 0")
    private Integer quantity;

    @Min(value = 0, message = "La cantidad mínima del medicamento no puede ser menor a 0")
    private Integer minQuantity;

    @DecimalMin(value = "1.0", inclusive = true, message = "El precio del medicamento no puede ser menor a Q1.00")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener un máximo de 10 dígitos enteros y 2 decimales")
    private Double price;
}
