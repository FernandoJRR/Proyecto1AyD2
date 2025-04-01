package com.hospitalApi.reports.dtos.response;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

import lombok.Getter;

@Getter
public class ProfitSummaryDTO {

    private FinancialSummaryDTO financialSummary;

    public ProfitSummaryDTO(FinancialSummaryDTO financialSummary) {
        this.financialSummary = financialSummary;
    }
}
