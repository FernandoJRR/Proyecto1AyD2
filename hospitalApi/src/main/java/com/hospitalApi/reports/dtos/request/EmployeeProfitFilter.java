package com.hospitalApi.reports.dtos.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmployeeProfitFilter {
    private final String employeeName;
    private final String employeeCUI;
}
