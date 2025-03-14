package com.hospitalApi.employees.models;

import java.util.List;

import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class EmployeeType extends Auditor {

    @Column(unique = true, length = 100)
    private String name;
    /**
     * Un tipo de empleado puede estar asignado a varios empleados
     */
    @OneToMany(mappedBy = "employeeType")
    private List<Employee> employees;

    public EmployeeType(String id, String name) {
        super(id);
        this.name = name;
    }

    public EmployeeType(String id) {
        super(id);
    }

}
