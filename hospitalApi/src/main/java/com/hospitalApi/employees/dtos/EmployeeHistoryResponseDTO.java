package com.hospitalApi.employees.dtos;

import java.time.LocalDate;
import java.util.List;

import com.hospitalApi.employees.models.EmployeeHistory;

import lombok.Value;

@Value
public class EmployeeHistoryResponseDTO {

    HistoryTypeResponseDTO historyType;

    String commentary;

    LocalDate historyDate;
}
