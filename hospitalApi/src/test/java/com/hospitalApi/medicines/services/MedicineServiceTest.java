package com.hospitalApi.medicines.services;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.medicines.models.Medicine;
import com.hospitalApi.medicines.repositories.MedicineRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    private CreateMedicineRequestDTO createDTO;
    private UpdateMedicineRequestDTO updateDTO;
    private Medicine medicine;

    // Constantes
    private static final Long MEDICINE_ID = 1L;
    private static final String MEDICINE_NAME = "Paracetamol";
    private static final String MEDICINE_DESCRIPTION = "Medicina para el dolor de cabeza";
    private static final Integer MEDICINE_QUANTITY = 10;
    private static final Integer MEDICINE_MIN_QUANTITY = 5;
    private static final Double MEDICINE_PRICE = 5.00;

    private static final String MEDICINE_NAME_UPDATED = "Ibuprofeno";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        createDTO = new CreateMedicineRequestDTO(
                MEDICINE_NAME,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE
        );

        updateDTO = new UpdateMedicineRequestDTO(
                MEDICINE_NAME_UPDATED,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE
        );

        medicine = new Medicine(
                MEDICINE_ID,
                MEDICINE_NAME,
                MEDICINE_DESCRIPTION,
                MEDICINE_QUANTITY,
                MEDICINE_MIN_QUANTITY,
                MEDICINE_PRICE
        );
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
                () -> assertEquals(MEDICINE_PRICE, captured.getPrice())
        );

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
                () -> assertEquals(MEDICINE_QUANTITY, updated.getQuantity())
        );

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
    public void shouldReturnAllMedicines() {
        // ARRANGE
        List<Medicine> medicines = new ArrayList<>();
        medicines.add(medicine);
        when(medicineRepository.findAll()).thenReturn(medicines);

        // ACT
        List<Medicine> result = medicineService.getAllMedicines();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(MEDICINE_NAME, result.get(0).getName());

        verify(medicineRepository, times(1)).findAll();
    }
}
