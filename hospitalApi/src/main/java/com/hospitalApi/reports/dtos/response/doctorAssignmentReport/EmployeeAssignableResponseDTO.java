package com.hospitalApi.reports.dtos.response.doctorAssignmentReport;

import java.math.BigDecimal;
import java.util.List;

import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.reports.dtos.response.EmployeeDtoForReport;

import lombok.Getter;

/**
 * DTO que representa a un médico con sus consultas asignadas.
 * 
 * Se usa en el reporte de asignación de doctores.
 * 
 * @param List<ConsultResponseDTO> son las consultas asignadas al médico.
 */
@Getter
public class EmployeeAssignableResponseDTO extends EmployeeDtoForReport {

    private List<ConsultResponseDTO> assignedConsults;

    /**
     * Este constructor inicia todas lso parametros de la clase.
     *
     * @param employeeFullName nombre del médico
     * @param cui              CUI del médico
     * @param salary           salario actual
     * @param assignedConsults lista de consultas asignadas
     */
    public EmployeeAssignableResponseDTO(String employeeFullName, String cui, BigDecimal salary,
            String employeeType,
            List<ConsultResponseDTO> assignedConsults) {
        super(employeeFullName, cui, salary, employeeType);
        this.assignedConsults = assignedConsults;
    }

}
