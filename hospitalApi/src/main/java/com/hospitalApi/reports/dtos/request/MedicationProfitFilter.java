package com.hospitalApi.reports.dtos.request;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class MedicationProfitFilter extends MedicationReportFilter {

    public MedicationProfitFilter(String medicationName, LocalDate startDate, LocalDate endDate) {
        super(startDate, endDate, medicationName);
    }
}
