package com.hospitalApi.reports.services.financialReport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.utils.ConsultFinancialCalculator;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.medicines.utils.MedicineSalesCalculator;
import com.hospitalApi.reports.dtos.request.FinancialFilter;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportDTO;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportEntryDTO;
import com.hospitalApi.reports.enums.FinancialReportArea;
import com.hospitalApi.reports.enums.FinancialReportType;
import com.hospitalApi.reports.utils.financialReport.FinancialReportEntryBuilder;
import com.hospitalApi.rooms.utils.RoomFinancialCalculator;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.surgery.utils.SurgeryFinancialCalculator;

@ExtendWith(MockitoExtension.class)
public class FinancialReportServiceTest {

    @Mock
    private ForSaleMedicinePort saleMedicinePort;
    @Mock
    private ForConsultPort consultPort;
    @Mock
    private MedicineSalesCalculator medicineCalculator;
    @Mock
    private ConsultFinancialCalculator consultCalculator;
    @Mock
    private RoomFinancialCalculator roomCalculator;
    @Mock
    private SurgeryFinancialCalculator surgeryCalculator;
    @Mock
    private FinancialReportEntryBuilder entryBuilder;

    @InjectMocks
    private FinancialReportService serviceFinancialReportService;

    private static final BigDecimal TOTAL_SALES = new BigDecimal(100);
    private static final BigDecimal TOTAL_COST = new BigDecimal(200);
    private static final BigDecimal TOTAL_PROFIT = new BigDecimal(140);

    private static final String DATE_ENTRY = "01/01/2025";
    private static final String DESCRIPTION_ENTRY = "Venta test";

    private SaleMedicine saleMedicine;
    private Consult consult;
    private FinancialSummaryDTO summary;
    private FinancialReportEntryDTO entry;

    @BeforeEach
    public void setUp() {
        serviceFinancialReportService = new FinancialReportService(
                saleMedicinePort, consultPort,
                medicineCalculator, consultCalculator,
                roomCalculator, surgeryCalculator,
                entryBuilder);

        saleMedicine = new SaleMedicine();
        consult = new Consult(null, null);
        summary = new FinancialSummaryDTO(TOTAL_SALES, TOTAL_COST, TOTAL_PROFIT);
        entry = new FinancialReportEntryDTO(DATE_ENTRY, DESCRIPTION_ENTRY, TOTAL_SALES);
    }

    /**
     * dado: un único resumen financiero con total de ventas igual a Q100.00.
     * cuando: se genera el reporte financiero global.
     * entonces: el total global debe ser Q100.00.
     */
    @Test
    public void shouldCalculateGlobalIncomeTotal() {
        // arrange
        FinancialFilter filter = new FinancialFilter(null, null,
                FinancialReportType.INCOME,
                FinancialReportArea.PHARMACY);
        when(saleMedicinePort.getSalesMedicineBetweenDatesAndMedicineName(any(), any(), any()))
                .thenReturn(List.of(saleMedicine));
        when(medicineCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);
        when(entryBuilder.buildResponseFromSales(any(), any()))
                .thenReturn(List.of(entry));

        // act
        FinancialReportDTO report = serviceFinancialReportService.generateReport(filter);

        // assert
        assertEquals(TOTAL_SALES, report.getGlobalFinancialSummary().getTotalSales());
    }

    /**
     * dado: un único resumen financiero con costo total igual a Q200.00.
     * cuando: se genera el reporte financiero global para el área de consultas y
     * tipo EXPENSE.
     * entonces: el total global debe ser Q200.00 como egreso.
     */

    @Test
    public void shouldCalculateGlobalExpenseTotalForConsults() {
        // arrange

        FinancialFilter filter = new FinancialFilter(null, null,
                FinancialReportType.EXPENSE,
                FinancialReportArea.CONSULTS);

        when(consultPort.findPaidConsultsBetweenDates(any(), any()))
                .thenReturn(List.of(consult));
        when(consultCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);
        when(entryBuilder.buildResponseFromConsults(any(), any()))
                .thenReturn(List.of(entry));

        // act
        FinancialReportDTO report = serviceFinancialReportService.generateReport(filter);

        // assert
        assertEquals(TOTAL_COST, report.getGlobalFinancialSummary().getTotalCost());
    }

    /**
     * dado: un único resumen financiero con ganancia total igual a Q140.00.
     * cuando: se genera el reporte financiero global para el área de habitaciones y
     * tipo PROFIT.
     * entonces: el total global debe ser Q140.00 como ganancia.
     */

    @Test
    public void shouldCalculateGlobalProfitTotalForRooms() {
        // arrange

        FinancialFilter filter = new FinancialFilter(null, null,
                FinancialReportType.PROFIT,
                FinancialReportArea.ROOMS);

        when(consultPort.findPaidConsultsBetweenDates(any(), any()))
                .thenReturn(List.of(consult));
        when(roomCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);

        // act
        FinancialReportDTO report = serviceFinancialReportService.generateReport(filter);

        // assert
        assertEquals(TOTAL_PROFIT, report.getGlobalFinancialSummary().getTotalProfit());
    }

    /**
     * dado: un único resumen financiero con ganancia total igual a Q140.00.
     * cuando: se genera el reporte financiero global para el área de cirugías y
     * tipo PROFIT.
     * entonces: el total global debe ser Q140.00 como ganancia.
     */
    @Test
    public void shouldCalculateGlobalProfitTotalForSurgeries() {
        // arrange

        FinancialFilter filter = new FinancialFilter(null, null,
                FinancialReportType.PROFIT,
                FinancialReportArea.SURGERIES);

        when(consultPort.findPaidConsultsBetweenDates(any(), any()))
                .thenReturn(List.of(consult));
        when(surgeryCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);

        // act
        FinancialReportDTO report = serviceFinancialReportService.generateReport(filter);

        // assert
        assertEquals(TOTAL_PROFIT, report.getGlobalFinancialSummary().getTotalProfit());
    }

    /**
     * dado: un resumen financiero de Q140.00 por cada área (farmacia, consultas,
     * habitaciones y cirugías).
     * cuando: se genera el reporte financiero global para el área ALL y tipo
     * PROFIT.
     * entonces: el total global debe ser Q560.00 como ganancia total acumulada.
     */
    @Test
    public void shouldCalculateGlobalProfitTotalForAll() {
        // arrange
        FinancialFilter filter = new FinancialFilter(null, null,
                FinancialReportType.PROFIT,
                FinancialReportArea.ALL);
        when(saleMedicinePort.getSalesMedicineBetweenDatesAndMedicineName(any(), any(), any()))
                .thenReturn(List.of(saleMedicine));
        when(consultPort.findPaidConsultsBetweenDates(any(), any()))
                .thenReturn(List.of(consult));
        when(surgeryCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);
        when(roomCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);
        when(consultCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);
        when(medicineCalculator.calculateFinancialTotalsOfList(any()))
                .thenReturn(summary);
        // act
        FinancialReportDTO report = serviceFinancialReportService.generateReport(filter);
        // assert
        assertEquals(TOTAL_PROFIT.multiply(new BigDecimal(4)), report.getGlobalFinancialSummary().getTotalProfit());
    }
}
