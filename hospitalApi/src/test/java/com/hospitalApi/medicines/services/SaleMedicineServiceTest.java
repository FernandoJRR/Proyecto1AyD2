package com.hospitalApi.medicines.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private static final Integer SALE_QUANTITY = 5;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        medicine = new Medicine(
                MEDICINE_ID,
                MEDICINE_NAME,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE);

        saleMedicine = new SaleMedicine(medicine, SALE_QUANTITY);
        saleMedicine.setId(SALE_MEDICINE_ID);

        Patient patient = new Patient("Jose Jose", "Sosa Ortiz", "123456789");
        consult = new Consult(CONSULT_ID, patient, false, 200.00, 200.00);
    }

    @Test
    public void shouldFindSaleMedicineByIdSuccessfully() throws NotFoundException {
        when(saleMedicineRepository.findById(SALE_MEDICINE_ID)).thenReturn(Optional.of(saleMedicine));

        SaleMedicine result = saleMedicineService.findById(SALE_MEDICINE_ID);

        assertNotNull(result);
        assertEquals(SALE_MEDICINE_ID, result.getId());
        verify(saleMedicineRepository, times(1)).findById(SALE_MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSaleMedicineNotFoundById() {
        when(saleMedicineRepository.findById(SALE_MEDICINE_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.findById(SALE_MEDICINE_ID);
        });

        verify(saleMedicineRepository, times(1)).findById(SALE_MEDICINE_ID);
    }

    @Test
    public void shouldCreateSaleMedicineSuccessfully() throws NotFoundException {
        // ARRANGE
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(saleMedicineRepository.save(any(SaleMedicine.class))).thenReturn(saleMedicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(medicine);

        // ACT
        SaleMedicine result = saleMedicineService.createSaleMedicine(MEDICINE_ID, SALE_QUANTITY);

        // ASSERT
        assertNotNull(result);
        assertEquals(medicine, result.getMedicine());
        assertEquals(SALE_QUANTITY, result.getQuantity());

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(1)).save(any(SaleMedicine.class));
        verify(forMedicinePort, times(1)).subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY);
    }

    @Test
    public void shouldThrowNotFoundWhenCreatingSaleMedicineIfMedicineNotFound() throws NotFoundException {
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(MEDICINE_ID, SALE_QUANTITY);
        });

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(0)).save(any(SaleMedicine.class));
    }

    @Test
    public void shouldThrowNotFoundWhenCreatingSaleMedicineIfStockIsInsufficient() throws NotFoundException {
        medicine.setQuantity(2); // stock insuficiente
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);

        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(MEDICINE_ID, SALE_QUANTITY);
        });

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(0)).save(any(SaleMedicine.class));
    }

    @Test
    public void shouldGetSalesMedicinesByMedicineIdSuccessfully() throws NotFoundException {
        List<SaleMedicine> sales = new ArrayList<>();
        sales.add(saleMedicine);

        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(saleMedicineRepository.findByMedicineId(MEDICINE_ID)).thenReturn(sales);

        List<SaleMedicine> result = saleMedicineService.getSalesMedicinesByMedicineId(MEDICINE_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(1)).findByMedicineId(MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundWhenGetSalesMedicinesByNonexistentMedicineId() throws NotFoundException {
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.getSalesMedicinesByMedicineId(MEDICINE_ID);
        });

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(0)).findByMedicineId(MEDICINE_ID);
    }

    @Test
    public void shouldGetSalesMedicinesBetweenDatesSuccessfully() {
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";

        List<SaleMedicine> sales = new ArrayList<>();
        sales.add(saleMedicine);

        when(saleMedicineRepository.findByCreatedAtBetween(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate))).thenReturn(sales);

        List<SaleMedicine> result = saleMedicineService.getSalesMedicineBetweenDates(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(saleMedicineRepository, times(1)).findByCreatedAtBetween(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate));
    }

    @Test
    public void shouldGetTotalSalesMedicinesBetweenDatesSuccessfully() {
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        Double expectedTotal = 1000.0;

        when(saleMedicineRepository.totalSalesMedicinesBetweenDates(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate))).thenReturn(expectedTotal);

        Double result = saleMedicineService.totalSalesMedicinesBetweenDates(startDate, endDate);

        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(saleMedicineRepository, times(1)).totalSalesMedicinesBetweenDates(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate));
    }

    @Test
    public void shouldGetTotalSalesMedicinesByMedicineSuccessfully() throws NotFoundException {
        Double expectedTotal = 500.0;

        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(medicine);
        when(saleMedicineRepository.totalSalesMedicinesByMedicine(MEDICINE_ID)).thenReturn(expectedTotal);

        Double result = saleMedicineService.totalSalesMedicinesByMedicine(MEDICINE_ID);

        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(1)).totalSalesMedicinesByMedicine(MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundWhenGetTotalSalesMedicinesByNonexistentMedicineId() throws NotFoundException {
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.totalSalesMedicinesByMedicine(MEDICINE_ID);
        });

        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(0)).totalSalesMedicinesByMedicine(MEDICINE_ID);
    }

    @Test
    public void shouldGetAllSalesMedicinesSuccessfully() {
        List<SaleMedicine> sales = new ArrayList<>();
        sales.add(saleMedicine);

        when(saleMedicineRepository.findAll()).thenReturn(sales);

        List<SaleMedicine> result = saleMedicineService.getAllSalesMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(saleMedicineRepository, times(1)).findAll();
    }

    @Test
    public void shouldCreateSaleMedicineWithConsultSuccessfully() throws NotFoundException {
        // Arrange
        Consult mockConsult = new Consult();
        mockConsult.setId(CONSULT_ID);

        Medicine mockMedicine = new Medicine();
        mockMedicine.setId(MEDICINE_ID);
        mockMedicine.setQuantity(20);
        mockMedicine.setPrice(50.0);

        when(forConsultPort.findById(CONSULT_ID)).thenReturn(mockConsult);
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(mockMedicine);
        when(forMedicinePort.subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY)).thenReturn(mockMedicine);

        ArgumentCaptor<SaleMedicine> captor = ArgumentCaptor.forClass(SaleMedicine.class);

        SaleMedicine savedSaleMedicine = new SaleMedicine(mockConsult, mockMedicine, SALE_QUANTITY);
        when(saleMedicineRepository.save(any(SaleMedicine.class))).thenReturn(savedSaleMedicine);

        // Act
        SaleMedicine result = saleMedicineService.createSaleMedicine(CONSULT_ID, MEDICINE_ID, SALE_QUANTITY);

        // Assert
        assertNotNull(result, "El resultado no debe ser null");

        assertAll("Verificación del SaleMedicine devuelto",
                () -> assertEquals(mockConsult.getId(), result.getConsult().getId(), "El id del consult es incorrecto"),
                () -> assertEquals(mockMedicine.getId(), result.getMedicine().getId(),
                        "El id de la medicina es incorrecto"),
                () -> assertEquals(SALE_QUANTITY, result.getQuantity(), "La cantidad es incorrecta"));

        verify(saleMedicineRepository, times(1)).save(captor.capture());
        SaleMedicine captured = captor.getValue();

        assertAll("Validación del objeto persistido en save",
                () -> assertNotNull(captured.getConsult(), "El consult no debería ser null"),
                () -> assertNotNull(captured.getMedicine(), "El medicine no debería ser null"),
                () -> assertEquals(mockConsult.getId(), captured.getConsult().getId(),
                        "El id del consult capturado es incorrecto"),
                () -> assertEquals(mockMedicine.getId(), captured.getMedicine().getId(),
                        "El id del medicine capturado es incorrecto"),
                () -> assertEquals(SALE_QUANTITY, captured.getQuantity(), "La cantidad capturada es incorrecta"));

        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(forMedicinePort, times(1)).subtractStockMedicine(MEDICINE_ID, SALE_QUANTITY);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findById(CONSULT_ID)).thenThrow(new NotFoundException("Consulta no encontrada"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(CONSULT_ID, MEDICINE_ID, SALE_QUANTITY);
        });

        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forMedicinePort, times(0)).getMedicine(any());
        verify(saleMedicineRepository, times(0)).save(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenMedicineHasInsufficientStock() throws NotFoundException {
        // Arrange
        Consult mockConsult = new Consult();
        mockConsult.setId(CONSULT_ID);

        Medicine mockMedicine = new Medicine();
        mockMedicine.setId(MEDICINE_ID);
        mockMedicine.setQuantity(2);

        when(forConsultPort.findById(CONSULT_ID)).thenReturn(mockConsult);
        when(forMedicinePort.getMedicine(MEDICINE_ID)).thenReturn(mockMedicine);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            saleMedicineService.createSaleMedicine(CONSULT_ID, MEDICINE_ID, SALE_QUANTITY);
        });

        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forMedicinePort, times(1)).getMedicine(MEDICINE_ID);
        verify(saleMedicineRepository, times(0)).save(any());
        verify(forMedicinePort, times(0)).subtractStockMedicine(any(), any());
    }

    @Test
    public void shouldGetSalesMedicinesByConsultIdSuccessfully() throws NotFoundException {
        List<SaleMedicine> sales = new ArrayList<>();
        sales.add(saleMedicine);

        when(forConsultPort.findById(CONSULT_ID)).thenReturn(consult);
        when(saleMedicineRepository.findByConsultId(CONSULT_ID)).thenReturn(sales);

        List<SaleMedicine> result = saleMedicineService.getSalesMedicinesByConsultId(CONSULT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(saleMedicine, result.get(0));

        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository).findByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldGetTotalSalesMedicinesByConsultSuccessfully() throws NotFoundException {
        Double expectedTotal = 250.0;

        when(forConsultPort.findById(CONSULT_ID)).thenReturn(consult);
        when(saleMedicineRepository.totalSalesMedicinesByConsult(CONSULT_ID)).thenReturn(expectedTotal);

        Double result = saleMedicineService.totalSalesMedicinesByConsult(CONSULT_ID);

        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository).totalSalesMedicinesByConsult(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultIdDoesNotExistForGetTotalSales() throws NotFoundException {
        when(forConsultPort.findById(CONSULT_ID)).thenThrow(new NotFoundException("Consulta no encontrada"));

        assertThrows(NotFoundException.class, () -> saleMedicineService.totalSalesMedicinesByConsult(CONSULT_ID));

        verify(forConsultPort).findById(CONSULT_ID);
        verify(saleMedicineRepository, never()).totalSalesMedicinesByConsult(anyString());
    }
}
