package com.hospitalApi.rooms.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

public class RoomFinancialCalculatorTest {

    private RoomFinancialCalculator calculator;

    private static final BigDecimal PRICE_DAY_1 = new BigDecimal("500.00");
    private static final BigDecimal COST_DAY_1 = new BigDecimal("100.00");
    private static final int USAGE_DAYS_1 = 2;

    private static final BigDecimal PRICE_DAY_2 = new BigDecimal("400.00");
    private static final BigDecimal COST_DAY_2 = new BigDecimal("80.00");
    private static final int USAGE_DAYS_2 = 3;

    private RoomUsage usage1;
    private RoomUsage usage2;
    private List<RoomUsage> usages;

    @BeforeEach
    public void setUp() {
        calculator = new RoomFinancialCalculator();
        usage1 = new RoomUsage(
                null,
                new Room(),
                USAGE_DAYS_1,
                PRICE_DAY_1,
                COST_DAY_1);

        usage2 = new RoomUsage(
                null,
                new Room(),
                USAGE_DAYS_2,
                PRICE_DAY_2,
                COST_DAY_2);
        usages = List.of(usage1, usage2);
    }

    /**
     * dado: una lista de usos de habitación con días y precios definidos.
     * cuando: se calcula el resumen financiero.
     * entonces: se obtiene el total de ingresos, costos y ganancias correctamente.
     */
    @Test
    public void shouldCalculateRoomFinancialTotalsCorrectly() {

        // arrange
        BigDecimal expectedTotalSales = PRICE_DAY_1.multiply(BigDecimal.valueOf(USAGE_DAYS_1))
                .add(PRICE_DAY_2.multiply(BigDecimal.valueOf(USAGE_DAYS_2)));

        BigDecimal expectedTotalCost = COST_DAY_1.multiply(BigDecimal.valueOf(USAGE_DAYS_1))
                .add(COST_DAY_2.multiply(BigDecimal.valueOf(USAGE_DAYS_2)));

        BigDecimal expectedTotalProfit = expectedTotalSales.subtract(expectedTotalCost);

        // act
        FinancialSummaryDTO result = calculator.calculateFinancialTotalsOfList(usages);
        // assert
        assertAll(
                () -> assertEquals(expectedTotalSales, result.getTotalSales()),
                () -> assertEquals(expectedTotalCost, result.getTotalCost()),
                () -> assertEquals(expectedTotalProfit, result.getTotalProfit()));
    }
}
