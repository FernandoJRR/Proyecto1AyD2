package com.hospitalApi.reports.dtos.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.hospitalApi.reports.enums.FinancialReportArea;
import com.hospitalApi.reports.enums.FinancialReportType;

public class FinancialFilterTest {
    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final FinancialReportType REPORT_TYPE = FinancialReportType.INCOME;
    private static final FinancialReportArea AREA = FinancialReportArea.CONSULTS;

    /**
     * dado: un rango de fechas, tipo de reporte y área válidos.
     * cuando: se crea una instancia de `FinancialFilter`.
     * entonces: los valores se asignan correctamente y los getters los devuelven
     * sin errores.
     */
    @Test
    void shouldCreateFinancialFilterWithCorrectValues() {
        FinancialFilter filter = new FinancialFilter(START_DATE, END_DATE, REPORT_TYPE, AREA);

        assertAll(
                () -> assertEquals(START_DATE, filter.getStartDate()),
                () -> assertEquals(END_DATE, filter.getEndDate()),
                () -> assertEquals(REPORT_TYPE, filter.getReportType()),
                () -> assertEquals(AREA, filter.getArea()));
    }
}