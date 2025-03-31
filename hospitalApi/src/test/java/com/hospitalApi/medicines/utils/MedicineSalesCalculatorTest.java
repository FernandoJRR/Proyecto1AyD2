package com.hospitalApi.medicines.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

@ExtendWith(MockitoExtension.class)
class MedicineSalesCalculatorTest {

    private static final BigDecimal PRICE = new BigDecimal("20.00");
    private static final BigDecimal COST = new BigDecimal("7.00");
    private static final int QUANTITY = 2;

    private Medicine medicine;
    private SaleMedicine sale1;
    private SaleMedicine sale2;
    private List<SaleMedicine> sales;

    private final MedicineSalesCalculator calculator = new MedicineSalesCalculator();

    @BeforeEach
    public void setUp() {
        medicine = new Medicine();
        medicine.setPrice(PRICE);
        medicine.setCost(COST);

        // estos constructores ya hacen las operaciones matematicas para el calculo del
        // profit y el
        sale1 = new SaleMedicine(medicine, QUANTITY);
        sale2 = new SaleMedicine(medicine, QUANTITY);

        sales = List.of(sale1, sale2);
    }

    /**
     * dado: una lista de objetos SaleMedicine con precio, costo y cantidad
     * definidos.
     * cuando: se calcula el resumen financiero.
     * entonces: se suman correctamente total, costo y profit.
     */
    @Test
    public void calculateTotalsReturnsCorrectValuesRespectingLogic() {
        // arrange

        BigDecimal expectedSales = sale1.getTotal().add(sale2.getTotal());
        BigDecimal expectedCost = sale1.getMedicineCost().add(sale2.getMedicineCost());
        BigDecimal expectedProfit = sale1.getProfit().add(sale2.getProfit());

        // act
        FinancialSummaryDTO result = calculator.calculateFinancialTotals(sales);

        // asseert
        assertAll(
                () -> assertEquals(expectedSales, result.getTotalSales()),
                () -> assertEquals(expectedCost, result.getTotalCost()),
                () -> assertEquals(expectedProfit, result.getTotalProfit()));
    }
}
