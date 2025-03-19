package com.hospitalApi.surgery.models;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "empleado_cirugia")
public class SurgeryEmployee extends Auditor {
    @ManyToOne
    @JoinColumn(name = "surgery_id", nullable = false)
    private Surgery surgery;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "specialist_employee_id", nullable = true)
    private SpecialistEmployee specialistEmployee;

    @NotBlank(message = "El pago al especialista es requerido")
    @DecimalMin(value = "0.01", message = "El pago al especialista debe ser mayor a 0")
    @Column(nullable = false)
    private Double specialistPayment;
}
