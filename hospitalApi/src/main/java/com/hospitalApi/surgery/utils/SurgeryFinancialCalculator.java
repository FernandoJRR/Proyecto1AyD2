package com.hospitalApi.surgery.utils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;
import com.hospitalApi.surgery.models.Surgery;

@Component
public class SurgeryFinancialCalculator implements FinancialCalculator<FinancialSummaryDTO, Surgery> {

    @Override
    public FinancialSummaryDTO calculateFinancialTotalsOfList(List<Surgery> financialMoves) {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Surgery surgery : financialMoves) {
            FinancialSummaryDTO summary = calculateFinancialTotals(surgery);
            // vamos sumando el precio de la sirugia
            totalSales = totalSales
                    .add(summary.getTotalSales());
            // vamos sumando el costo que representa al hospital
            totalCost = totalCost
                    .add(summary.getTotalCost());
            totalProfit = totalProfit.add(summary.getTotalProfit());
        }

        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

    @Override
    public FinancialSummaryDTO calculateFinancialTotals(Surgery surgery) {
        BigDecimal totalSales = surgery.getSurgeryCost();
        BigDecimal totalCost = surgery.getHospitalCost();
        // el profi pues es la resta de el total de ingreso menos el total de gasto
        BigDecimal totalProfit = totalSales.subtract(totalCost);
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

}
