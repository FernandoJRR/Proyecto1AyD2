package com.hospitalApi.shared.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DateFormatterUtilTest {

    private static final DateFormatterUtil dateUtils = new DateFormatterUtil();

    private static final LocalDate DATO_MOCK = LocalDate.of(2025, 3, 31);

    private static final String DATE_FORMATTED = "31/03/2025";

    /**
     * dado: una fecha válida.
     * cuando: se formatea con el método `formatDateToLocalFormat`.
     * entonces: se devuelve la fecha en formato "dd/MM/yyyy".
     */
    @Test
    public void shouldFormatDateToLocalFormatCorrectly() {
        // Act
        String formatted = dateUtils.formatDateToLocalFormat(DATO_MOCK);
        // Assert
        assertEquals(DATE_FORMATTED, formatted);
    }
}
