package com.hospitalApi.reports.dtos.response.employeeSalesReport;

import java.util.List;

import com.hospitalApi.reports.dtos.response.ProfitSummaryDTO;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Getter;

@Getter
public class EmployeeProfitSummary extends ProfitSummaryDTO {
    List<SalesPerEmployeeDTO> salePerEmployee;

    public EmployeeProfitSummary(FinancialSummaryDTO financialSummary, List<SalesPerEmployeeDTO> salePerMedication) {
        super(financialSummary);
        this.salePerEmployee = salePerMedication;
    }
}
