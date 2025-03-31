package com.hospitalApi.reports.dtos.response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

public class ProfitSummaryDTOTest {

    private static final BigDecimal SALES = new BigDecimal("100.00");
    private static final BigDecimal COST = new BigDecimal("60.00");
    private static final BigDecimal PROFIT = new BigDecimal("40.00");

    /**
     * dado: un objeto FinancialSummaryDTO con ventas, costo y ganancia definidos.
     * cuando: se instancia ProfitSummaryDTO con dicho objeto.
     * entonces: se retorna correctamente el resumen financiero desde el getter.
     */
    @Test
    public void shouldReturnFinancialSummaryFromGetter() {
        // arrange
        FinancialSummaryDTO summary = new FinancialSummaryDTO(SALES, COST, PROFIT);
        ProfitSummaryDTO dto = new ProfitSummaryDTO(summary);

        // assert
        assertAll(
                () -> assertEquals(SALES, dto.getFinancialSummary().getTotalSales()),
                () -> assertEquals(COST, dto.getFinancialSummary().getTotalCost()),
                () -> assertEquals(PROFIT, dto.getFinancialSummary().getTotalProfit()));
    }
}
