package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;

import com.hospitalApi.reports.enums.FinancialReportArea;
import com.hospitalApi.reports.enums.FinancialReportType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * Filtro para generar reportes financieros, permitiendo filtrar por tipo de
 * reporte y área.
 *
 * @param startDate  fecha de inicio del período a filtrar.
 * @param endDate    fecha de fin del período a filtrar.
 * @param reportType tipo de reporte financiero (ingresos, egresos, ganancias).
 * @param area       área del hospital a la que pertenece el reporte financiero.
 */
@Getter
public class FinancialFilter extends PeriodFilter {
    @NotNull(message = "El tipo de reporte no puede ser nulo")
    private final FinancialReportType reportType;

    @NotNull(message = "El área no puede ser nulo")
    private final FinancialReportArea area;

    public FinancialFilter(LocalDate startDate, LocalDate endDate, FinancialReportType reportType,
            FinancialReportArea area) {
        super(startDate, endDate);
        this.reportType = reportType;
        this.area = area;
    }

}
