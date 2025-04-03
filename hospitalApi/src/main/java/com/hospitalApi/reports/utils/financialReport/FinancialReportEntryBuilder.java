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

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromSales(List<SaleMedicine> elements,
            FinancialReportType type) {
        return elements.stream()
                .map(e -> buildFromSaleMedicine(e, type))
                .toList();
    }

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromConsults(List<Consult> elements, FinancialReportType type) {
        return elements.stream()
                .map(e -> buildFromConsult(e, type))
                .toList();
    }

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromRooms(List<RoomUsage> elements, FinancialReportType type) {
        return elements.stream()
                .map(e -> buildFromRoomUsage(e, type))
                .toList();
    }

    @Override
    public List<FinancialReportEntryDTO> buildResponseFromSurgeries(List<Surgery> elements, FinancialReportType type) {
        return elements.stream()
                .map(e -> buildFromSurgery(e, type))
                .toList();
    }

    private FinancialReportEntryDTO buildFromSaleMedicine(SaleMedicine sale, FinancialReportType type) {
        String description = String.format(
                "Venta del medicamento: \"%s\", Precio: Q %s, Costo: Q %s",
                sale.getMedicine().getName(), sale.getPrice(), sale.getMedicineCost());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = financialCalculator.calculateFinancialTotals(sale);

        BigDecimal value = switch (type) {
            case INCOME -> financialSummary.getTotalSales();
            case EXPENSE -> financialSummary.getTotalCost();
            default -> financialSummary.getTotalProfit();
        };

        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(sale.getCreatedAt()),
                description,
                value);
    }

    private FinancialReportEntryDTO buildFromConsult(Consult consult, FinancialReportType type) {
        String description = String.format(
                "Consulta del paciente: \"%s\" con CUI: %s, Precio: Q %s",
                consult.getPatient().getFirstnames(),
                consult.getPatient().getDpi(),
                consult.getCostoConsulta());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = consultsFinancialCalculator.calculateFinancialTotals(consult);

        BigDecimal value = switch (type) {
            case INCOME -> financialSummary.getTotalSales();
            case EXPENSE -> financialSummary.getTotalCost();
            default -> financialSummary.getTotalProfit();
        };

        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(consult.getCreatedAt()),
                description,
                value);
    }

    private FinancialReportEntryDTO buildFromSurgery(Surgery surgery, FinancialReportType type) {
        String description = String.format(
                "Cirugía \"%s\" al paciente: \"%s\" CUI: %s, Precio: Q %s, Costo: Q %s",
                surgery.getSurgeryType().getType(),
                surgery.getConsult().getPatient().getFirstnames(),
                surgery.getConsult().getPatient().getDpi(),
                surgery.getSurgeryCost(),
                surgery.getHospitalCost());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = surgeryFinancialCalculator.calculateFinancialTotals(surgery);

        BigDecimal value = switch (type) {
            case INCOME -> financialSummary.getTotalSales();
            case EXPENSE -> financialSummary.getTotalCost();
            default -> financialSummary.getTotalProfit();
        };

        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(surgery.getPerformedDate()),
                description,
                value);
    }

    private FinancialReportEntryDTO buildFromRoomUsage(RoomUsage usage, FinancialReportType type) {
        String description = String.format(
                "Habitación: \"%s\" por %s días, Precio diario: Q %s, Costo diario: Q %s",
                usage.getRoom().getNumber(),
                usage.getUsageDays(),
                usage.getDailyRoomPrice(),
                usage.getDailyRoomMaintenanceCost());
        // obtenemos el summary de la operacion
        FinancialSummaryDTO financialSummary = roomFinancialCalculator.calculateFinancialTotals(usage);

        BigDecimal value = switch (type) {
            case INCOME -> financialSummary.getTotalSales();
            case EXPENSE -> financialSummary.getTotalCost();
            default -> financialSummary.getTotalProfit();
        };

        return new FinancialReportEntryDTO(
                dateFormatterUtil.formatDateToLocalFormat(usage.getCreatedAt()),
                description,
                value);
    }
}
