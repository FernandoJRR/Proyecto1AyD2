package com.hospitalApi.reports.dtos.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO utilizado para representar información básica de un empleado en reportes.
 * 
 * @param employeeFullName el nombre completo del trabajador
 * @param cui              es el numero unico de identificacion, tambien puede
 *                         ser el numero de dpi.
 * @param salary           es el salario del empleado en cuestion.
 */
@Getter
@RequiredArgsConstructor
public class EmployeeDtoForReport {

    private final String employeeFullName;
    private final String cui;
    private final BigDecimal salary;
    private final String employeeType;

}
