package com.hospitalApi.reports.dtos.response.financialReport;

import java.math.BigDecimal;

import lombok.Value;

/**
 * Representa una entrada individual dentro de un reporte financiero.
 *
 * @param date        fecha del movimiento financiero.
 * @param description descripción del ingreso, gasto o ganancia.
 * @param amount      monto asociado al movimiento.
 */
@Value
public class FinancialReportEntryDTO {
    String date;
    String description;
    BigDecimal amount;
}
