package com.hospitalApi.employees.dtos;

import java.time.LocalDate;

import lombok.Value;

@Value
public class EmployeeHistoryResponseDTO {

    HistoryTypeResponseDTO historyType;

    String commentary;

    LocalDate historyDate;
}
