package com.hospitalApi.reports.dtos.response.medicationProfitReport;

import java.util.List;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Value;

@Value
public class SalePerMedicationDTO {

    String medicationName;
    FinancialSummaryDTO financialSummaryDTO;
    List<SaleMedicineResponseDTO> sales;
}
