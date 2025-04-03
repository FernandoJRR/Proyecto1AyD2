package com.hospitalApi.reports.dtos.response.employeeSalesReport;

import java.math.BigDecimal;
import java.util.List;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.reports.dtos.response.EmployeeDtoForReport;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Getter;

@Getter
public class SalesPerEmployeeDTO extends EmployeeDtoForReport {

    private final FinancialSummaryDTO financialSummaryDTO;
    private final List<SaleMedicineResponseDTO> sales;

    public SalesPerEmployeeDTO(String employeeFullName, String cui, BigDecimal salary,
            String employeeType,
            FinancialSummaryDTO financialSummaryDTO,
            List<SaleMedicineResponseDTO> sales) {
        super(employeeFullName, cui, salary, employeeType);
        this.financialSummaryDTO = financialSummaryDTO;
        this.sales = sales;
    }

}
