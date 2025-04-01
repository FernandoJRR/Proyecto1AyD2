package com.hospitalApi.consults.services;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.consults.repositories.EmployeeConsultRepository;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.consults.port.ForConsultPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeConsultServiceTest {

    @Mock
    private ForEmployeesPort forEmployeesPort;

    @Mock
    private ForConsultPort forConsultPort;

    @Mock
    private EmployeeConsultRepository employeeConsultRepository;

    @InjectMocks
    private EmployeeConsultService employeeConsultService;

    private static final String EMPLOYEE_ID = "EMP-001";
    private static final String CONSULT_ID = "CONSULT-001";

    private Employee employee;
    private Consult consult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        employee.setId(EMPLOYEE_ID);

        consult = new Consult();
        consult.setId(CONSULT_ID);
    }

    @Test
    void shouldReturnEmployeeConsultsByConsultId() throws NotFoundException {
        // Arrange
        List<EmployeeConsult> expected = new ArrayList<>();
        expected.add(new EmployeeConsult(consult, employee));

        when(employeeConsultRepository.findByConsultId(CONSULT_ID)).thenReturn(expected);

        // Act
        List<EmployeeConsult> result = employeeConsultService.getEmployeeConsultsByConsultId(CONSULT_ID);

        // Assert
        assertEquals(1, result.size());
        verify(employeeConsultRepository).findByConsultId(CONSULT_ID);
    }

    @Test
    void shouldReturnEmployeeConsultsByEmployeeId() throws NotFoundException {
        // Arrange
        List<EmployeeConsult> expected = new ArrayList<>();
        expected.add(new EmployeeConsult(consult, employee));

        when(employeeConsultRepository.findByEmployeeId(EMPLOYEE_ID)).thenReturn(expected);

        // Act
        List<EmployeeConsult> result = employeeConsultService.getEmployeeConsultsByEmployeeId(EMPLOYEE_ID);

        // Assert
        assertEquals(1, result.size());
        verify(employeeConsultRepository).findByEmployeeId(EMPLOYEE_ID);
    }

    @Test
    void shouldDeleteEmployeeConsultSuccessfully() throws NotFoundException {
        // Arrange
        when(employeeConsultRepository.existsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID)).thenReturn(true);
        when(employeeConsultRepository.countByConsultId(CONSULT_ID)).thenReturn(2);
        List<EmployeeConsult> remaining = List.of(new EmployeeConsult(consult, employee));
        when(employeeConsultRepository.findByConsultId(CONSULT_ID)).thenReturn(remaining);

        // Act
        List<EmployeeConsult> result = employeeConsultService.deleteEmployeeConsultsByConsultIdAndEmployeeId(CONSULT_ID,
                EMPLOYEE_ID);

        // Assert
        assertEquals(1, result.size());
        verify(employeeConsultRepository).deleteByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID);
        verify(employeeConsultRepository).findByConsultId(CONSULT_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenEmployeeConsultDoesNotExist() {
        // Arrange
        when(employeeConsultRepository.existsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID)).thenReturn(false);

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> employeeConsultService.deleteEmployeeConsultsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID));

        assertEquals("El empleado " + EMPLOYEE_ID + " no está asignado a la consulta " + CONSULT_ID, ex.getMessage());
        verify(employeeConsultRepository, never()).deleteByConsultIdAndEmployeeId(any(), any());
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenEmployeeIsOnlyOneAssigned() {
        // Arrange
        when(employeeConsultRepository.existsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID)).thenReturn(true);
        when(employeeConsultRepository.countByConsultId(CONSULT_ID)).thenReturn(1);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> employeeConsultService.deleteEmployeeConsultsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID));

        assertTrue(ex.getMessage().contains("porque es el único empleado asignado"));
        verify(employeeConsultRepository, never()).deleteByConsultIdAndEmployeeId(any(), any());
    }

    @Test
    void shouldCreateEmployeeConsultSuccessfully() throws NotFoundException {
        // Arrange
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(employeeConsultRepository.save(any(EmployeeConsult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmployeeConsult result = employeeConsultService.createEmployeeConsult(consult, EMPLOYEE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(employee, result.getEmployee());
        assertEquals(consult, result.getConsult());

        ArgumentCaptor<EmployeeConsult> captor = ArgumentCaptor.forClass(EmployeeConsult.class);
        verify(employeeConsultRepository).save(captor.capture());

        EmployeeConsult captured = captor.getValue();
        assertEquals(employee, captured.getEmployee());
        assertEquals(consult, captured.getConsult());

        verify(forEmployeesPort).findEmployeeById(EMPLOYEE_ID);
        verify(employeeConsultRepository).save(any(EmployeeConsult.class));
    }

    /**
     * dado: que el empleado no está aún asignado a la consulta.
     * cuando: se llama a addEmployeeConsultsByConsultIdAndEmployeeId.
     * entonces: se crea la asignación y se retorna la lista de asignaciones
     * actualizada.
     */
    @Test
    void shouldAddEmployeeConsultsSuccessfully() throws NotFoundException {
        // Arrange
        when(employeeConsultRepository.existsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(employeeConsultRepository.save(any(EmployeeConsult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeConsultRepository.findByConsultId(CONSULT_ID))
                .thenReturn(List.of(new EmployeeConsult(consult, employee)));

        // Act
        List<EmployeeConsult> result = employeeConsultService.addEmployeeConsultsByConsultIdAndEmployeeId(consult,
                EMPLOYEE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(consult, result.get(0).getConsult());
        assertEquals(employee, result.get(0).getEmployee());

        verify(employeeConsultRepository).existsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID);
        verify(employeeConsultRepository).save(any(EmployeeConsult.class));
        verify(employeeConsultRepository).findByConsultId(CONSULT_ID);
        verify(forEmployeesPort).findEmployeeById(EMPLOYEE_ID);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeAlreadyAssignedToConsult() throws NotFoundException {
        // Arrange
        when(employeeConsultRepository.existsByConsultIdAndEmployeeId(CONSULT_ID, EMPLOYEE_ID)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            employeeConsultService.addEmployeeConsultsByConsultIdAndEmployeeId(consult, EMPLOYEE_ID);
        });

        verify(employeeConsultRepository, never()).save(any());
        verify(forEmployeesPort, never()).findEmployeeById(any());
    }

}
