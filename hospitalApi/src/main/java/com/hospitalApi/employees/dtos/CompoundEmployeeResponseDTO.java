package com.hospitalApi.employees.dtos;

import lombok.Value;

@Value
public class CompoundEmployeeResponseDTO {
    EmployeeResponseDTO employeeResponseDTO;
    String username;
}
