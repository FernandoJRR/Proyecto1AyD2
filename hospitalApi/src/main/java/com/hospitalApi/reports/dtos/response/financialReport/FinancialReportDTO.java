package com.hospitalApi.reports.dtos.response.financialReport;

import java.util.List;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Value;

/**
 * Representa el reporte financiero general del hospital,
 * incluyendo el resumen global y el detalle por cada área.
 *
 * @param globalFinancialSummary resumen financiero total del hospital.
 * @param financialReportPerArea lista de reportes financieros detallados por
 *                               área.
 */
@Value
public class FinancialReportDTO {
    FinancialSummaryDTO globalFinancialSummary;
    List<FinancialReportPerAreaDTO> financialReportPerArea;
}
