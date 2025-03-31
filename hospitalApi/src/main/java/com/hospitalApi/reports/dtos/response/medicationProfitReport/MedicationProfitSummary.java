package com.hospitalApi.reports.dtos.response.medicationProfitReport;

import java.util.List;

import com.hospitalApi.reports.dtos.response.ProfitSummaryDTO;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class MedicationProfitSummary extends ProfitSummaryDTO {
    private List<SalePerMedicationDTO> salePerMedication;

    public MedicationProfitSummary(FinancialSummaryDTO financialSummary, List<SalePerMedicationDTO> salePerMedication) {
        super(financialSummary);
        this.salePerMedication = salePerMedication;
    }

}
