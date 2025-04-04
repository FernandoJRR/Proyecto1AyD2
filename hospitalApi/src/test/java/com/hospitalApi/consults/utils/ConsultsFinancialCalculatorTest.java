package com.hospitalApi.consults.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

public class ConsultsFinancialCalculatorTest {

    private ConsultFinancialCalculator calculator;

    private static final BigDecimal CONSULT_COST_1 = new BigDecimal(250);
    private static final BigDecimal CONSULT_COST_2 = new BigDecimal(350);

    private Consult consult1;
    private Consult consult2;
    private List<Consult> consults;

    @BeforeEach
    public void setUp() {
        calculator = new ConsultFinancialCalculator();

        consult1 = new Consult();
        consult1.setCostoConsulta(CONSULT_COST_1);

        consult2 = new Consult();
        consult2.setCostoConsulta(CONSULT_COST_2);

        consults = List.of(consult1, consult2);
    }

    /**
     * dado: una lista de consultas con costos definidos.
     * cuando: se calcula el resumen financiero.
     * entonces: el total de ventas es la suma de los costos, y los totales de costo
     * y ganancia son cero.
     */
    @Test
    public void shouldCalculateTotalSalesFromConsultsCorrectly() {
        // act
        FinancialSummaryDTO result = calculator.calculateFinancialTotalsOfList(consults);
        // el metodo deberia sumar esto
        BigDecimal expectedTotalSales = new BigDecimal(600);
        BigDecimal expectedProfit = new BigDecimal(600);
        BigDecimal expectedCost = BigDecimal.ZERO;
        // assert
        assertAll(
                () -> assertEquals(expectedTotalSales, result.getTotalSales()),
                () -> assertEquals(expectedCost, result.getTotalCost()),
                () -> assertEquals(expectedProfit, result.getTotalProfit()));
    }
}
