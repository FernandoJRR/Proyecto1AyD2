package com.hospitalApi.reports.dtos.response.medicationProfitReport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

public class MedicationProfitSummaryTest {

    private static final BigDecimal SALES = new BigDecimal("150.00");
    private static final BigDecimal COST = new BigDecimal("80.00");
    private static final BigDecimal PROFIT = new BigDecimal("70.00");

    /**
     * dado: un resumen financiero y una lista de ventas por medicamento.
     * cuando: se instancia un objeto MedicationProfitSummary.
     * entonces: los valores son accesibles mediante sus getters.
     */
    @Test
    public void shouldCreateMedicationProfitSummaryAndReturnValues() {
        // arrange
        FinancialSummaryDTO financialSummary = new FinancialSummaryDTO(SALES, COST, PROFIT);

        List<SalePerMedicationDTO> salesList = List.of();
        // act
        MedicationProfitSummary summary = new MedicationProfitSummary(financialSummary, salesList);

        // assert
        assertAll(
                () -> assertEquals(financialSummary, summary.getFinancialSummary()),
                () -> assertEquals(0, summary.getSalePerMedication().size()));
    }
}
