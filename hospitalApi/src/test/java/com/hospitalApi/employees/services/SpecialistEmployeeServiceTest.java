package com.hospitalApi.employees.services;

import com.hospitalApi.employees.dtos.UpdateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.employees.repositories.SpecialistEmployeeRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SpecialistEmployeeServiceTest {

    @Mock
    private SpecialistEmployeeRepository specialistEmployeeRepository;

    @InjectMocks
    private SpecialistEmployeeService specialistEmployeeService;

    private static final String SPECIALIST_ID = "SPECIALIST-12345";
    private static final String NOMBRES = "Carlos Alberto";
    private static final String APELLIDOS = "Ramirez Lopez";
    private static final String DPI = "1234567890123";
    private SpecialistEmployee specialistEmployee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        specialistEmployee = new SpecialistEmployee();
        specialistEmployee.setId(SPECIALIST_ID);
        specialistEmployee.setNombres(NOMBRES);
        specialistEmployee.setApellidos(APELLIDOS);
        specialistEmployee.setDpi(DPI);
    }

    @Test
    public void shouldCreateSpecialistEmployeeSuccessfully() throws DuplicatedEntryException {
        // Arrange
        when(specialistEmployeeRepository.existsByDpi(DPI)).thenReturn(false);
        when(specialistEmployeeRepository.save(any(SpecialistEmployee.class))).thenReturn(specialistEmployee);

        // Act
        SpecialistEmployee result = specialistEmployeeService.createSpecialistEmployee(specialistEmployee);

        // Assert
        ArgumentCaptor<SpecialistEmployee> captor = ArgumentCaptor.forClass(SpecialistEmployee.class);
        verify(specialistEmployeeRepository).save(captor.capture());

        SpecialistEmployee captured = captor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(DPI, captured.getDpi()),
                () -> assertEquals(NOMBRES, captured.getNombres()),
                () -> assertEquals(APELLIDOS, captured.getApellidos()));
        verify(specialistEmployeeRepository, times(1)).existsByDpi(DPI);
        verify(specialistEmployeeRepository, times(1)).save(any(SpecialistEmployee.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenCreatingWithExistingDpi() {
        // Arrange
        when(specialistEmployeeRepository.existsByDpi(DPI)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedEntryException.class, () -> {
            specialistEmployeeService.createSpecialistEmployee(specialistEmployee);
        });

        verify(specialistEmployeeRepository, times(1)).existsByDpi(DPI);
        verify(specialistEmployeeRepository, times(0)).save(any(SpecialistEmployee.class));
    }

    @Test
    public void shouldGetSpecialistEmployeeByIdSuccessfully() throws NotFoundException {
        // Arrange
        when(specialistEmployeeRepository.findById(SPECIALIST_ID)).thenReturn(Optional.of(specialistEmployee));

        // Act
        SpecialistEmployee result = specialistEmployeeService.getSpecialistEmployeeById(SPECIALIST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(SPECIALIST_ID, result.getId());
        verify(specialistEmployeeRepository, times(1)).findById(SPECIALIST_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        when(specialistEmployeeRepository.findById(SPECIALIST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            specialistEmployeeService.getSpecialistEmployeeById(SPECIALIST_ID);
        });

        verify(specialistEmployeeRepository, times(1)).findById(SPECIALIST_ID);
    }

    @Test
    public void shouldGetSpecialistEmployeeByDpiSuccessfully() throws NotFoundException {
        // Arrange
        when(specialistEmployeeRepository.existsByDpi(DPI)).thenReturn(true);
        when(specialistEmployeeRepository.findByDpi(DPI)).thenReturn(specialistEmployee);

        // Act
        SpecialistEmployee result = specialistEmployeeService.getSpecialistEmployeeByDpi(DPI);

        // Assert
        assertNotNull(result);
        assertEquals(DPI, result.getDpi());
        verify(specialistEmployeeRepository, times(1)).existsByDpi(DPI);
        verify(specialistEmployeeRepository, times(1)).findByDpi(DPI);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenDpiDoesNotExist() {
        // Arrange
        when(specialistEmployeeRepository.existsByDpi(DPI)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            specialistEmployeeService.getSpecialistEmployeeByDpi(DPI);
        });

        verify(specialistEmployeeRepository, times(1)).existsByDpi(DPI);
        verify(specialistEmployeeRepository, times(0)).findByDpi(any());
    }

    @Test
    public void shouldGetAllSpecialistEmployeesWhenSearchIsNull() {
        // Arrange
        List<SpecialistEmployee> expectedList = new ArrayList<>();
        expectedList.add(specialistEmployee);

        when(specialistEmployeeRepository.findAll()).thenReturn(expectedList);

        // Act
        List<SpecialistEmployee> result = specialistEmployeeService.getSpecialistEmployees(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(specialistEmployeeRepository, times(1)).findAll();
        verify(specialistEmployeeRepository, times(0)).findByNombresAndApellidos(any(), any());
    }

    @Test
    public void shouldGetSpecialistEmployeesBySearch() {
        // Arrange
        String searchQuery = "Carlos";
        List<SpecialistEmployee> expectedList = new ArrayList<>();
        expectedList.add(specialistEmployee);

        when(specialistEmployeeRepository.findByNombresAndApellidos(searchQuery, searchQuery)).thenReturn(expectedList);

        // Act
        List<SpecialistEmployee> result = specialistEmployeeService.getSpecialistEmployees(searchQuery);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(specialistEmployeeRepository, times(1)).findByNombresAndApellidos(searchQuery, searchQuery);
        verify(specialistEmployeeRepository, times(0)).findAll();
    }

    @Test
    public void shouldUpdateSpecialistEmployeeSuccessfully() throws NotFoundException, DuplicatedEntryException {
        // Arrange
        String newName = "Luis Alberto";
        String newLastName = "Martinez Gomez";
        String newDpi = "9876543210987";

        UpdateSpecialistEmpleoyeeRequestDTO updateDTO = new UpdateSpecialistEmpleoyeeRequestDTO(
                newName, newLastName, newDpi);

        SpecialistEmployee existingEmployee = new SpecialistEmployee();
        existingEmployee.setId(SPECIALIST_ID);
        existingEmployee.setNombres(NOMBRES);
        existingEmployee.setApellidos(APELLIDOS);
        existingEmployee.setDpi(DPI);

        when(specialistEmployeeRepository.existsById(SPECIALIST_ID)).thenReturn(true);
        when(specialistEmployeeRepository.existsByDpiAndIdNot(newDpi, SPECIALIST_ID)).thenReturn(false);
        when(specialistEmployeeRepository.findById(SPECIALIST_ID)).thenReturn(Optional.of(existingEmployee));
        when(specialistEmployeeRepository.save(any(SpecialistEmployee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SpecialistEmployee updatedEmployee = specialistEmployeeService.updateSpecialistEmployee(updateDTO,
                SPECIALIST_ID);

        // Assert
        assertNotNull(updatedEmployee);

        assertAll("Campos actualizados",
                () -> assertEquals(newName, updatedEmployee.getNombres()),
                () -> assertEquals(newLastName, updatedEmployee.getApellidos()),
                () -> assertEquals(newDpi, updatedEmployee.getDpi()));

        ArgumentCaptor<SpecialistEmployee> captor = ArgumentCaptor.forClass(SpecialistEmployee.class);
        verify(specialistEmployeeRepository).save(captor.capture());

        SpecialistEmployee capturedEmployee = captor.getValue();
        assertEquals(newName, capturedEmployee.getNombres());
        assertEquals(newLastName, capturedEmployee.getApellidos());
        assertEquals(newDpi, capturedEmployee.getDpi());

        verify(specialistEmployeeRepository, times(1)).existsById(SPECIALIST_ID);
        verify(specialistEmployeeRepository, times(1)).existsByDpiAndIdNot(newDpi, SPECIALIST_ID);
        verify(specialistEmployeeRepository, times(1)).findById(SPECIALIST_ID);
        verify(specialistEmployeeRepository, times(1)).save(any(SpecialistEmployee.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSpecialistEmployeeDoesNotExist() {
        // Arrange
        UpdateSpecialistEmpleoyeeRequestDTO updateDTO = new UpdateSpecialistEmpleoyeeRequestDTO(
                "Luis", "Martinez", "9876543210987");

        when(specialistEmployeeRepository.existsById(SPECIALIST_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            specialistEmployeeService.updateSpecialistEmployee(updateDTO, SPECIALIST_ID);
        });

        verify(specialistEmployeeRepository, times(1)).existsById(SPECIALIST_ID);
        verify(specialistEmployeeRepository, never()).existsByDpiAndIdNot(any(), any());
        verify(specialistEmployeeRepository, never()).findById(any());
        verify(specialistEmployeeRepository, never()).save(any());
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenUpdatingWithExistingDpi() {
        // Arrange
        String duplicateDpi = "9876543210987";
        UpdateSpecialistEmpleoyeeRequestDTO updateDTO = new UpdateSpecialistEmpleoyeeRequestDTO(
                "Luis", "Martinez", duplicateDpi);

        when(specialistEmployeeRepository.existsById(SPECIALIST_ID)).thenReturn(true);
        when(specialistEmployeeRepository.existsByDpiAndIdNot(duplicateDpi, SPECIALIST_ID)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedEntryException.class, () -> {
            specialistEmployeeService.updateSpecialistEmployee(updateDTO, SPECIALIST_ID);
        });

        verify(specialistEmployeeRepository, times(1)).existsById(SPECIALIST_ID);
        verify(specialistEmployeeRepository, times(1)).existsByDpiAndIdNot(duplicateDpi, SPECIALIST_ID);
        verify(specialistEmployeeRepository, never()).findById(any());
        verify(specialistEmployeeRepository, never()).save(any());
    }

}
