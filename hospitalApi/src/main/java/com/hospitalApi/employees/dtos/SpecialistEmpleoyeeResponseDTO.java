package com.hospitalApi.employees.dtos;

import com.hospitalApi.employees.models.SpecialistEmployee;

import lombok.Value;

@Value
public class SpecialistEmpleoyeeResponseDTO {
    private String id;
    private String nombres;
    private String apellidos;
    private String dpi;

    public SpecialistEmpleoyeeResponseDTO(SpecialistEmployee specialistEmployee) {
        this.id = specialistEmployee.getId();
        this.nombres = specialistEmployee.getNombres();
        this.apellidos = specialistEmployee.getApellidos();
        this.dpi = specialistEmployee.getDpi();
    }
}
