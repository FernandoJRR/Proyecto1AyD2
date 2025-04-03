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
public class FinancialReportService implements ReportService<Object, FinancialFilter> {

    private final ForSaleMedicinePort forSaleMedicinePort;
    private final ForConsultPort forConsultPort;

    private final FinancialCalculator<FinancialSummaryDTO, SaleMedicine> financialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, Consult> consultsFinancialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, RoomUsage> roomFinancialCalculator;
    private final FinancialCalculator<FinancialSummaryDTO, Surgery> surgeryFinancialCalculator;
    private final EntryBuilder entryBuilder;

    @Override
    public FinancialReportDTO generateReport(FinancialFilter filter) {

        List<FinancialReportPerAreaDTO> financialReportPerAreas = new ArrayList<>();

        // si se quiere la farmacia o todos entonces inicalizamos las ventas de medicina
        if (filter.getArea() == FinancialReportArea.PHARMACY || filter.getArea() == FinancialReportArea.ALL) {
            List<SaleMedicine> sales = forSaleMedicinePort.getSalesMedicineBetweenDatesAndMedicineName(
                    filter.getStartDate(), filter.getEndDate(), null);
            FinancialSummaryDTO summary = financialCalculator.calculateFinancialTotalsOfList(sales);
            List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromSales(sales, filter.getReportType());
            financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                    summary, "Farmacia", entries));
        }

        // si no se quiere ver la farmacia entonces quiere ver cualquier otra cosa y
        // para esas casas necesitamos
        // iiniciar las consultas pagadas
        if (filter.getArea() != FinancialReportArea.PHARMACY) {
            List<Consult> consults = forConsultPort.findPaidConsultsBetweenDates(
                    filter.getStartDate(), filter.getEndDate());
            // si se quiere ver la info de las consultas
            if (filter.getArea() == FinancialReportArea.CONSULTS || filter.getArea() == FinancialReportArea.ALL) {

                // mandamos a
                FinancialSummaryDTO summary = consultsFinancialCalculator.calculateFinancialTotalsOfList(consults);
                List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromConsults(consults,
                        filter.getReportType());
                financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                        summary, "Consultas", entries));
            }

            if (filter.getArea() == FinancialReportArea.ROOMS || filter.getArea() == FinancialReportArea.ALL) {
                List<RoomUsage> rooms = getPaidRooms(consults);
                FinancialSummaryDTO summary = roomFinancialCalculator.calculateFinancialTotalsOfList(rooms);
                List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromRooms(rooms,
                        filter.getReportType());
                financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                        summary, "Habitaciones", entries));
            }

            if (filter.getArea() == FinancialReportArea.SURGERIES || filter.getArea() == FinancialReportArea.ALL) {
                List<Surgery> surgeries = getPaidSurgieries(consults);
                FinancialSummaryDTO summary = surgeryFinancialCalculator.calculateFinancialTotalsOfList(surgeries);
                List<FinancialReportEntryDTO> entries = entryBuilder.buildResponseFromSurgeries(surgeries,
                        filter.getReportType());
                financialReportPerAreas.add(new FinancialReportPerAreaDTO(
                        summary, "Cirugías", entries));
            }
        }

        BigDecimal globalTotal = financialReportPerAreas.stream()
                .map(area -> extractValue(area.getFinancialSummary(), filter.getReportType()))
                .reduce(BigDecimal.ZERO, (acummulated, toAdd) -> acummulated.add(toAdd));

        FinancialSummaryDTO globalFinancialSummaryDTO = buildGlobalSummary(globalTotal, filter.getReportType());

        return new FinancialReportDTO(globalFinancialSummaryDTO, financialReportPerAreas);
    }

    private BigDecimal extractValue(FinancialSummaryDTO summary, FinancialReportType type) {
        return switch (type) {
            case INCOME -> summary.getTotalSales();
            case EXPENSE -> summary.getTotalCost();
            default -> summary.getTotalProfit();
        };
    }

    private FinancialSummaryDTO buildGlobalSummary(BigDecimal total, FinancialReportType type) {
        return switch (type) {
            case INCOME -> new FinancialSummaryDTO(total, null, null);
            case EXPENSE -> new FinancialSummaryDTO(null, total, null);
            default -> new FinancialSummaryDTO(null, null, total);
        };
    }

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