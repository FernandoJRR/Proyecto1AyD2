package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Value;

@Value
public class EmployeeLifecycleFilter {
    LocalDate startDate;
    LocalDate endDate;
    String employeeTypeId;
    List<String> historyTypeIds;
}
