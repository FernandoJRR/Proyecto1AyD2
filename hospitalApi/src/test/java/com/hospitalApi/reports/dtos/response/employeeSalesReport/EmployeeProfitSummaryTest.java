package com.hospitalApi.reports.dtos.response.employeeSalesReport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

public class EmployeeProfitSummaryTest {

    private static final BigDecimal SALES = new BigDecimal("200.00");
    private static final BigDecimal COST = new BigDecimal("120.00");
    private static final BigDecimal PROFIT = new BigDecimal("80.00");

    /**
     * dado: un resumen financiero y una lista de ventas por empleado.
     * cuando: se instancia un objeto EmployeeProfitSummary.
     * entonces: los datos se asignan correctamente y pueden recuperarse.
     */
    @Test
    public void shouldCreateEmployeeProfitSummaryAndReturnValues() {
        // arrange
        FinancialSummaryDTO summary = new FinancialSummaryDTO(SALES, COST, PROFIT);
        List<SalesPerEmployeeDTO> employeeSales = List.of();
        // act
        EmployeeProfitSummary result = new EmployeeProfitSummary(summary, employeeSales);

        // assert
        assertAll(
                () -> assertEquals(summary, result.getFinancialSummary()),
                () -> assertEquals(0, result.salePerEmployee.size()));
    }
}
