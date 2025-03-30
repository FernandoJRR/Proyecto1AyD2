package com.hospitalApi.surgery.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddDeleteEmployeeSurgeryDTO {
    @NotBlank(message = "El id de la cirugía es requerido")
    private String surgeryId;
    @NotBlank(message = "El id del empleado es requerido")
    private String doctorId;
    @NotNull(message = "El tipo de doctor es requerido")
    private Boolean isSpecialist;
}
