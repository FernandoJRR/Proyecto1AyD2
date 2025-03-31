package com.hospitalApi.consults.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddDeleteEmployeeConsultRequestDTO {
    @NotBlank(message = "El id de la consulta no puede estar vacío")
    private String consultId;
    @NotBlank(message = "El id del empleado no puede estar vacío")
    private String employeeId;
}
