package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;

/**
 * Filtro para generar reportes del ciclo de vida del empleado,
 * basado en un rango de fechas, tipo de empleado y tipos de historial.
 *
 * @param startDate      fecha de inicio del período a filtrar.
 * @param endDate        fecha de fin del período a filtrar.
 * @param employeeTypeId identificador del tipo de empleado.
 * @param historyTypeIds lista de identificadores de tipos de historial a
 *                       incluir.
 */
@Getter
public class EmployeeLifecycleFilter extends PeriodFilter {

    private final String employeeTypeId;
    private final List<String> historyTypeIds;

    public EmployeeLifecycleFilter(LocalDate startDate, LocalDate endDate, String employeeTypeId,
            List<String> historyTypeIds) {
        super(startDate, endDate);
        this.employeeTypeId = employeeTypeId;
        this.historyTypeIds = historyTypeIds;
    }

}
