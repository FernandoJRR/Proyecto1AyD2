package com.hospitalApi.reports.dtos.response.financialReport;

import java.util.List;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Value;

/**
 * Representa el reporte financiero de una sola área del hospital,
 * incluyendo un resumen financiero y el detalle de cada entrada.
 *
 * @param financialSummary resumen financiero (ingresos, egresos, ganancias) del
 *                         área.
 * @param area             nombre del área analisado.
 * @param entries          lista de movimientos financieros en el área.
 */
@Value
public class FinancialReportPerAreaDTO {
    FinancialSummaryDTO financialSummary;
    String area;
    List<FinancialReportEntryDTO> entries;
}
