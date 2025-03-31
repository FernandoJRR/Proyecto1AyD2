package com.hospitalApi.reports.dtos.response.medicationProfitReport;

import java.util.List;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Value;

@Value
public class MedicationProfitSummary {
    FinancialSummaryDTO financialSummary;
    List<SalePerMedicationDTO> salePerMedication;
}
