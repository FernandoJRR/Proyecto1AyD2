package com.hospitalApi.medicines.utils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;

@Component
public class MedicineSalesCalculator implements FinancialCalculator<FinancialSummaryDTO, SaleMedicine> {

    /**
     * Recorre cada uno de los elementos de la lista,
     * al tratarse de SaleMedicine va obteniendo su
     * total, medicineCost y profit y los va sumando.
     *
     * @param sales lista de ventas de medicamentos
     * @return resumen financiero con totales sumados
     */
    @Override
    public FinancialSummaryDTO calculateFinancialTotalsOfList(List<SaleMedicine> sales) {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (SaleMedicine sale : sales) {
            FinancialSummaryDTO summary = calculateFinancialTotals(sale);
            totalSales = totalSales.add(summary.getTotalSales());
            totalCost = totalCost.add(summary.getTotalCost());
            totalProfit = totalProfit.add(summary.getTotalProfit());
        }
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

    @Override
    public FinancialSummaryDTO calculateFinancialTotals(SaleMedicine sale) {
        BigDecimal totalSales = sale.getTotal();
        BigDecimal totalCost = sale.getMedicineCost().multiply(BigDecimal.valueOf(sale.getQuantity()));
        BigDecimal totalProfit = sale.getProfit();
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }
}
