package com.hospitalApi.medicines.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;

public class MedicineTest {

    private static final String ID = "MED-001";
    private static final String NAME = "Paracetamol";
    private static final String DESC = "Pain reliever";
    private static final Integer QTY = 50;
    private static final Integer MIN_QTY = 5;
    private static final Double PRICE = 10.0;
    private static final Double COST = 6.0;

    private Medicine medicine;

    @BeforeEach
    public void setup() {
        medicine = new Medicine(ID, NAME, DESC, QTY, MIN_QTY, PRICE, COST);
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
                () -> assertEquals(DESC, medicine.getDescription()),
                () -> assertEquals(QTY, medicine.getQuantity()),
                () -> assertEquals(MIN_QTY, medicine.getMinQuantity()),
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
        CreateMedicineRequestDTO dto = new CreateMedicineRequestDTO(NAME, DESC, QTY, MIN_QTY, PRICE, COST);
        Medicine med = new Medicine(dto);

        assertAll(
                () -> assertEquals(NAME, med.getName()),
                () -> assertEquals(DESC, med.getDescription()),
                () -> assertEquals(QTY, med.getQuantity()),
                () -> assertEquals(MIN_QTY, med.getMinQuantity()),
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
                "Ibuprofeno",
                "Anti-inflamatorio",
                100,
                10,
                12.5,
                7.5);

        medicine.updateFromDTO(dto);

        assertAll(
                () -> assertEquals("Ibuprofeno", medicine.getName()),
                () -> assertEquals("Anti-inflamatorio", medicine.getDescription()),
                () -> assertEquals(100, medicine.getQuantity()),
                () -> assertEquals(10, medicine.getMinQuantity()),
                () -> assertEquals(12.5, medicine.getPrice()),
                () -> assertEquals(7.5, medicine.getCost()));
    }

    /**
     * dado: un constructor sin ID
     * cuando: se inicializa el objeto
     * entonces: debe tener null como ID
     */
    @Test
    public void shouldCreateMedicineWithoutId() {
        Medicine med = new Medicine(NAME, DESC, QTY, MIN_QTY, PRICE, COST);

        assertAll(
                () -> assertNull(med.getId()),
                () -> assertEquals(NAME, med.getName()),
                () -> assertEquals(DESC, med.getDescription()));
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
