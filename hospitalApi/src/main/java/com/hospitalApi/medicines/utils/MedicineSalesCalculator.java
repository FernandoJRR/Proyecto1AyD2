package com.hospitalApi.medicines.utils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.shared.FinancialCalculator;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

@Component
public class MedicineSalesCalculator implements FinancialCalculator<FinancialSummaryDTO, List<SaleMedicine>> {

    /**
     * Recorre cada uno de los elementos de la lista,
     * al tratarse de SaleMedicine va obteniendo su
     * total, medicineCost y profit y los va sumando.
     *
     * @param sales lista de ventas de medicamentos
     * @return resumen financiero con totales sumados
     */
    @Override
    public FinancialSummaryDTO calculateFinancialTotals(List<SaleMedicine> sales) {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (SaleMedicine sale : sales) {
            totalSales = totalSales.add(sale.getTotal());
            totalCost = totalCost.add(sale.getMedicineCost());
            totalProfit = totalProfit.add(sale.getProfit());
        }
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }
}
