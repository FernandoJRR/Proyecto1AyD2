package com.hospitalApi.reports.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.mappers.SaleMedicineMapper;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.reports.dtos.request.MedicationProfitFilter;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.MedicationProfitSummary;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.SalePerMedicationDTO;
import com.hospitalApi.reports.ports.ReportService;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationProfitReportService implements ReportService<MedicationProfitSummary, MedicationProfitFilter> {

    private final ForSaleMedicinePort forSaleMedicinePort;
    private final FinancialCalculator<FinancialSummaryDTO, SaleMedicine> financialCalculator;
    private final SaleMedicineMapper saleMedicineMapper;

    /**
     * Genera un reporte de gananncias por medicamento en base al nombre del
     * medicamento
     * 
     * @param name nombre del medicamento, si esta vacio se traen todos los
     *             medicamentos
     * @return lista de medicamentos
     */
    @Override
    public MedicationProfitSummary generateReport(MedicationProfitFilter filter) {
        // mandamos atraer todas las ventas que se hicieron en las fechas establecidas
        // la query maneja el filtro de nombre del medicamento y las fechas
        List<SaleMedicine> salesMedicines = forSaleMedicinePort.getSalesMedicineBetweenDatesAndMedicineName(
                filter.getStartDate(), filter.getEndDate(),
                filter.getMedicationName());

        // juntamos los registros por nombre de medicamento el cual es unico en la bd
        Map<String, List<SaleMedicine>> groupedSalesByMedicineName = groupSalesByMedicineName(salesMedicines);

        // Construir DTOs por medicamento
        List<SalePerMedicationDTO> salesPerMedication = buildSalesPerMedicationReport(groupedSalesByMedicineName);

        // mandamos a traer los totales de todas las ventas
        FinancialSummaryDTO globalFinancialSummary = financialCalculator.calculateFinancialTotalsOfList(salesMedicines);
        // construimos nuestra respuesta final

        return new MedicationProfitSummary(globalFinancialSummary, salesPerMedication);
    }

    private Map<String, List<SaleMedicine>> groupSalesByMedicineName(List<SaleMedicine> medicationSales) {
        //con un stream usamo collectors para poder agrupar por el nombre de la medicina
        Map<String, List<SaleMedicine>> groupedSalesByMedicineName = 
        medicationSales.stream().collect(Collectors.groupingBy(sale-> sale.getMedicine().getName()));
        return groupedSalesByMedicineName;
    }

    private List<SalePerMedicationDTO> buildSalesPerMedicationReport(
            Map<String, List<SaleMedicine>> groupedMedicationSalesByMedicineName) {
        // aqui guardaremos la lista de todas las ventas por meditcamentos
        List<SalePerMedicationDTO> salesPerMedication = new ArrayList<>();

        // recorremos todas las ocurrencias del mapa
        for (Entry<String, List<SaleMedicine>> medicationWithSales : groupedMedicationSalesByMedicineName.entrySet()) {
            // la key es el nombre del medicamentos
            String medicineName = medicationWithSales.getKey();

            // el valor de la entrada es la lista de ventas del
            // medicamentos
            List<SaleMedicine> sales = medicationWithSales.getValue();

            // ahora lo que vamos a hacer es mandar a calcular el sumary de todas las ventas
            // de este
            FinancialSummaryDTO financialSummary = financialCalculator.calculateFinancialTotalsOfList(sales);

            // convertimos las ventas del medicamento en
            List<SaleMedicineResponseDTO> salesDTO = saleMedicineMapper
                    .fromSaleMedicineListToSaleMedicineDTOList(sales);

            // creamos nuestro dto con la info recabada y la agreamos a nuestro condensado
            SalePerMedicationDTO dto = new SalePerMedicationDTO(medicineName, financialSummary, salesDTO);
            salesPerMedication.add(dto);
        }

        return salesPerMedication;
    }

}
