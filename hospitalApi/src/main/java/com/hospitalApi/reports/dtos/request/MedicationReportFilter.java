package com.hospitalApi.reports.dtos.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Esta es una clase que representa los filtros aplicables a los reportes.
 */
@Getter
@RequiredArgsConstructor
public class MedicationReportFilter {

    private final String medicationName;
}
