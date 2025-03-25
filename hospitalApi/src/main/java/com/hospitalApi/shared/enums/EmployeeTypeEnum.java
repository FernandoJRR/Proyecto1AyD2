package com.hospitalApi.shared.enums;

import com.hospitalApi.employees.models.EmployeeType;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum que define los tipos de empleados predefinidos en el sistema.
 */
@Getter
@AllArgsConstructor
public enum EmployeeTypeEnum {

    DEFAULT(new EmployeeType("Sin Asignar")),
    ADMIN(new EmployeeType("Admin"));

    private final EmployeeType employeeType;
}
