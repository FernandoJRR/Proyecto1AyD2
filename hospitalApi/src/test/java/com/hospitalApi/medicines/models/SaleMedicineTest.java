package com.hospitalApi.medicines.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.consults.models.Consult;

@ExtendWith(MockitoExtension.class)
class SaleMedicineTest {

    private static final BigDecimal PRICE = new BigDecimal("25.00");
    private static final BigDecimal COST = new BigDecimal("10.00");
    private static final int QUANTITY = 3;
    private static final String CONSULT_ID = "2345.34562345.3456";

    private Medicine medicine;
    private Consult consult;

    @BeforeEach
    void setUp() {
        medicine = new Medicine();
        medicine.setPrice(PRICE);
        medicine.setCost(COST);
        consult = new Consult();
        consult.setId(CONSULT_ID);
    }

    /**
     * dado: un medicamento con precio y costo, y una cantidad.
     * cuando: se construye SaleMedicine usando el constructor sin consult.
     * entonces: se deben asignar correctamente los campos y calcular total y
     * profit.
     */
    @Test
    public void constructorWithoutConsultInitializesAndCalculatesCorrectly() {
        // arrange y act
        SaleMedicine saleMedicine = new SaleMedicine(medicine, QUANTITY);
        BigDecimal expectedTotal = PRICE.multiply(BigDecimal.valueOf(QUANTITY));
        BigDecimal expectedProfit = expectedTotal.subtract(COST.multiply(BigDecimal.valueOf(QUANTITY)));
        // assert
        assertAll(
                () -> assertNull(saleMedicine.getConsult()),
                () -> assertEquals(medicine, saleMedicine.getMedicine()),
                () -> assertEquals(QUANTITY, saleMedicine.getQuantity()),
                () -> assertEquals(PRICE, saleMedicine.getPrice()),
                () -> assertEquals(COST, saleMedicine.getMedicineCost()),
                () -> assertEquals(expectedTotal, saleMedicine.getTotal()),
                () -> assertEquals(expectedProfit, saleMedicine.getProfit()));
    }

    /**
     * dado: un medicamento y una consulta, y una cantidad.
     * cuando: se construye SaleMedicine usando el constructor con consult.
     * entonces: se deben asignar correctamente los campos y calcular total y
     * profit.
     */
    @Test
    public void constructorWithConsultInitializesAndCalculatesCorrectly() {
        // arrange y act
        SaleMedicine saleMedicine = new SaleMedicine(consult, medicine, QUANTITY);
        BigDecimal expectedTotal = PRICE.multiply(BigDecimal.valueOf(QUANTITY));
        BigDecimal expectedProfit = expectedTotal.subtract(COST.multiply(BigDecimal.valueOf(QUANTITY)));
        // assert
        assertAll(
                () -> assertEquals(consult, saleMedicine.getConsult()),
                () -> assertEquals(medicine, saleMedicine.getMedicine()),
                () -> assertEquals(QUANTITY, saleMedicine.getQuantity()),
                () -> assertEquals(PRICE, saleMedicine.getPrice()),
                () -> assertEquals(COST, saleMedicine.getMedicineCost()),
                () -> assertEquals(expectedTotal, saleMedicine.getTotal()),
                () -> assertEquals(expectedProfit, saleMedicine.getProfit()));
    }

    /**
     * dado: un medicamento con precio y costo definidos, y una cantidad.
     * cuando: se construye un objeto SaleMedicine con ese medicamento y cantidad.
     * entonces: se calcula correctamente el total y la ganancia (profit).
     */
    @Test
    public void saleMedicineCalculatesTotalAndProfitCorrectly() {
        // arrange y act
        SaleMedicine saleMedicine = new SaleMedicine(medicine, QUANTITY);

        BigDecimal expectedTotal = PRICE.multiply(BigDecimal.valueOf(QUANTITY));
        BigDecimal expectedProfit = expectedTotal.subtract(COST.multiply(BigDecimal.valueOf(QUANTITY)));
        // assert
        assertAll(
                () -> assertEquals(expectedTotal, saleMedicine.getTotal()),
                () -> assertEquals(expectedProfit, saleMedicine.getProfit()));
    }
}
