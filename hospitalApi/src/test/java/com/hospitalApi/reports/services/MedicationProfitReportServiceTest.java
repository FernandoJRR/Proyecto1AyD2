package com.hospitalApi.reports.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.mappers.SaleMedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.reports.dtos.request.MedicationProfitFilter;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.MedicationProfitSummary;
import com.hospitalApi.reports.dtos.response.medicationProfitReport.SalePerMedicationDTO;
import com.hospitalApi.shared.FinancialCalculator;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;

@ExtendWith(MockitoExtension.class)
public class MedicationProfitReportServiceTest {

    @Mock
    private ForSaleMedicinePort forSaleMedicinePort;

    @Mock
    private FinancialCalculator<FinancialSummaryDTO, List<SaleMedicine>> financialCalculator;

    @Mock
    private SaleMedicineMapper saleMedicineMapper;

    @InjectMocks
    private MedicationProfitReportService medicationProfitReportService;

    private static final String MEDICINE_NAME = "Paracetamol";
    private static final String SALE_ID = "ASDF-ASDF-ADSF-ASDF";
    private static final int SALE_QUANTITY = 1;
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 12, 31);

    private static final BigDecimal EXPECTED_TOTAL_SALES = new BigDecimal("100.00");
    private static final BigDecimal EXPECTED_TOTAL_COST = new BigDecimal("40.00");
    private static final BigDecimal EXPECTED_TOTAL_PROFIT = new BigDecimal("60.00");

    private static final BigDecimal SALE_PRICE = new BigDecimal("10.00");
    private static final BigDecimal SALE_TOTAL = new BigDecimal("10.00");
    private static final BigDecimal SALE_COST = new BigDecimal("4.00");
    private static final BigDecimal SALE_PROFIT = new BigDecimal("6.00");

    private SaleMedicine saleMedicine;
    private Medicine medicine;
    private FinancialSummaryDTO expectedGlobalSummary;
    private List<SaleMedicineResponseDTO> expectedSaleDtoList;
    private MedicationProfitFilter filter;

    @BeforeEach
    public void setUp() {
        medicine = new Medicine();
        medicine.setName(MEDICINE_NAME);

        saleMedicine = new SaleMedicine();
        saleMedicine.setMedicine(medicine);

        expectedGlobalSummary = new FinancialSummaryDTO(
                EXPECTED_TOTAL_SALES,
                EXPECTED_TOTAL_COST,
                EXPECTED_TOTAL_PROFIT);

        expectedSaleDtoList = List.of(new SaleMedicineResponseDTO(
            SALE_ID, null, null, SALE_QUANTITY,
            SALE_PRICE, SALE_TOTAL, SALE_COST, SALE_PROFIT));
            
        filter = new MedicationProfitFilter(MEDICINE_NAME, START_DATE, END_DATE);
    }

    @Test
    public void shouldGenerateMedicationProfitReportSuccessfully() {
        // Arrange

        List<SaleMedicine> salesList = List.of(saleMedicine);
        // cuando se manden a traer las ventas siempre traer nuestro mock
        when(forSaleMedicinePort.getSalesMedicineBetweenDatesAndMedicineName(
                any(), any(), anyString())).thenReturn(salesList);
        
        when(financialCalculator.calculateFinancialTotals(any())).thenReturn(expectedGlobalSummary);

        when(saleMedicineMapper.fromSaleMedicineListToSaleMedicineDTOList(any())).thenReturn(expectedSaleDtoList);

        // Act
        MedicationProfitSummary result = medicationProfitReportService.generateReport(filter);

        // Assert
        SalePerMedicationDTO dto = result.getSalePerMedication().get(0);

        assertNotNull(result);
        assertEquals(expectedGlobalSummary, result.getFinancialSummary());
        assertEquals(1, result.getSalePerMedication().size());
        assertEquals(MEDICINE_NAME, dto.getMedicationName());
        assertEquals(expectedGlobalSummary, dto.getFinancialSummaryDTO());
        assertEquals(expectedSaleDtoList, dto.getSales());

        // ASSERT
        verify(financialCalculator, times(2)).calculateFinancialTotals(salesList);
        verify(saleMedicineMapper, times(1)).fromSaleMedicineListToSaleMedicineDTOList(salesList);
    }

}
