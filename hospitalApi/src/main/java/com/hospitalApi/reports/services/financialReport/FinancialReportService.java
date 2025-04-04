package com.hospitalApi.reports.services.financialReport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.reports.dtos.request.FinancialFilter;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportDTO;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportEntryDTO;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportPerAreaDTO;
import com.hospitalApi.reports.enums.FinancialReportArea;
import com.hospitalApi.reports.enums.FinancialReportType;
import com.hospitalApi.reports.ports.EntryBuilder;
import com.hospitalApi.reports.ports.ReportService;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.FinancialCalculator;
import com.hospitalApi.surgery.models.Surgery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialReportService implements ReportService<FinancialReportDTO, FinancialFilter> {

    private final ForSaleMedicinePort forSaleMedicinePort;
    private final ForConsultPort forConsultPort;

    private final FinancialCalculator<FinancialSummaryDTO, SaleMedicine> financialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, Consult> consultsFinancialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, RoomUsage> roomFinancialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, Surgery> surgeryFinancialCalculator;

    private final EntryBuilder entryBuilder;

    private BigDecimal totalGlobalSales;
    private BigDecimal totalGlobalCost;
    private BigDecimal totalGlobalProfit;
    private FinancialReportType type;

    /**
     * Genera un reporte financiero completo con base en los filtros definidos por
     * el usuario.
     * Este incluye un resumen global (suma total) y un desglose por cada área
     * seleccionada.
     * Las áreas consideradas son: Farmacia, Consultas, Habitaciones y Cirugías.
     * 
     * @param filter Filtros aplicados al reporte, incluyendo fecha de inicio, fecha
     *               de fin,
     *               tipo de reporte (ingresos, egresos o ganancias) y área de
     *               interés.
     * @return Objeto que contiene el resumen financiero global
     *         y el detalle por área.
     */
    @Override
    public FinancialReportDTO generateReport(FinancialFilter filter) {
        // inicializmaos los totales siempre a 0 cada que se genra el reporte
        totalGlobalSales = BigDecimal.ZERO;
        totalGlobalCost = BigDecimal.ZERO;
        totalGlobalProfit = BigDecimal.ZERO;
        type = filter.getReportType();

        List<FinancialReportPerAreaDTO> financialReportPerAreas = new ArrayList<>();

        // si se quiere la farmacia o todos entonces inicalizamos las ventas de medicina
        if (filter.getArea() == FinancialReportArea.PHARMACY || filter.getArea() == FinancialReportArea.ALL) {
            List<SaleMedicine> sales = forSaleMedicinePort.getSalesMedicineBetweenDatesAndMedicineName(
                    filter.getStartDate(), filter.getEndDate(), null);
            FinancialSummaryDTO summary = financialCalculator.calculateFinancialTotalsOfList(sales);
            List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromSales(sales, filter.getReportType());
            // mandmamos a sumar los totales del area a los globales
            addToGlobalTotals(summary);
            financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                    summary, "Farmacia", entries));
        }

        // si no se quiere ver la farmacia entonces quiere ver cualquier otra cosa y
        // para esas casas necesitamos
        // iiniciar las consultas pagadas
        if (filter.getArea() != FinancialReportArea.PHARMACY) {
            List<Consult> consults = forConsultPort.findPaidConsultsBetweenDates(
                    filter.getStartDate(), filter.getEndDate());
            if (filter.getArea() == FinancialReportArea.CONSULTS || filter.getArea() == FinancialReportArea.ALL) {

                // mandamos a traer los totales financieros de las consultas
                FinancialSummaryDTO summary = consultsFinancialCalculator.calculateFinancialTotalsOfList(consults);

                // mandamos a construir la lista de entradas del reporte
                List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromConsults(consults,
                        filter.getReportType());
                // mandmamos a sumar los totales del area a los globales
                addToGlobalTotals(summary);

                // agregamos el reporte por area a la lista de reportes por area
                financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                        summary, "Consultas", entries));
            }

            if (filter.getArea() == FinancialReportArea.ROOMS || filter.getArea() == FinancialReportArea.ALL) {

                // mandmaos a traer las habitaciones pagadas en funcion de las consultas pagadas
                List<RoomUsage> rooms = getPaidRooms(consults);

                // mandamos a traer los totales financieros de las habitaciones
                FinancialSummaryDTO summary = roomFinancialCalculator.calculateFinancialTotalsOfList(rooms);

                // mandamos a construir la lista de entradas del reporte
                List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromRooms(rooms,
                        filter.getReportType());
                // mandmamos a sumar los totales del area a los globales
                addToGlobalTotals(summary);
                financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                        summary, "Habitaciones", entries));
            }

            if (filter.getArea() == FinancialReportArea.SURGERIES || filter.getArea() == FinancialReportArea.ALL) {

                // mandamos a traer las cirugias pagadas en funcion de las consultas pagadas
                List<Surgery> surgeries = getPaidSurgieries(consults);

                // mandamos a traer los totales financieros de las cirugias
                FinancialSummaryDTO summary = surgeryFinancialCalculator.calculateFinancialTotalsOfList(surgeries);

                // mandamos a construir la lista de entradas del reporte
                List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromSurgeries(surgeries,
                        filter.getReportType());

                // mandmamos a sumar los totales del area a los globales
                addToGlobalTotals(summary);
                financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                        summary, "Cirugías", entries));
            }
        }
        // mandamos a constrir nuestro resumen global
        FinancialSummaryDTO globalFinancialSummaryDTO = buildGlobalSummary();

        return new FinancialReportDTO(globalFinancialSummaryDTO, financialReportPerAreas);
    }

    /**
     * Agrega los valores de un resumen financiero específico al total global
     * acumulado
     * 
     * @param summary el resumen financiero individual que contiene los valores a
     *                sumar.
     *                No debe ser null y sus atributos deben estar
     *                correctamente inicializados.
     */
    private void addToGlobalTotals(FinancialSummaryDTO summary) {
        totalGlobalSales = totalGlobalSales.add(summary.getTotalSales());
        totalGlobalCost = totalGlobalCost.add(summary.getTotalCost());
        totalGlobalProfit = totalGlobalProfit.add(summary.getTotalProfit());
    }

    /**
     * Construye un resumen financiero global, estableciendo el valor total en el
     * campo correspondiente
     * al tipo de reporte (ingresos, egresos o ganancias). Los demás campos se
     * establecen como \0.
     *
     * @param total Valor total a asignar en el resumen global.
     * @param type  Tipo de reporte (ingresos, egresos o ganancias).
     * @return FinancialSummaryDTO representando el resumen
     *         financiero global.
     */
    private FinancialSummaryDTO buildGlobalSummary() {
        return switch (type) {
            case INCOME -> new FinancialSummaryDTO(totalGlobalSales, null, null);
            case EXPENSE -> new FinancialSummaryDTO(null, totalGlobalCost, null);
            default -> new FinancialSummaryDTO(totalGlobalSales, totalGlobalCost, totalGlobalProfit);
        };
    }

    /**
     * Obtiene todas las habitaciones utilizadas (RoomUsage) asociadas a consultas
     * que ya han sido pagadas.
     * Este método es utilizado cuando el reporte incluye la sección de
     * habitaciones.
     *
     * @param paidConsults Lista de consultas médicas marcadas como pagadas.
     * @return Lista de objetos correspondientes a habitaciones
     *         ocupadas durante esas consultas.
     */
    private List<RoomUsage> getPaidRooms(List<Consult> paidConsults) {
        List<RoomUsage> paidRooms = new ArrayList<>();
        // dentor de cada consulta esta la asignacion que tuvo a una habitacion y como
        // ya fue pagada entonces tambien la habitacion
        for (Consult paidConsult : paidConsults) {
            // solo agregmos aquellas que esten asignadas o sea no sean nulas
            if (paidConsult.getRoomUsage() != null) {
                paidRooms.add(paidConsult.getRoomUsage());
            }
        }
        return paidRooms;
    }

    /**
     * Obtiene todas las cirugías realizadas que están asociadas a consultas médicas
     * pagadas.
     * Este método se utiliza cuando se requiere incluir información de cirugías en
     * el reporte financiero.
     *
     * @param paidConsults Lista de consultas que han sido pagadas.
     * @return Lista de objetos Surgery relacionados con las consultas pagadas.
     */
    private List<Surgery> getPaidSurgieries(List<Consult> paidConsults) {
        List<Surgery> paidRooms = new ArrayList<>();
        // dentor de cada consulta esta la asignacion que tuvo a una habitacion y como
        // ya fue pagada entonces tambien la habitacion
        for (Consult paidConsult : paidConsults) {
            // solo agregmos aquellas que esten asignadas o sea no sean nulas
            if (paidConsult.getSurgeries() != null) {
                paidRooms.addAll(paidConsult.getSurgeries());
            }
        }
        return paidRooms;
    }

}