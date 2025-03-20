package com.hospitalApi.employees.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.EmployeeHistoryRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public class EmployeeHistoryServiceTest {

    @Mock
    private EmployeeHistoryRepository employeeHistoryRepository;

    @Mock
    private ForHistoryTypePort forHistoryTypePort;

    @InjectMocks
    private EmployeeHistoryService employeeHistoryService;

    private Employee employee;
    private EmployeeHistory employeeHistoryHiring;
    private HistoryType historyType;

    private static final String EMPLOYEE_HISTORY_HIRING_COMMENTARY = "Se realizo la contratacion";
    private static final String EMPLOYEE_HISTORY_ID = "fdsf-jhds-jtes";

    private static final String HISTORY_TYPE_ID = "fdsf-rtrer-bbvk";
    private static final String HISTORY_TYPE = "Contratacion";
    private static final LocalDate EMPLOYEE_HISTORY_LOCAL_DATE = LocalDate.of(2022, 11, 23);

    // Constantes para el empleado de test
    private static final String EMPLOYEE_ID = "adsfgdh-arsgdfhg-adfgh";
    private static final String EMPLOYEE_FIRST_NAME = "Fernando";
    private static final String EMPLOYEE_LAST_NAME = "Rodriguez";
    private static final BigDecimal EMPLOYEE_SALARY = new BigDecimal(1200);
    private static final BigDecimal EMPLOYEE_IGSS = new BigDecimal(10.2);
    private static final BigDecimal EMPLOYEE_IRTRA = new BigDecimal(10.2);

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(
                EMPLOYEE_FIRST_NAME,
                EMPLOYEE_LAST_NAME,
                EMPLOYEE_SALARY,
                EMPLOYEE_IGSS,
                EMPLOYEE_IRTRA
                );
        employee.setId(EMPLOYEE_ID);

        historyType = new HistoryType(HISTORY_TYPE);
        historyType.setId(HISTORY_TYPE_ID);

        employeeHistoryHiring = new EmployeeHistory(EMPLOYEE_HISTORY_HIRING_COMMENTARY);
        employeeHistoryHiring.setHistoryDate(EMPLOYEE_HISTORY_LOCAL_DATE);
        employeeHistoryHiring.setId(EMPLOYEE_HISTORY_ID);
    }

    @Test
    public void shouldCreateHiringEmployeeHistory() throws DuplicatedEntryException, NotFoundException {
        // arrglar
        when(forHistoryTypePort.findHistoryTypeByName(HISTORY_TYPE)).thenReturn(historyType);
        when(employeeHistoryRepository.save(any(EmployeeHistory.class))).thenReturn(employeeHistoryHiring);

        // actuar
        EmployeeHistory resultEmployeeHistory = employeeHistoryService.createEmployeeHistoryHiring(employee, EMPLOYEE_HISTORY_LOCAL_DATE);

        // evaluar
        ArgumentCaptor<EmployeeHistory> employeeHistoryCaptor = ArgumentCaptor.forClass(EmployeeHistory.class);

        // se captura lo guardado por save()
        verify(employeeHistoryRepository).save(employeeHistoryCaptor.capture());

        EmployeeHistory capturedEmployeeHistory = employeeHistoryCaptor.getValue();

        assertAll(
                () -> assertNotNull(resultEmployeeHistory),
                () -> assertEquals(resultEmployeeHistory.getCommentary(), capturedEmployeeHistory.getCommentary()),
                () -> assertEquals(resultEmployeeHistory.getHistoryDate(), capturedEmployeeHistory.getHistoryDate()),
                () -> assertEquals(resultEmployeeHistory.getHistoryType(), capturedEmployeeHistory.getHistoryType())
        );
    }
}
