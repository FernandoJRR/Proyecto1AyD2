package com.hospitalApi.surgery.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSugeryRequestDTO {
    @NotBlank(message = "El id de la consulta es requerido")
    private String consultId;
    @NotBlank(message = "El id del tipo de cirugía es requerido")
    private String surgeryTypeId;
    @NotBlank(message = "El id del paciente es requerido")
    private String asignedDoctorId;
    @NotNull(message = "El tipo de doctor es requerido")
    private Boolean isSpecialist;
}
