package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Filtro base para reportes que requieren un intervalo de tiempo.
 *
 * @param startDate fecha de inicio del período a filtrar.
 * @param endDate   fecha de fin del período a filtrar.
 */
@Getter
@RequiredArgsConstructor
public class PeriodFilter {

    private final LocalDate startDate;
    private final LocalDate endDate;
}
