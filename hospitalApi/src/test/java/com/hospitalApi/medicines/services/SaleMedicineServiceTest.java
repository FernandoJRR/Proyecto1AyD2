package com.hospitalApi.medicines.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.medicines.dtos.CreateSaleMedicineConsultRequestDTO;
import com.hospitalApi.medicines.dtos.CreateSaleMedicineFarmaciaRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.models.SaleMedicine;
import com.hospitalApi.medicines.ports.ForMedicinePort;
import com.hospitalApi.medicines.repositories.SaleMedicineRepository;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class SaleMedicineServiceTest {

    @Mock
    private SaleMedicineRepository saleMedicineRepository;

    @Mock
    private ForMedicinePort forMedicinePort;

    @Mock
    private ForConsultPort forConsultPort;

    @InjectMocks
    private SaleMedicineService saleMedicineService;

    private Medicine medicine;
    private SaleMedicine saleMedicine;
    private Consult consult;

    private static final String MEDICINE_ID = "12312-12312-12312-12312";
    private static final String CONSULT_ID = "CONSULT-9999";
    private static final String MEDICINE_NAME = "Paracetamol";
    private static final String MEDICINE_DESCRIPTION = "Para el dolor";
    private static final String SALE_MEDICINE_ID = "99999-88888-77777-66666";
    private static final Integer MEDICINE_QUANTITY = 10;
    private static final Integer MEDICINE_MIN_QUANTITY = 5;
    private static final Double MEDICINE_PRICE = 5.00;
    private static final Double MEDICINE_COST = 4.00;;
    private static final Integer SALE_QUANTITY = 5;

    @BeforeEach
    public void setUp() {
        medicine = new Medicine(
                MEDICINE_ID,
                MEDICINE_NAME,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE,
                MEDICINE_COST);

        saleMedicine = new SaleMedicine(medicine, SALE_QUANTITY);
        saleMedicine.setId(SALE_MEDICINE_ID);

        Patient patient = new Patient("Jose Jose", "Sosa Ortiz", "123456789");
        consult = new Consult(CONSULT_ID, patient, false, 200.00, 200.00);
    }

    @Test
    public void shouldReturnSaleMedicineWhenExists() throws NotFoundException {
        // Arrange
        when(saleMedicineRepository.findById(SALE_MEDICINE_ID)).thenReturn(Optional.of(saleMedicine));

        // Act
        SaleMedicine result = saleMedicineService.findById(SALE_MEDICINE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(saleMedicine, result);
        assertEquals(SALE_MEDICINE_ID, result.getId());
        verify(saleMedicineRepository, times(1)).findById(SALE_MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSaleMedicineDoesNotExist() {
        // Arrange
        when(saleMedicineRepository.findById(SALE_MEDICINE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.findById(SALE_MEDICINE_ID);
        });

        verify(saleMedicineRepository, times(1)).findById(SALE_MEDICINE_ID);
    }

    @Test
    public void shouldCreateSaleMedicineSuccessfullyWhenStockIsSufficient() throws NotFoundException {
        // Arrange
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(saleMedicineRepository.save(any(SaleMedicine.class))).thenReturn(saleMedicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(medicine);

        // Act
        SaleMedicine result = saleMedicineService.createSaleMedicine(MEDICINE_ID, SALE_QUANTITY);

        // Assert
        assertNotNull(result);
        assertEquals(medicine, result.getMedicine());
        assertEquals(SALE_QUANTITY, result.getQuantity());

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(1)).save(any(SaleMedicine.class));
        verify(forMedicinePort, times(1)).subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenStockIsInsufficient() throws NotFoundException {
        // Arrange
        medicine.setQuantity(2); // stock insuficiente
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(MEDICINE_ID, SALE_QUANTITY);
        });

        // Verify
        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, never()).save(any());
        verify(forMedicinePort, never()).subtractStockMedicine(anyString(), any());
    }

    @Test
    public void shouldCreateSaleMedicineWithConsultSuccessfully_WhenStockIsSufficient() throws NotFoundException {
        // Arrange
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID)).thenReturn(consult);
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(medicine);
        when(saleMedicineRepository.save(any(SaleMedicine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<SaleMedicine> captor = ArgumentCaptor.forClass(SaleMedicine.class);

        // Act
        SaleMedicine result = saleMedicineService.createSaleMedicine(CONSULT_ID, MEDICINE_ID, SALE_QUANTITY);

        // Assert
        assertNotNull(result);
        assertEquals(medicine, result.getMedicine());
        assertEquals(consult, result.getConsult());
        assertEquals(SALE_QUANTITY, result.getQuantity());

        verify(forConsultPort).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forMedicinePort).getMedicine(MEDICINE_ID);
        verify(forMedicinePort).subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY);
        verify(saleMedicineRepository).save(captor.capture());

        SaleMedicine captured = captor.getValue();
        assertEquals(CONSULT_ID, captured.getConsult().getId());
        assertEquals(MEDICINE_ID, captured.getMedicine().getId());
        assertEquals(SALE_QUANTITY, captured.getQuantity());
    }

    @Test
    public void shouldThrowNotFoundException_WhenStockIsInsufficient() throws NotFoundException {
        // Arrange
        medicine.setQuantity(2); // insuficiente
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID)).thenReturn(consult);
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(CONSULT_ID, MEDICINE_ID, SALE_QUANTITY);
        });

        // Verify
        verify(forConsultPort).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forMedicinePort).getMedicine(MEDICINE_ID);
        verify(forMedicinePort, never()).subtractStockMedicine(any(), any());
        verify(saleMedicineRepository, never()).save(any());
    }

    @Test
    public void shouldThrowNotFoundException_WhenConsultIsInvalid() throws NotFoundException {
        // Arrange
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID))
                .thenThrow(new NotFoundException("Consulta no encontrada"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(CONSULT_ID, MEDICINE_ID, SALE_QUANTITY);
        });

        verify(forConsultPort).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forMedicinePort, never()).getMedicine(any());
        verify(forMedicinePort, never()).subtractStockMedicine(any(), any());
        verify(saleMedicineRepository, never()).save(any());
    }

    @Test
    public void shouldGetSalesMedicinesByConsultIdSuccessfully() throws NotFoundException {
        // Arrange
        List<SaleMedicine> expectedSales = List.of(saleMedicine);
        when(forConsultPort.findById(CONSULT_ID)).thenReturn(consult);
        when(saleMedicineRepository.findByConsultId(CONSULT_ID)).thenReturn(expectedSales);

        // Act
        List<SaleMedicine> result = saleMedicineService.getSalesMedicinesByConsultId(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository).findByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundException_WhenConsultDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findById(CONSULT_ID)).thenThrow(new NotFoundException("Consulta no encontrada"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.getSalesMedicinesByConsultId(CONSULT_ID);
        });

        // Verify
        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository, never()).findByConsultId(anyString());
    }

    @Test
    public void shouldReturnSalesByMedicineIdSuccessfully() throws NotFoundException {
        // Arrange
        List<SaleMedicine> expectedSales = List.of(saleMedicine);
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(saleMedicineRepository.findByMedicineId(MEDICINE_ID)).thenReturn(expectedSales);

        // Act
        List<SaleMedicine> result = saleMedicineService.getSalesMedicinesByMedicineId(MEDICINE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(forMedicinePort).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository).findByMedicineId(MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundException_WhenMedicineDoesNotExist() throws NotFoundException {
        // Arrange
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.getSalesMedicinesByMedicineId(MEDICINE_ID);
        });

        // Verify
        verify(forMedicinePort).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, never()).findByMedicineId(anyString());
    }

    @Test
    public void shouldReturnSalesBetweenDatesSuccessfully() {
        // Arrange
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        List<SaleMedicine> sales = List.of(saleMedicine);

        when(saleMedicineRepository.findByCreatedAtBetween(
                LocalDate.parse(startDate), LocalDate.parse(endDate)))
                .thenReturn(sales);

        // Act
        List<SaleMedicine> result = saleMedicineService.getSalesMedicineBetweenDates(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(saleMedicineRepository).findByCreatedAtBetween(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @Test
    public void shouldReturnTotalSalesBetweenDatesSuccessfully() {
        // Arrange
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        Double expectedTotal = 1500.0;

        when(saleMedicineRepository.totalSalesMedicinesBetweenDates(
                LocalDate.parse(startDate), LocalDate.parse(endDate)))
                .thenReturn(expectedTotal);

        // Act
        Double result = saleMedicineService.totalSalesMedicinesBetweenDates(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(saleMedicineRepository).totalSalesMedicinesBetweenDates(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @Test
    public void shouldReturnZeroWhenNoSalesInDateRange() {
        // Arrange
        String startDate = "2022-01-01";
        String endDate = "2022-12-31";
        when(saleMedicineRepository.totalSalesMedicinesBetweenDates(
                LocalDate.parse(startDate), LocalDate.parse(endDate)))
                .thenReturn(0.0);

        // Act
        Double result = saleMedicineService.totalSalesMedicinesBetweenDates(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result);

        verify(saleMedicineRepository).totalSalesMedicinesBetweenDates(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @Test
    public void shouldReturnTotalSalesByConsultSuccessfully() throws NotFoundException {
        // Arrange
        Double expectedTotal = 250.0;

        when(forConsultPort.findById(CONSULT_ID)).thenReturn(consult);
        when(saleMedicineRepository.totalSalesMedicinesByConsult(CONSULT_ID)).thenReturn(expectedTotal);

        // Act
        Double result = saleMedicineService.totalSalesMedicinesByConsult(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository).totalSalesMedicinesByConsult(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionIfConsultDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findById(CONSULT_ID)).thenThrow(new NotFoundException("Consulta no encontrada"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.totalSalesMedicinesByConsult(CONSULT_ID);
        });

        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository, never()).totalSalesMedicinesByConsult(anyString());
    }

    @Test
    public void shouldReturnTotalSalesByMedicineSuccessfully() throws NotFoundException {
        // Arrange
        Double expectedTotal = 150.0;

        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(saleMedicineRepository.totalSalesMedicinesByMedicine(MEDICINE_ID)).thenReturn(expectedTotal);

        // Act
        Double result = saleMedicineService.totalSalesMedicinesByMedicine(MEDICINE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(forMedicinePort).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository).totalSalesMedicinesByMedicine(MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionIfMedicineDoesNotExist() throws NotFoundException {
        // Arrange
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.totalSalesMedicinesByMedicine(MEDICINE_ID);
        });

        verify(forMedicinePort).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, never()).totalSalesMedicinesByMedicine(anyString());
    }

    @Test
    public void shouldReturnAllSaleMedicinesSuccessfully() {
        // Arrange
        List<SaleMedicine> expectedSales = List.of(saleMedicine);
        when(saleMedicineRepository.findAll()).thenReturn(expectedSales);

        // Act
        List<SaleMedicine> result = saleMedicineService.getAllSalesMedicines();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));
        verify(saleMedicineRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnEmptyListWhenNoSalesExist() {
        // Arrange
        when(saleMedicineRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<SaleMedicine> result = saleMedicineService.getAllSalesMedicines();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(saleMedicineRepository, times(1)).findAll();
    }

    @Test
    public void shouldCreateMultipleSaleMedicinesSuccessfully() throws NotFoundException {
        // Arrange
        List<CreateSaleMedicineFarmaciaRequestDTO> requestList = List.of(
                new CreateSaleMedicineFarmaciaRequestDTO(MEDICINE_ID, SALE_QUANTITY),
                new CreateSaleMedicineFarmaciaRequestDTO(MEDICINE_ID, SALE_QUANTITY),
                new CreateSaleMedicineFarmaciaRequestDTO(MEDICINE_ID, SALE_QUANTITY));

        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(medicine);
        when(saleMedicineRepository.save(any(SaleMedicine.class))).thenReturn(saleMedicine);

        // Act
        List<SaleMedicine> result = saleMedicineService.createSaleMedicines(requestList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(forMedicinePort, times(3)).getMedicine(MEDICINE_ID);
        verify(forMedicinePort, times(3)).subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY);
        verify(saleMedicineRepository, times(3)).save(any(SaleMedicine.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenOneMedicineIsInvalid() throws NotFoundException {
        // Arrange
        CreateSaleMedicineFarmaciaRequestDTO valid = new CreateSaleMedicineFarmaciaRequestDTO(MEDICINE_ID,
                SALE_QUANTITY);
        CreateSaleMedicineFarmaciaRequestDTO invalid = new CreateSaleMedicineFarmaciaRequestDTO("INVALID_ID",
                SALE_QUANTITY);

        List<CreateSaleMedicineFarmaciaRequestDTO> requestList = List.of(valid, invalid);

        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(medicine);
        when(saleMedicineRepository.save(any(SaleMedicine.class))).thenReturn(saleMedicine);
        when(forMedicinePort.getMedicine("INVALID_ID")).thenThrow(new NotFoundException("No existe"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicines(requestList);
        });

        verify(saleMedicineRepository, times(1)).save(any()); // Solo 1 debe pasar antes del error
    }

    @Test
    public void shouldCreateMultipleSaleMedicinesForConsultSuccessfully() throws NotFoundException {
        // Arrange
        List<CreateSaleMedicineConsultRequestDTO> requestList = List.of(
                new CreateSaleMedicineConsultRequestDTO(MEDICINE_ID, CONSULT_ID, SALE_QUANTITY),
                new CreateSaleMedicineConsultRequestDTO(MEDICINE_ID, CONSULT_ID, SALE_QUANTITY),
                new CreateSaleMedicineConsultRequestDTO(MEDICINE_ID, CONSULT_ID, SALE_QUANTITY));

        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID)).thenReturn(consult);
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(medicine);
        when(saleMedicineRepository.save(any(SaleMedicine.class))).thenReturn(saleMedicine);

        // Act
        List<SaleMedicine> result = saleMedicineService.createSaleMedicinesForConsult(requestList);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        verify(forConsultPort, times(3)).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forMedicinePort, times(3)).getMedicine(MEDICINE_ID);
        verify(forMedicinePort, times(3)).subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY);
        verify(saleMedicineRepository, times(3)).save(any(SaleMedicine.class));
    }
}
