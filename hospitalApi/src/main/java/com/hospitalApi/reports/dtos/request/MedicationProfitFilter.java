package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class MedicationProfitFilter extends MedicationReportFilter {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public MedicationProfitFilter(String medicationName, LocalDate startDate, LocalDate endDate) {
        super(medicationName);
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
