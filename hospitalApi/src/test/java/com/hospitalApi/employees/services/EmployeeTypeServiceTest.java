package com.hospitalApi.employees.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.repositories.EmployeeTypeRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

public class EmployeeTypeServiceTest {

    @Mock
    private EmployeeTypeRepository employeeTypeRepository;

    @InjectMocks
    private EmployeeTypeService employeeService;

    private EmployeeType employeeType;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeType = new EmployeeType("asdfg-asdfgasdf", "Admin");
    }

    @Test
    public void shouldReturnTrueWhenEmployeeTypeExistsById() throws NotFoundException {
        // ARRANGE
        when(employeeTypeRepository.existsById(anyString())).thenReturn(true);
        // ACT
        boolean result = employeeService.verifyExistsEmployeeTypeById(employeeType.getId());
        // ASSERTS
        assertTrue(result);
    }

    @Test
    public void shouldThrowWhenEmployeeTypeDoesNotExistById() {
        // ARRANGE
        when(employeeTypeRepository.existsById(anyString())).thenReturn(false);
        // ACT y ASSERTS
        assertThrows(NotFoundException.class, () -> {
            employeeService.verifyExistsEmployeeTypeById(employeeType.getId());
        });
    }

    @Test
    public void shouldReturnTrueWhenEmployeeTypeExistsByName() throws NotFoundException {
        // ARRANGE
        when(employeeTypeRepository.existsByName(anyString())).thenReturn(true);
        // ACT
        boolean result = employeeService.verifyExistsEmployeeTypeByName(employeeType.getName());
        // ASSERTS
        assertTrue(result);
    }

    @Test
    public void shouldThrownWhenEmployeeTypeDoesNotExistByName() {
        // ARRANGE
        when(employeeTypeRepository.existsByName(anyString())).thenReturn(false);
        // ACT y ASSERTS
        assertThrows(NotFoundException.class, () -> {
            employeeService.verifyExistsEmployeeTypeByName(employeeType.getName());
        });
    }

}
