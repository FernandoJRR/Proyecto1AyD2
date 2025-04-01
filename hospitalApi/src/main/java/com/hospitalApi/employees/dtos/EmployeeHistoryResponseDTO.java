package com.hospitalApi.employees.dtos;

import lombok.Value;

@Value
public class EmployeeHistoryResponseDTO {

    EmployeeResponseDTO employee;

    HistoryTypeResponseDTO historyType;

    String commentary;

    String historyDate;
}
