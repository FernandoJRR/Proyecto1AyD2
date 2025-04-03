package com.hospitalApi.reports.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.mappers.SaleMedicineMapper;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.reports.dtos.request.EmployeeProfitFilter;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.EmployeeProfitSummary;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.SalesPerEmployeeDTO;
import com.hospitalApi.reports.ports.ReportService;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeSalesReportService implements ReportService<EmployeeProfitSummary, EmployeeProfitFilter> {

    private final ForSaleMedicinePort forSaleMedicinePort;
    private final FinancialCalculator<FinancialSummaryDTO, List<SaleMedicine>> financialCalculator;
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
    public EmployeeProfitSummary generateReport(EmployeeProfitFilter filter) {
        // mandamos atraer todas las ventas
        List<SaleMedicine> salesMedicines = forSaleMedicinePort.getSalesMedicineByEmployeeNameAndCui(
                filter.getEmployeeName(), filter.getEmployeeCUI());

        // juntamos los registros por nombre de medicamento el cual es unico en la bd
        Map<String, List<SaleMedicine>> groupedSalesByEmployeeId = groupSalesByEmployeeid(salesMedicines);

        // Construir DTOs por medicamento
        List<SalesPerEmployeeDTO> salesPerMedication = buildSalesPerEmployeeReport(groupedSalesByEmployeeId);

        // mandamos a traer los totales de todas las ventas
        FinancialSummaryDTO globalFinancialSummary = financialCalculator.calculateFinancialTotals(salesMedicines);
        // construimos nuestra respuesta final

        return new EmployeeProfitSummary(globalFinancialSummary, salesPerMedication);
    }

    private Map<String, List<SaleMedicine>> groupSalesByEmployeeid(List<SaleMedicine> medicationSales) {
        // con un stream usamo collectors para poder agrupar por id del empleado que
        // hizo la venta
        Map<String, List<SaleMedicine>> groupedSalesByEmployeeId = medicationSales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getEmployee().getId()));
        return groupedSalesByEmployeeId;
    }

    private List<SalesPerEmployeeDTO> buildSalesPerEmployeeReport(Map<String, List<SaleMedicine>> groupedSales) {
        List<SalesPerEmployeeDTO> report = new ArrayList<>();

        for (Map.Entry<String, List<SaleMedicine>> entry : groupedSales.entrySet()) {
            // obtenemos el value que es el listado de todas las ventas del empleado
            List<SaleMedicine> sales = entry.getValue();
            // obtneemos el primer registro de las ventas y extraemos el empleado (todos
            // comparten el mismo)
            Employee employee = sales.get(0).getEmployee();
            // mandamos a calcular el total en ventas que hizo este empleado
            FinancialSummaryDTO financialSummary = financialCalculator.calculateFinancialTotals(sales);
            // mandamos a convertir las ventas a dtos
            List<SaleMedicineResponseDTO> salesDTO = saleMedicineMapper
                    .fromSaleMedicineListToSaleMedicineDTOList(sales);

            SalesPerEmployeeDTO dto = new SalesPerEmployeeDTO(
                    employee.getFullName(),
                    employee.getCui(),
                    employee.getSalary(),
                    employee.getEmployeeType().getName(),
                    financialSummary,
                    salesDTO);

            report.add(dto);
        }

        return report;
    }
}
