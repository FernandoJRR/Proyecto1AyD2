package com.hospitalApi.vacations.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class VacationTest {

    private static final LocalDate BEGIN_DATE = LocalDate.of(2025, 4, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 4, 10);

    private Vacations vacations;

    /**
     * dado: un objeto Vacations creado con el constructor de fechas.
     * cuando: se obtienen las fechas de inicio y fin.
     * entonces: deben coincidir con las fechas proporcionadas.
     */
    @Test
    void shouldSetDatesFromConstructor() {
        // arrange y act
        vacations = new Vacations(BEGIN_DATE, END_DATE);
        // assert
        assertAll(() -> assertEquals(BEGIN_DATE, vacations.getBeginDate()),
                () -> assertEquals(END_DATE, vacations.getEndDate()));

    }
}
