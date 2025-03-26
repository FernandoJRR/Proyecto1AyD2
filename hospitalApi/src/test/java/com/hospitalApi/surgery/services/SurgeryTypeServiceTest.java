package com.hospitalApi.surgery.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.UpdateSurgeryTypeRequestDTO;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.repositories.SurgeryTypeRepository;

@ExtendWith(MockitoExtension.class)
public class SurgeryTypeServiceTest {

    @Mock
    private SurgeryTypeRepository surgeryTypeRepository;

    @InjectMocks
    private SurgeryTypeService surgeryTypeService;

    private static final String SURGERY_TYPE_ID = "SURGERY-TYPE-123";
    private static final String TYPE = "Cirugía Cardiaca";
    private static final String DESCRIPTION = "Intervención en el corazón";
    private static final Double SPECIALIST_PAYMENT = 1500.00;
    private static final Double HOSPITAL_COST = 5000.00;
    private static final Double SURGERY_COST = 7000.00;

    private SurgeryType surgeryType;

    @BeforeEach
    public void setUp() {
        surgeryType = new SurgeryType();
        surgeryType.setId(SURGERY_TYPE_ID);
        surgeryType.setType(TYPE);
        surgeryType.setDescription(DESCRIPTION);
        surgeryType.setSpecialistPayment(SPECIALIST_PAYMENT);
        surgeryType.setHospitalCost(HOSPITAL_COST);
        surgeryType.setSurgeryCost(SURGERY_COST);
    }

    @Test
    public void shouldGetSurgeryTypesWhenSearchIsNull() {
        // Arrange
        List<SurgeryType> expectedList = List.of(surgeryType);
        when(surgeryTypeRepository.findAll()).thenReturn(expectedList);

        // Act
        List<SurgeryType> result = surgeryTypeService.getSurgeryTypes(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TYPE, result.get(0).getType());

        verify(surgeryTypeRepository, times(1)).findAll();
        verify(surgeryTypeRepository, never()).findByTypeContainingIgnoreCase(any());
    }

    @Test
    public void shouldGetSurgeryTypesWhenSearchIsProvided() {
        // Arrange
        String search = "Cardiaca";
        List<SurgeryType> expectedList = List.of(surgeryType);
        when(surgeryTypeRepository.findByTypeContainingIgnoreCase(search)).thenReturn(expectedList);

        // Act
        List<SurgeryType> result = surgeryTypeService.getSurgeryTypes(search);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TYPE, result.get(0).getType());

