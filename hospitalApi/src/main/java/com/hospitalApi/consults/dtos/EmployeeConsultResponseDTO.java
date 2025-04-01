package com.hospitalApi.consults.dtos;

import lombok.Data;

@Data
public class EmployeeConsultResponseDTO {
    private String employeeId;
    private String employeeName;
    private String employeeLastName;
    private String employeeType;
}
