package com.hospitalApi.employees.dtos;

import java.util.List;

import lombok.Value;

@Value
public class CompoundEmployeeResponseDTO {
    EmployeeResponseDTO employeeResponseDTO;
    String username;
    List<EmployeeHistoryResponseDTO> employeeHistories;
}