        verify(surgeryTypeRepository, times(1)).findByTypeContainingIgnoreCase(search);
        verify(surgeryTypeRepository, never()).findAll();
    }

    @Test
    public void shouldGetSurgeryTypeByIdSuccessfully() throws NotFoundException {
        // Arrange
        when(surgeryTypeRepository.findById(SURGERY_TYPE_ID)).thenReturn(Optional.of(surgeryType));

        // Act
        SurgeryType result = surgeryTypeService.getSurgeryType(SURGERY_TYPE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(SURGERY_TYPE_ID, result.getId());

        verify(surgeryTypeRepository, times(1)).findById(SURGERY_TYPE_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSurgeryTypeIdDoesNotExist() {
        // Arrange
        when(surgeryTypeRepository.findById(SURGERY_TYPE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> surgeryTypeService.getSurgeryType(SURGERY_TYPE_ID));

        verify(surgeryTypeRepository, times(1)).findById(SURGERY_TYPE_ID);
    }

    @Test
    public void shouldCreateSurgeryTypeSuccessfully() throws DuplicatedEntryException {
        // Arrange
        when(surgeryTypeRepository.existsByType(TYPE)).thenReturn(false);
        when(surgeryTypeRepository.save(any(SurgeryType.class))).thenReturn(surgeryType);

        // Act
        SurgeryType result = surgeryTypeService.createSurgeryType(surgeryType);

        // Assert
        assertNotNull(result);

        ArgumentCaptor<SurgeryType> captor = ArgumentCaptor.forClass(SurgeryType.class);
        verify(surgeryTypeRepository).save(captor.capture());

        SurgeryType captured = captor.getValue();
        assertEquals(TYPE, captured.getType());

        verify(surgeryTypeRepository, times(1)).existsByType(TYPE);
        verify(surgeryTypeRepository, times(1)).save(any(SurgeryType.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenCreatingExistingSurgeryType() {
        // Arrange
        when(surgeryTypeRepository.existsByType(TYPE)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedEntryException.class, () -> surgeryTypeService.createSurgeryType(surgeryType));

        verify(surgeryTypeRepository, times(1)).existsByType(TYPE);
        verify(surgeryTypeRepository, never()).save(any(SurgeryType.class));
    }

    @Test
    public void shouldUpdateSurgeryTypeSuccessfully() throws DuplicatedEntryException, NotFoundException {
        // Arrange
        String newType = "Cirugía Neurológica";
        String newDescription = "Intervención en el cerebro";
        Double newSpecialistPayment = 2000.00;
        Double newHospitalCost = 6000.00;
        Double newSurgeryCost = 8000.00;

        UpdateSurgeryTypeRequestDTO updateDTO = new UpdateSurgeryTypeRequestDTO();
        updateDTO.setType(newType);
        updateDTO.setDescription(newDescription);
        updateDTO.setSpecialistPayment(newSpecialistPayment);
        updateDTO.setHospitalCost(newHospitalCost);
        updateDTO.setSurgeryCost(newSurgeryCost);

        when(surgeryTypeRepository.existsByTypeAndIdNot(newType, SURGERY_TYPE_ID)).thenReturn(false);
        when(surgeryTypeRepository.findById(SURGERY_TYPE_ID)).thenReturn(Optional.of(surgeryType));
        when(surgeryTypeRepository.save(any(SurgeryType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SurgeryType updated = surgeryTypeService.updateSurgeryType(updateDTO, SURGERY_TYPE_ID);

        // Assert
        assertNotNull(updated);
        assertAll(
                () -> assertEquals(newType, updated.getType()),
                () -> assertEquals(newDescription, updated.getDescription()),
                () -> assertEquals(newSpecialistPayment, updated.getSpecialistPayment()),
                () -> assertEquals(newHospitalCost, updated.getHospitalCost()),
                () -> assertEquals(newSurgeryCost, updated.getSurgeryCost()));

        verify(surgeryTypeRepository, times(1)).existsByTypeAndIdNot(newType, SURGERY_TYPE_ID);
        verify(surgeryTypeRepository, times(1)).findById(SURGERY_TYPE_ID);
        verify(surgeryTypeRepository, times(1)).save(any(SurgeryType.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenUpdatingWithExistingType() {
        // Arrange
        String duplicateType = "Cirugía Ocular";

        UpdateSurgeryTypeRequestDTO updateDTO = new UpdateSurgeryTypeRequestDTO();
        updateDTO.setType(duplicateType);

        when(surgeryTypeRepository.existsByTypeAndIdNot(duplicateType, SURGERY_TYPE_ID)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedEntryException.class,
                () -> surgeryTypeService.updateSurgeryType(updateDTO, SURGERY_TYPE_ID));

        verify(surgeryTypeRepository, times(1)).existsByTypeAndIdNot(duplicateType, SURGERY_TYPE_ID);
        verify(surgeryTypeRepository, never()).findById(anyString());
        verify(surgeryTypeRepository, never()).save(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingWithNonexistentId() {
        // Arrange
        String newType = "Cirugía Neurológica";

        UpdateSurgeryTypeRequestDTO updateDTO = new UpdateSurgeryTypeRequestDTO();
        updateDTO.setType(newType);

        when(surgeryTypeRepository.existsByTypeAndIdNot(newType, SURGERY_TYPE_ID)).thenReturn(false);
        when(surgeryTypeRepository.findById(SURGERY_TYPE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> surgeryTypeService.updateSurgeryType(updateDTO, SURGERY_TYPE_ID));

        verify(surgeryTypeRepository, times(1)).existsByTypeAndIdNot(newType, SURGERY_TYPE_ID);
        verify(surgeryTypeRepository, times(1)).findById(SURGERY_TYPE_ID);
        verify(surgeryTypeRepository, never()).save(any());
    }
}
