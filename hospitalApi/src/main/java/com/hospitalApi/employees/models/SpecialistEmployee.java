package com.hospitalApi.employees.models;

import java.util.List;

import com.hospitalApi.employees.dtos.CreateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.employees.dtos.UpdateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.shared.models.Auditor;
import com.hospitalApi.surgery.models.SurgeryEmployee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SpecialistEmployee extends Auditor {

    @NotBlank(message = "Los nombres del especialista son requeridos")
    @Size(min = 3, max = 100, message = "Los nombres del especialista deben tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos del especialista son requeridos")
    @Size(min = 3, max = 100, message = "Los apellidos del especialista deben tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El DPI del especialista es requerido")
    @Pattern(regexp = "^[0-9]{13}$", message = "El DPI del especialista debe tener 13 dígitos")
    @Column(nullable = false, length = 13, unique = true)
    private String dpi;

    @OneToMany(mappedBy = "specialistEmployee")
    private List<SurgeryEmployee> surgeryEmployees;

    public SpecialistEmployee(CreateSpecialistEmpleoyeeRequestDTO createSpecialistEmpleoyeeRequestDTO) {
        this.nombres = createSpecialistEmpleoyeeRequestDTO.getNombres();
        this.apellidos = createSpecialistEmpleoyeeRequestDTO.getApellidos();
        this.dpi = createSpecialistEmpleoyeeRequestDTO.getDpi();
    }

    public SpecialistEmployee updateFromDTO(UpdateSpecialistEmpleoyeeRequestDTO updateSpecialistEmpleoyeeRequestDTO) {
        if (updateSpecialistEmpleoyeeRequestDTO.getNombres() != null) {
            this.nombres = updateSpecialistEmpleoyeeRequestDTO.getNombres();
        }
        if (updateSpecialistEmpleoyeeRequestDTO.getApellidos() != null) {
            this.apellidos = updateSpecialistEmpleoyeeRequestDTO.getApellidos();
        }
        if (updateSpecialistEmpleoyeeRequestDTO.getDpi() != null) {
            this.dpi = updateSpecialistEmpleoyeeRequestDTO.getDpi();
        }
        return this;
    }
}
