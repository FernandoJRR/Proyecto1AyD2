package com.hospitalApi.employees.models;

import java.util.List;

import com.hospitalApi.shared.models.Auditor;
import com.hospitalApi.surgery.models.SurgeryEmployee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "empleado_especialista")
public class SpecialistEmployee extends Auditor {

    @NotBlank(message = "Los nombres del especialista son requeridos")
    @Size(min = 3, max = 100, message = "Los nombres del especialista deben tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos del especialista son requeridos")
    @Size(min = 3, max = 100, message = "Los apellidos del especialista deben tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @OneToMany(mappedBy = "specialistEmployee")
    private List<SurgeryEmployee> surgeryEmployees;

}
