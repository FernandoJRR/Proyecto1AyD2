package com.hospitalApi.employees.models;

import java.util.List;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EmployeeType extends Auditor {

    @Column(unique = true, length = 100)
    private String name;
    /**
     * Un tipo de empleado puede estar asignado a varios empleados
     */
    @OneToMany(mappedBy = "employeeType") // utilizado para no crear una tabla en la bd y Spring sepa como inicializar
                                          // este atributo
    private List<Employee> employees;

    public EmployeeType(String id, String name) {
        super(id);
        this.name = name;
    }

    public EmployeeType(String name) {
        this.name = name;
    }
}
