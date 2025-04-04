package com.hospitalApi.surgery.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.surgery.models.Surgery;

public class SurgeryFinancialCalculatorTest {

    private SurgeryFinancialCalculator calculator;

    private static final BigDecimal SURGERY_COST_1 = new BigDecimal(1200);
    private static final BigDecimal HOSPITAL_COST_1 = new BigDecimal(500);

    private static final BigDecimal SURGERY_COST_2 = new BigDecimal(1500);
    private static final BigDecimal HOSPITAL_COST_2 = new BigDecimal(600);

    private Surgery surgery1;
    private Surgery surgery2;
    private List<Surgery> surgeries;

    @BeforeEach
    public void setUp() {
        calculator = new SurgeryFinancialCalculator();
        surgery1 = new Surgery(
                null,
                null,
                HOSPITAL_COST_1,
                SURGERY_COST_1);

        surgery2 = new Surgery(
                null,
                null,
                HOSPITAL_COST_2,
                SURGERY_COST_2);

        surgeries = List.of(surgery1, surgery2);
    }

    /**
     * dado: una lista de cirugías con precios y costos definidos.
     * cuando: se calcula el resumen financiero.
     * entonces: se obtiene el total de ingresos, costos y ganancias correctamente.
     */
    @Test
    public void shouldCalculateSurgeryFinancialTotalsCorrectly() {
        // arrange
        BigDecimal expectedTotalSales = SURGERY_COST_1.add(SURGERY_COST_2);
        BigDecimal expectedTotalCost = HOSPITAL_COST_1.add(HOSPITAL_COST_2);
        BigDecimal expectedTotalProfit = expectedTotalSales.subtract(expectedTotalCost);

        // act
        FinancialSummaryDTO result = calculator.calculateFinancialTotalsOfList(surgeries);

        // assert
        assertAll(
                () -> assertEquals(expectedTotalSales, result.getTotalSales()),
                () -> assertEquals(expectedTotalCost, result.getTotalCost()),
                () -> assertEquals(expectedTotalProfit, result.getTotalProfit()));
    }
}