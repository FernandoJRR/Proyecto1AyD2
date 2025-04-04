package com.hospitalApi.reports.utils.financialReport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.utils.ConsultFinancialCalculator;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.utils.MedicineSalesCalculator;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.reports.dtos.response.financialReport.FinancialReportEntryDTO;
import com.hospitalApi.reports.enums.FinancialReportType;
import com.hospitalApi.rooms.enums.RoomStatus;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.rooms.utils.RoomFinancialCalculator;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.utils.DateFormatterUtil;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.utils.SurgeryFinancialCalculator;

@ExtendWith(MockitoExtension.class)
public class FinancialReportEntryBuilderTest {

        @Mock
        private DateFormatterUtil dateFormatterUtil;
        @Mock
        private MedicineSalesCalculator medicineCalculator;
        @Mock
        private ConsultFinancialCalculator consultCalculator;
        @Mock
        private RoomFinancialCalculator roomCalculator;
        @Mock
        private SurgeryFinancialCalculator surgeryCalculator;

        private FinancialReportEntryBuilder builder;

        private static final BigDecimal SALE = BigDecimal.valueOf(300);
        private static final BigDecimal COST = BigDecimal.valueOf(100);
        private static final BigDecimal PROFIT = BigDecimal.valueOf(200);
        private static final BigDecimal ROOM_DAILY_PRICE = BigDecimal.valueOf(500);
        private static final BigDecimal ROOM_MAINTENANCE_COST = BigDecimal.valueOf(100);
        private static final Integer ROOM_USAGE_DAYS = 3;
        private static final Integer QUANTITY = 3;

        private static final String DATE = "01/01/2025";
        private static final String MEDICINE_NAME = "Paracetamol";
        private static final String ROOM_NUMBER = "A101";

        private static final FinancialSummaryDTO SUMMARY = new FinancialSummaryDTO(SALE, COST, PROFIT);

        private SaleMedicine sale;
        private Consult consult;
        private RoomUsage usage;
        private Surgery surgery;

        @BeforeEach
        public void setUp() {
                builder = new FinancialReportEntryBuilder(dateFormatterUtil, medicineCalculator, consultCalculator,
                                roomCalculator, surgeryCalculator);

                when(dateFormatterUtil.formatDateToLocalFormat(any())).thenReturn(DATE);
                Medicine medicine = new Medicine(MEDICINE_NAME, null, null, null, SALE, COST);
                sale = new SaleMedicine(medicine, QUANTITY);

                Patient patient = new Patient();
                consult = new Consult(patient, SALE);

                Room room = new Room(ROOM_NUMBER, ROOM_DAILY_PRICE, ROOM_MAINTENANCE_COST, RoomStatus.AVAILABLE);
                usage = new RoomUsage(consult, room, ROOM_USAGE_DAYS, ROOM_DAILY_PRICE, ROOM_MAINTENANCE_COST);

                surgery = new Surgery(consult, new SurgeryType(), COST, SALE);
        }

        /**
         * dado: una venta de medicamento.
         * cuando: se genera una entrada de reporte financiero tipo PROFIT.
         * entonces: el valor debe ser igual a la ganancia de la venta.
         */
        @Test
        public void shouldBuildEntryFromSaleMedicine() {
                // arragne
                when(medicineCalculator.calculateFinancialTotals(sale)).thenReturn(SUMMARY);
                // act
                List<FinancialReportEntryDTO> result = builder.buildResponseFromSales(List.of(sale),
                                FinancialReportType.PROFIT);
                // assert
                assertAll(
                                () -> assertEquals(1, result.size()),
                                () -> assertEquals(DATE, result.get(0).getDate()),
                                () -> assertEquals(PROFIT, result.get(0).getAmount()));
        }

        /**
         * dado: una consulta médica.
         * cuando: se genera una entrada de reporte financiero tipo EXPENSE.
         * entonces: el valor debe ser igual al costo de la consulta.
         */
        @Test
        public void shouldBuildEntryFromConsult() {
                // arrange
                when(consultCalculator.calculateFinancialTotals(consult)).thenReturn(SUMMARY);
                // axt
                List<FinancialReportEntryDTO> result = builder.buildResponseFromConsults(List.of(consult),
                                FinancialReportType.EXPENSE);
                // assert
                assertAll(
                                () -> assertEquals(1, result.size()),
                                () -> assertEquals(COST, result.get(0).getAmount()));
        }

        /**
         * dado: un uso de habitación.
         * cuando: se genera una entrada de reporte financiero tipo INCOME.
         * entonces: el valor debe ser igual al ingreso generado por la habitación.
         */
        @Test
        public void shouldBuildEntryFromRoomUsage() {
                // arrange
                when(roomCalculator.calculateFinancialTotals(usage)).thenReturn(SUMMARY);
                // act
                List<FinancialReportEntryDTO> result = builder.buildResponseFromRooms(List.of(usage),
                                FinancialReportType.INCOME);
                // assert
                assertAll(
                                () -> assertEquals(1, result.size()),
                                () -> assertEquals(SALE, result.get(0).getAmount()));
        }

        /**
         * dado: una cirugía realizada.
         * cuando: se genera una entrada de reporte financiero tipo PROFIT.
         * entonces: el valor debe ser igual a la ganancia de la cirugía.
         */
        @Test
        public void shouldBuildEntryFromSurgery() {
                // arange
                when(surgeryCalculator.calculateFinancialTotals(surgery)).thenReturn(SUMMARY);
                // act
                List<FinancialReportEntryDTO> result = builder.buildResponseFromSurgeries(List.of(surgery),
                                FinancialReportType.PROFIT);
                // assert
                assertAll(
                                () -> assertEquals(1, result.size()),
                                () -> assertEquals(PROFIT, result.get(0).getAmount()));
        }
}