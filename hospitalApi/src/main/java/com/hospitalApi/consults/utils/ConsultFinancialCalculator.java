package com.hospitalApi.consults.utils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;

@Component
public class ConsultFinancialCalculator implements FinancialCalculator<FinancialSummaryDTO, Consult> {

    /**
     * Calcula el resumen financiero a partir de una lista de consultas - deberian
     * ser ya pagadas.
     * Suma el costo de cada consulta como venta y ganancia total.
     * El costo operativo se considera cero.
     *
     * @param financialMoves lista de consultas a procesar.
     * @return resumen financiero con ventas, costo y ganancia totales.
     */
    @Override
    public FinancialSummaryDTO calculateFinancialTotalsOfList(List<Consult> financialMoves) {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        // para cada una de las consultas vamos sumando su total en ventas que es el
        // costo o procio de la consulta
        for (Consult consultSale : financialMoves) {
            totalSales = totalSales.add(consultSale.getCostoConsulta());

        }
        // la ganancia siempre sera el total de ventas porque bajo nuesta logica
        // laconsulta no tiene costos asociados
        totalProfit = totalSales;
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

    @Override
    public FinancialSummaryDTO calculateFinancialTotals(Consult consultSale) {
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalSales = consultSale.getCostoConsulta();
        BigDecimal totalProfit = totalSales;
        // la ganancia siempre sera el total de ventas porque bajo nuesta logica
        // laconsulta no tiene costos asociados
        totalProfit = totalSales;
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

}
