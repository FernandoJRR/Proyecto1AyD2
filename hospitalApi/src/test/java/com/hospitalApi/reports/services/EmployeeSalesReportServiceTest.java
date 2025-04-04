package com.hospitalApi.reports.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.medicines.dtos.SaleMedicineResponseDTO;
import com.hospitalApi.medicines.mappers.SaleMedicineMapper;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForSaleMedicinePort;
import com.hospitalApi.reports.dtos.request.EmployeeProfitFilter;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.EmployeeProfitSummary;
import com.hospitalApi.reports.dtos.response.employeeSalesReport.SalesPerEmployeeDTO;
import com.hospitalApi.shared.dtos.FinancialSummaryDTO;
import com.hospitalApi.shared.enums.EmployeeTypeEnum;
import com.hospitalApi.shared.utils.FinancialCalculator;

@ExtendWith(MockitoExtension.class)
public class EmployeeSalesReportServiceTest {

    @Mock
    private ForSaleMedicinePort forSaleMedicinePort;

    @Mock
    private FinancialCalculator<FinancialSummaryDTO, List<SaleMedicine>> financialCalculator;

    @Mock
    private SaleMedicineMapper saleMedicineMapper;

    @InjectMocks
    private EmployeeSalesReportService employeeSalesReportService;

    private static final String EMPLOYEE_ID = "adsdfdf-sdfds-sdfs";
    private static final String EMPLOYEE_CUI = "2345330910901";
    private static final String FIRST_NAME = "Luis";
    private static final String LAST_NAME = "Monterroso";
    private static final BigDecimal SALARY = new BigDecimal("5000.00");
    private static final BigDecimal IGSS = new BigDecimal(4);
    private static final BigDecimal IRTRA = new BigDecimal(1);

    private static final String SALE_ID = "SALE-EMP-001";
    private static final int SALE_QUANTITY = 1;

    private static final BigDecimal EXPECTED_TOTAL_SALES = new BigDecimal(100);
    private static final BigDecimal EXPECTED_TOTAL_COST = new BigDecimal(60);
    private static final BigDecimal EXPECTED_TOTAL_PROFIT = new BigDecimal(40);

    private static final BigDecimal SALE_PRICE = new BigDecimal(10);
    private static final BigDecimal SALE_TOTAL = new BigDecimal(10);
    private static final BigDecimal SALE_COST = new BigDecimal(6);
    private static final BigDecimal SALE_PROFIT = new BigDecimal(4);

    private Employee employee;
    private Medicine medicine;
    private SaleMedicine saleMedicine;
    private FinancialSummaryDTO expectedGlobalSummary;
    private List<SaleMedicineResponseDTO> expectedSaleDtoList;
    private EmployeeProfitFilter filter;

    @BeforeEach
    public void setUp() {
        employee = new Employee(EMPLOYEE_CUI, FIRST_NAME, LAST_NAME, SALARY, IGSS, IRTRA);
        employee.setId(EMPLOYEE_ID);
        employee.setEmployeeType(EmployeeTypeEnum.DOCTOR.getEmployeeType());

        medicine = new Medicine();

        saleMedicine = new SaleMedicine();
        saleMedicine.setId(SALE_ID);
        saleMedicine.setQuantity(SALE_QUANTITY);
        saleMedicine.setEmployee(employee);
        saleMedicine.setMedicine(medicine);

        expectedGlobalSummary = new FinancialSummaryDTO(EXPECTED_TOTAL_SALES, EXPECTED_TOTAL_COST,
                EXPECTED_TOTAL_PROFIT);

        expectedSaleDtoList = List.of(new SaleMedicineResponseDTO(
                SALE_ID, null, null, SALE_QUANTITY,
                SALE_PRICE, SALE_TOTAL, SALE_COST, SALE_PROFIT, "2024-03-31"));

        filter = new EmployeeProfitFilter(FIRST_NAME, EMPLOYEE_CUI);
    }

    /**
     * dado: un nombre y CUI de empleado existentes con ventas registradas.
     * cuando: se genera el reporte.
     * entonces: se agrupan correctamente por empleado y se retornan sus ventas y
     * totales.
     */
    @Test
    public void shouldGenerateEmployeeProfitReportSuccessfully() {
        // Arrange
        List<SaleMedicine> salesList = List.of(saleMedicine);

        when(forSaleMedicinePort.getSalesMedicineByEmployeeNameAndCui(anyString(), anyString())).thenReturn(salesList);
        when(financialCalculator.calculateFinancialTotalsOfList(any())).thenReturn(expectedGlobalSummary);
        when(saleMedicineMapper.fromSaleMedicineListToSaleMedicineDTOList(any())).thenReturn(expectedSaleDtoList);

        // Act
        EmployeeProfitSummary result = employeeSalesReportService.generateReport(filter);

        // Assert
        SalesPerEmployeeDTO dto = result.getSalePerEmployee().get(0);

        assertAll(

                () -> assertNotNull(result),
                () -> assertEquals(expectedGlobalSummary, result.getFinancialSummary()),
                () -> assertEquals(1, result.getSalePerEmployee().size()),
                () -> assertEquals(employee.getFullName(), dto.getEmployeeFullName()),
                () -> assertEquals(EMPLOYEE_CUI, dto.getCui()),
                () -> assertEquals(expectedSaleDtoList, dto.getSales()),
                () -> assertEquals(expectedGlobalSummary, dto.getFinancialSummaryDTO()));
    }
}
