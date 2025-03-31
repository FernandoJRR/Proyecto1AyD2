package com.hospitalApi.reports.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.reports.dtos.request.MedicationProfitFilter;

@ExtendWith(MockitoExtension.class)
public class MedicationProfitFilterTest {

    private final String MEDICATION_NAME = "Aspirina";
    private final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private final LocalDate END_DATE = LocalDate.of(2023, 12, 31);

    /**
     * dado: un nombre de medicamento y un rango de fechas.
     * cuando: se construye un objeto `MedicationProfitFilter` con dichos valores.
     * entonces: los getters del filtro deben devolver los mismos valores con los
     * que fue construido.
     */
    @Test
    public void testMedicationProfitFilter() {
        // Act Y ARRANGE
        MedicationProfitFilter filter = new MedicationProfitFilter(MEDICATION_NAME, START_DATE, END_DATE);

        // Assert
        assertEquals(MEDICATION_NAME, filter.getMedicationName());
        assertEquals(START_DATE, filter.getStartDate());
        assertEquals(END_DATE, filter.getEndDate());
    }
}
