package com.hospitalApi.reports.dtos.response.employeeSalesReport;

import java.util.List;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Value;

@Value
public class SalesPerEmployeeDTO {

    String employeeFullName;
    String cui;
    FinancialSummaryDTO financialSummaryDTO;
    List<SaleMedicineResponseDTO> sales;
}
