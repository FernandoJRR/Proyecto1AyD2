package com.hospitalApi.medicines.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;

public class MedicineTest {

    private static final String ID = "MED-001";
    private static final String NAME = "Paracetamol";
    private static final String DESCRIPTION = "Pain reliever";
    private static final Integer QUANTITY = 50;
    private static final Integer MIN_QUANTITY = 5;
    private static final BigDecimal PRICE = BigDecimal.valueOf(10.0);
    private static final BigDecimal COST = BigDecimal.valueOf(6.0);

    private static final String MEDICINE_NAME_UPDATED = "Ibuprofeno";
    private static final String MEDICINE_DESCRIPTION_UPDATED = "Anti-inflamatorio";
    private static final int MEDICINE_QUANTITY_UPDATED = 100;
    private static final int MEDICINE_MIN_QUANTITY_UPDATED = 10;
    private static final BigDecimal MEDICINE_PRICE_UPDATED = BigDecimal.valueOf(12.5);
    private static final BigDecimal MEDICINE_COST_UPDATED = BigDecimal.valueOf(7.5);

    private Medicine medicine;

    @BeforeEach
    public void setup() {
        medicine = new Medicine(ID, NAME, DESCRIPTION, QUANTITY, MIN_QUANTITY, PRICE, COST);
    }

    /**
     * dado: datos completos de un medicamento
     * cuando: se construye desde el constructor principal con id
     * entonces: los atributos deben coincidir
     */
    @Test
    public void shouldCreateMedicineWithAllFields() {
        assertAll(
                () -> assertEquals(ID, medicine.getId()),
                () -> assertEquals(NAME, medicine.getName()),
                () -> assertEquals(DESCRIPTION, medicine.getDescription()),
                () -> assertEquals(QUANTITY, medicine.getQuantity()),
                () -> assertEquals(MIN_QUANTITY, medicine.getMinQuantity()),
                () -> assertEquals(PRICE, medicine.getPrice()),
                () -> assertEquals(COST, medicine.getCost()));
    }

    /**
     * dado: una instancia de CreateMedicineRequestDTO
     * cuando: se construye el objeto Medicine
     * entonces: los valores deben reflejar los del DTO
     */
    @Test
    public void shouldCreateMedicineFromCreateDTO() {
        CreateMedicineRequestDTO dto = new CreateMedicineRequestDTO(NAME, DESCRIPTION, QUANTITY, MIN_QUANTITY, PRICE, COST);
        Medicine med = new Medicine(dto);

        assertAll(
                () -> assertEquals(NAME, med.getName()),
                () -> assertEquals(DESCRIPTION, med.getDescription()),
                () -> assertEquals(QUANTITY, med.getQuantity()),
                () -> assertEquals(MIN_QUANTITY, med.getMinQuantity()),
                () -> assertEquals(PRICE, med.getPrice()),
                () -> assertEquals(COST, med.getCost()));
    }

    /**
     * dado: un DTO con nuevos valores
     * cuando: se actualiza el objeto Medicine
     * entonces: los valores del objeto deben cambiar
     */

    @Test
    public void shouldUpdateMedicineFromUpdateDTO() {
        UpdateMedicineRequestDTO dto = new UpdateMedicineRequestDTO(
                MEDICINE_NAME_UPDATED,
                MEDICINE_DESCRIPTION_UPDATED,
                MEDICINE_QUANTITY_UPDATED,
                MEDICINE_MIN_QUANTITY_UPDATED,
                MEDICINE_PRICE_UPDATED,
                MEDICINE_COST_UPDATED);

        medicine.updateFromDTO(dto);

        assertAll(
                () -> assertEquals(MEDICINE_NAME_UPDATED, medicine.getName()),
                () -> assertEquals(MEDICINE_DESCRIPTION_UPDATED, medicine.getDescription()),
                () -> assertEquals(MEDICINE_QUANTITY_UPDATED, medicine.getQuantity()),
                () -> assertEquals(MEDICINE_MIN_QUANTITY_UPDATED, medicine.getMinQuantity()),
                () -> assertEquals(MEDICINE_PRICE_UPDATED, medicine.getPrice()),
                () -> assertEquals(MEDICINE_COST_UPDATED, medicine.getCost()));
    }

    /**
     * dado: un constructor sin ID
     * cuando: se inicializa el objeto
     * entonces: debe tener null como ID
     */
    @Test
    public void shouldCreateMedicineWithoutId() {
        Medicine med = new Medicine(NAME, DESCRIPTION, QUANTITY, MIN_QUANTITY, PRICE, COST);

        assertAll(
                () -> assertNull(med.getId()),
                () -> assertEquals(NAME, med.getName()),
                () -> assertEquals(DESCRIPTION, med.getDescription()));
    }

    /**
     * dado: un objeto creado con constructor vacío
     * cuando: se inicializa
     * entonces: todos los campos deben ser nulos o por defecto
     */
    @Test
    public void shouldCreateEmptyMedicine() {
        Medicine emptyMed = new Medicine();

        assertAll(
                () -> assertNull(emptyMed.getId()),
                () -> assertNull(emptyMed.getName()),
                () -> assertNull(emptyMed.getDescription()),
                () -> assertNull(emptyMed.getQuantity()),
                () -> assertNull(emptyMed.getMinQuantity()),
                () -> assertNull(emptyMed.getPrice()),
                () -> assertNull(emptyMed.getCost()),
                () -> assertNull(emptyMed.getSaleMedicines()));
    }
}
