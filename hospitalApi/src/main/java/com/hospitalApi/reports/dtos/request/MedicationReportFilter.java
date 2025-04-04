package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;

import lombok.Getter;

/**
 * Esta es una clase que representa los filtros aplicables a los reportes.
 */
@Getter
public class MedicationReportFilter extends PeriodFilter {

    private final String medicationName;

    public MedicationReportFilter(LocalDate startDate, LocalDate endDate, String medicationName) {
        super(startDate, endDate);
        this.medicationName = medicationName;
    }

}
