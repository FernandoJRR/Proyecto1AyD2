package com.hospitalApi.reports.dtos.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class EmployeeLifecycleFilterTest {

    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final String EMPLOYEE_TYPE_ID = "jgf-sdffv-sdf";
    private static final List<String> HISTORY_TYPE_IDS = List.of("asddsf-sfsf-sdfsdf", "adasd-asdasd-asd");

    /**
     * dado: un rango de fechas, un tipo de empleado y una lista de tipos de
     * historial.
     * cuando: se crea una instancia de `EmployeeLifecycleFilter` con estos valores.
     * entonces: los valores se asignan correctamente y los getters devuelven lo
     * esperado.
     */ 
    @Test
    public void shouldCreateFilterWithCorrectValues() {
        EmployeeLifecycleFilter filter = new EmployeeLifecycleFilter(
                START_DATE,
                END_DATE,
                EMPLOYEE_TYPE_ID,
                HISTORY_TYPE_IDS);

        assertAll(
                () -> assertEquals(START_DATE, filter.getStartDate()),
                () -> assertEquals(END_DATE, filter.getEndDate()),
                () -> assertEquals(EMPLOYEE_TYPE_ID, filter.getEmployeeTypeId()),
                () -> assertEquals(HISTORY_TYPE_IDS, filter.getHistoryTypeIds()));
    }

}
