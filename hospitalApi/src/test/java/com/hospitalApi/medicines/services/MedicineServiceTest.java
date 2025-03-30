package com.hospitalApi.medicines.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.repositories.MedicineRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    private CreateMedicineRequestDTO createDTO;
    private UpdateMedicineRequestDTO updateDTO;
    private Medicine medicine;

    // Constantes
    private static final String MEDICINE_ID = "12312-12312-12312-12312";
    private static final String MEDICINE_NAME = "Paracetamol";
    private static final String MEDICINE_DESCRIPTION = "Medicina para el dolor de cabeza";
    private static final Integer MEDICINE_QUANTITY = 10;
    private static final Integer MEDICINE_MIN_QUANTITY = 5;
    private static final Double MEDICINE_PRICE = 5.00;
    private static final Double MEDICINE_COST = 4.00;;

    private static final String MEDICINE_ID_2 = "23434-23434-23434-23434";
    private static final String MEDICINE_NAME_2 = "Amoxicilina";
    private static final String MEDICINE_DESCRIPTION_2 = "Antibiótico de amplio espectro";
    private static final Integer MEDICINE_QUANTITY_2 = 3;
    private static final Integer MEDICINE_MIN_QUANTITY_2 = 5;
    private static final Double MEDICINE_PRICE_2 = 8.50;
    private static final Double MEDICINE_COST_2 = 7.00;;

    private static final String MEDICINE_NAME_UPDATED = "Ibuprofeno";

    @BeforeEach
    public void setUp() {
        createDTO = new CreateMedicineRequestDTO(
                MEDICINE_NAME,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE,
                   MEDICINE_COST);

        updateDTO = new UpdateMedicineRequestDTO(
                MEDICINE_NAME_UPDATED,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE,
                MEDICINE_COST);

        medicine = new Medicine(
                MEDICINE_ID,
                MEDICINE_NAME,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE ,
                MEDICINE_COST);
    }

    @Test
    public void shouldCreateMedicineSuccessfully() throws DuplicatedEntryException {
        // ARRANGE
        when(medicineRepository.existsByName(MEDICINE_NAME)).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        // ACT
        Medicine result = medicineService.createMedicine(createDTO);

        // ASSERT
        ArgumentCaptor<Medicine> medicineCaptor = ArgumentCaptor.forClass(Medicine.class);
        verify(medicineRepository).save(medicineCaptor.capture());
        Medicine captured = medicineCaptor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(MEDICINE_NAME, captured.getName()),
                () -> assertEquals(MEDICINE_DESCRIPTION, captured.getDescription()),
                () -> assertEquals(MEDICINE_QUANTITY, captured.getQuantity()),
                () -> assertEquals(MEDICINE_MIN_QUANTITY, captured.getMinQuantity()),
                () -> assertEquals(MEDICINE_PRICE, captured.getPrice()));

        verify(medicineRepository, times(1)).existsByName(MEDICINE_NAME);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    public void shouldNotCreateMedicineWhenDuplicateName() {
        // ARRANGE
        when(medicineRepository.existsByName(MEDICINE_NAME)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(DuplicatedEntryException.class, () -> medicineService.createMedicine(createDTO));

        verify(medicineRepository, times(1)).existsByName(MEDICINE_NAME);
        verify(medicineRepository, times(0)).save(any(Medicine.class));
    }

    @Test
    public void shouldUpdateMedicineSuccessfully() throws DuplicatedEntryException, NotFoundException {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.of(medicine));
        when(medicineRepository.existsByName(MEDICINE_NAME_UPDATED)).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        // ACT
        Medicine updated = medicineService.updateMedicine(MEDICINE_ID, updateDTO);

        // ASSERT
        assertAll(
                () -> assertEquals(MEDICINE_NAME_UPDATED, updated.getName()),
                () -> assertEquals(MEDICINE_DESCRIPTION, updated.getDescription()),
                () -> assertEquals(MEDICINE_QUANTITY, updated.getQuantity()));

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(1)).existsByName(MEDICINE_NAME_UPDATED);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    public void shouldNotUpdateMedicineWhenNotFound() {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> medicineService.updateMedicine(MEDICINE_ID, updateDTO));

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(0)).save(any(Medicine.class));
    }

    @Test
    public void shouldNotUpdateMedicineWhenDuplicateName() {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.of(medicine));
        when(medicineRepository.existsByName(MEDICINE_NAME_UPDATED)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(DuplicatedEntryException.class, () -> medicineService.updateMedicine(MEDICINE_ID, updateDTO));

        verify(medicineRepository, times(1)).existsByName(MEDICINE_NAME_UPDATED);
        verify(medicineRepository, times(0)).save(any(Medicine.class));
    }

    @Test
    public void shouldGetMedicineSuccessfully() throws NotFoundException {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.of(medicine));

        // ACT
        Medicine result = medicineService.getMedicine(MEDICINE_ID);

        // ASSERT
        assertNotNull(result);
        assertEquals(MEDICINE_NAME, result.getName());

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
    }

    @Test
    public void shouldThrowNotFoundWhenMedicineNotExists() {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> medicineService.getMedicine(MEDICINE_ID));

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
    }

    @Test
    public void shouldReturnAllMedicinesWhenQueryIsNull() {
        // ARRANGE
        List<Medicine> medicines = new ArrayList<>();
        medicines.add(medicine);

        when(medicineRepository.findAll()).thenReturn(medicines);

        // ACT
        List<Medicine> result = medicineService.getAllMedicines(null);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MEDICINE_NAME, result.get(0).getName());

        // VERIFY
        verify(medicineRepository, times(1)).findAll();
        verify(medicineRepository, times(0)).findByNameContainingIgnoreCase(any());
    }

    @Test
    public void shouldReturnFilteredMedicinesWhenQueryIsProvided() {
        // ARRANGE
        String query = "Paraceta";
        List<Medicine> filteredMedicines = new ArrayList<>();
        filteredMedicines.add(medicine);

        when(medicineRepository.findByNameContainingIgnoreCase(query)).thenReturn(filteredMedicines);

        // ACT
        List<Medicine> result = medicineService.getAllMedicines(query);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MEDICINE_NAME, result.get(0).getName());

        // VERIFY with ArgumentCaptor
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(medicineRepository, times(1)).findByNameContainingIgnoreCase(queryCaptor.capture());
        assertEquals(query, queryCaptor.getValue());

        verify(medicineRepository, times(0)).findAll();
    }

    @Test
    public void shouldReturnMedicinesWithLowStock() {
        // ARRANGE
        List<Medicine> lowStockMedicines = new ArrayList<>();

        Medicine lowStockMedicine = new Medicine(
                MEDICINE_ID_2,
                MEDICINE_NAME_2,
                MEDICINE_DESCRIPTION_2,
                MEDICINE_QUANTITY_2,
                MEDICINE_MIN_QUANTITY_2,
                MEDICINE_PRICE_2,
                MEDICINE_COST_2);

        lowStockMedicines.add(lowStockMedicine);

        // Simulamos el resultado del repositorio
        when(medicineRepository.findMedicinesWithLowStock()).thenReturn(lowStockMedicines);

        // ACT
        List<Medicine> result = medicineService.getMedicinesWithLowStock();

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MEDICINE_ID_2, result.get(0).getId());

        verify(medicineRepository, times(1)).findMedicinesWithLowStock();
    }

    @Test
    public void shouldUpdateStockMedicineSuccessfully() throws NotFoundException {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Integer newQuantity = 15;

        // ACT
        Medicine updatedMedicine = medicineService.updateStockMedicine(MEDICINE_ID, newQuantity);

        // ASSERT
        assertAll(
                () -> assertNotNull(updatedMedicine),
                () -> assertEquals(newQuantity, updatedMedicine.getQuantity()));

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingStockAndMedicineNotFound() {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            medicineService.updateStockMedicine(MEDICINE_ID, 10);
        });

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(0)).save(any(Medicine.class));
    }

    @Test
    public void shouldSumStockMedicineSuccessfully() throws NotFoundException {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Integer quantityToAdd = 5;
        Integer expectedQuantity = medicine.getQuantity() + quantityToAdd;

        // ACT
        Medicine updatedMedicine = medicineService.sumStockMedicine(MEDICINE_ID, quantityToAdd);

        // ASSERT
        assertAll(
                () -> assertNotNull(updatedMedicine),
                () -> assertEquals(expectedQuantity, updatedMedicine.getQuantity()));

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSummingStockAndMedicineNotFound() {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            medicineService.sumStockMedicine(MEDICINE_ID, 5);
        });

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(0)).save(any(Medicine.class));
    }

    @Test
    public void shouldSubtractStockMedicineSuccessfully() throws NotFoundException {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Integer quantityToSubtract = 3;
        Integer expectedQuantity = medicine.getQuantity() - quantityToSubtract;

        // ACT
        Medicine updatedMedicine = medicineService.subtractStockMedicine(MEDICINE_ID, quantityToSubtract);

        // ASSERT
        assertAll(
                () -> assertNotNull(updatedMedicine),
                () -> assertEquals(expectedQuantity, updatedMedicine.getQuantity()));

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSubtractingStockAndMedicineNotFound() {
        // ARRANGE
        when(medicineRepository.findById(MEDICINE_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            medicineService.subtractStockMedicine(MEDICINE_ID, 3);
        });

        verify(medicineRepository, times(1)).findById(MEDICINE_ID);
        verify(medicineRepository, times(0)).save(any(Medicine.class));
    }

}
