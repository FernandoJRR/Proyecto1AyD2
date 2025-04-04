package com.hospitalApi.rooms.utils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;

@Component
public class RoomFinancialCalculator implements FinancialCalculator<FinancialSummaryDTO, RoomUsage> {

    /**
     * Calcula el resumen financiero de una lista de usos de habitaciones.
     * Suma los ingresos y costos diarios multiplicados por los días de uso.
     * La ganancia se obtiene como la diferencia entre ingresos y costos.
     *
     * @param financialMoves lista de usos de habitación a procesar.
     * @return resumen financiero con ingresos, costos y ganancia total.
     */
    @Override
    public FinancialSummaryDTO calculateFinancialTotalsOfList(List<RoomUsage> financialMoves) {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (RoomUsage roomUsage : financialMoves) {
            FinancialSummaryDTO summary = calculateFinancialTotals(roomUsage);
            // vamos sumando los resultados de cada resumen
            totalSales = totalSales
                    .add(summary.getTotalSales());
            totalCost = totalCost
                    .add(summary.getTotalCost());
            totalProfit = totalProfit.add(summary.getTotalProfit());
        }
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

    @Override
    public FinancialSummaryDTO calculateFinancialTotals(RoomUsage roomUsage) {
        // El total de la venta es la multiplicacion de los dias de uso con el precio
        // diario
        // al que se dio la habitacion
        BigDecimal totalSales = roomUsage.getDailyRoomPrice()
                .multiply(new BigDecimal(roomUsage.getUsageDays()));
        // El total del costo es la multiplicacion de los dias de uso con el costo
        // diario
        // al que se dio la habitacion
        BigDecimal totalCost = roomUsage.getDailyRoomMaintenanceCost()
                .multiply(new BigDecimal(roomUsage.getUsageDays()));
        // la ganancia es la vetna menos el costo
        BigDecimal totalProfit = totalSales.subtract(totalCost);
        return new FinancialSummaryDTO(totalSales, totalCost, totalProfit);
    }

}
