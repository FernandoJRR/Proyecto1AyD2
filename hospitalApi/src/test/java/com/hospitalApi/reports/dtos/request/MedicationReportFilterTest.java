package com.hospitalApi.reports.dtos.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class MedicationReportFilterTest {

    private static final String MEDICATION_NAME = "Paracetamol";
    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);

    /**
     * dado: un nombre de medicamento y un rango de fechas.
     * cuando: se crea una instancia de `MedicationProfitFilter` con estos valores.
     * entonces: los valores se asignan correctamente y los getters devuelven lo
     * esperado.
     */
    @Test
    void shouldCreateProfitFilterWithCorrectValues() {
        MedicationProfitFilter filter = new MedicationProfitFilter(
                MEDICATION_NAME,
                START_DATE,
                END_DATE);

        assertAll(
                () -> assertEquals(MEDICATION_NAME, filter.getMedicationName()),
                () -> assertEquals(START_DATE, filter.getStartDate()),
                () -> assertEquals(END_DATE, filter.getEndDate()));
    }
}
