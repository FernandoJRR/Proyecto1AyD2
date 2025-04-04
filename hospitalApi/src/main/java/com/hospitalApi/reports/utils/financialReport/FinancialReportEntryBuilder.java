package com.hospitalApi.reports.utils.financialReport;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportEntryDTO;
import com.hospitalApi.reports.enums.FinancialReportType;
import com.hospitalApi.reports.ports.EntryBuilder;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.DateFormatterUtil;
import com.hospitalApi.shared.utils.FinancialCalculator;
import com.hospitalApi.surgery.models.Surgery;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FinancialReportEntryBuilder implements EntryBuilder {

    private final DateFormatterUtil dateFormatterUtil;
    private final FinancialCalculator<FinancialSummaryDTO, SaleMedicine> financialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, Consult> consultsFinancialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, RoomUsage> roomFinancialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, Surgery> surgeryFinancialCalculator;
    private FinancialReportType type;

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromSales(List<SaleMedicine> elements,
            FinancialReportType type) {
        // inicializams el tipo de reporte
        this.type = type;
        // por cada uno de los elementos trasformamos con el debido builder
        return elements.stream()
                .map(element -> buildFromSaleMedicine(element))
                .toList();
    }

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromConsults(List<Consult> elements, FinancialReportType type) {
        // inicializams el tipo de reporte
        this.type = type;
        return elements.stream()
                .map(element -> buildFromConsult(element))
                .toList();
    }

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromRooms(List<RoomUsage> elements, FinancialReportType type) {
        // inicializams el tipo de reporte
        this.type = type;
        return elements.stream()
                .map(element -> buildFromRoomUsage(element))
                .toList();
    }

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromSurgeries(List<Surgery> elements, FinancialReportType type) {
        // inicializams el tipo de reporte
        this.type = type;
        return elements.stream()
                .map(element -> buildFromSurgery(element))
                .toList();
    }

    /**
     * Construye una entrada del reporte financiero a partir de una venta de
     * medicamento,
     * incluyendo el nombre del medicamento, precio, costo y cantidad vendida.
     *
     * @param sale instancia de venta de medicamento.
     * @return entrada detallada del reporte financiero.
     */
    private FinancialReportEntryDTO buildFromSaleMedicine(SaleMedicine sale) {
        String description = String.format(
                "Venta del medicamento: \"%s\" Precio: Q %s Costo: Q %s Cantidad: %s",
                sale.getMedicine().getName(), sale.getPrice(), sale.getMedicineCost(),
                sale.getQuantity());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = financialCalculator.calculateFinancialTotals(sale);

        BigDecimal value = getFinancialVaue(financialSummary);
        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(sale.getCreatedAt()),
                description,
                value);
    }

    /**
     * Construye una entrada del reporte financiero a partir de una consulta médica,
     * mostrando el nombre del paciente, su CUI y el costo de la consulta.
     *
     * @param consult consulta médica registrada.
     * @return entrada detallada del reporte financiero.
     */
    private FinancialReportEntryDTO buildFromConsult(Consult consult) {
        String description = String.format(
                "Consulta del paciente: \"%s\" con CUI: %s, Precio: Q %s",
                consult.getPatient().getFirstnames(),
                consult.getPatient().getDpi(),
                consult.getCostoConsulta());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = consultsFinancialCalculator.calculateFinancialTotals(consult);
        // obtenemos el valor dependiendo del tipo de reporte
        BigDecimal value = getFinancialVaue(financialSummary);
        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(consult.getCreatedAt()),
                description,
                value);
    }

    /**
     * Construye una entrada del reporte financiero a partir de una cirugía
     * realizada,
     * incluyendo tipo de cirugía, nombre del paciente, CUI, costo hospitalario y
     * precio.
     *
     * @param surgery cirugía registrada.
     * @return entrada detallada del reporte financiero.
     */
    private FinancialReportEntryDTO buildFromSurgery(Surgery surgery) {
        String description = String.format(
                "Cirugía \"%s\" al paciente: \"%s\" CUI: %s, Precio: Q %s, Costo: Q %s",
                surgery.getSurgeryType().getType(),
                surgery.getConsult().getPatient().getFirstnames(),
                surgery.getConsult().getPatient().getDpi(),
                surgery.getSurgeryCost(),
                surgery.getHospitalCost());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = surgeryFinancialCalculator.calculateFinancialTotals(surgery);
        // obtenemos el valor dependiendo del tipo de reporte
        BigDecimal value = getFinancialVaue(financialSummary);

        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(surgery.getPerformedDate()),
                description,
                value);
    }

    /**
     * Construye una entrada del reporte financiero a partir del uso de una
     * habitación,
     * especificando número de habitación, días de uso, precio y costo diarios.
     *
     * @param usage instancia de uso de habitación.
     * @return entrada detallada del reporte financiero.
     */
    private FinancialReportEntryDTO buildFromRoomUsage(RoomUsage usage) {
        String description = String.format(
                "Habitación: \"%s\" por %s días, Precio diario: Q %s, Costo diario: Q %s",
                usage.getRoom().getNumber(),
                usage.getUsageDays(),
                usage.getDailyRoomPrice(),
                usage.getDailyRoomMaintenanceCost());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = roomFinancialCalculator.calculateFinancialTotals(usage);
        // obtenemos el valor dependiendo del tipo de reporte
        BigDecimal value = getFinancialVaue(financialSummary);
        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(usage.getCreatedAt()),
                description,
                value);
    }

    /**
     * Obtiene el valor financiero correspondiente según el tipo de reporte
     * establecido.
     * 
     * Si el tipo es INCOME, retorna el total de ventas; si es EXPENSE, retorna el
     * total de costos;
     * de lo contrario, retorna la ganancia total.
     *
     * @param financialSummary resumen financiero de la entidad analizada.
     * @return valor financiero calculado según el tipo de reporte (venta, costo o
     *         ganancia).
     */
    private BigDecimal getFinancialVaue(FinancialSummaryDTO financialSummary) {
        return switch (type) {
            case INCOME -> financialSummary.getTotalSales();
            case EXPENSE -> financialSummary.getTotalCost();
            default -> financialSummary.getTotalProfit();
        };
    }
}
