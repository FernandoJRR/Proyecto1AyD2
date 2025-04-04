package com.hospitalApi.surgery.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.DeleteSurgeryResponseDTO;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;
import com.hospitalApi.surgery.repositories.SurgeryRepository;

@ExtendWith(MockitoExtension.class)
public class SugeryServicesTest {

    @Mock
    private ForConsultPort forConsultPort;

    @Mock
    private ForSurgeryTypePort forSurgeryTypePort;

    @Mock
    private SurgeryRepository surgeryRepository;

    @InjectMocks
    private SurgeryServices sugeryServices;

    // Constantes
    private static final String SURGERY_ID = "SURGERY-1234";

    private static final String CONSULT_ID = "CONSULT-5678";
    private static final boolean IS_INTERNADO = false;
    private static final BigDecimal COSTO_CONSULTA = new BigDecimal(500);
    private static final BigDecimal COSTO_TOTAL = new BigDecimal(500);

    private static final String SURGERY_TYPE_ID = "TYPE-9999";
    private static final String SURGERY_TYPE_NAME = "Cirugía Mayor";
    private static final String SURGERY_TYPE_DESCRIPTION = "Cirugía mayor en hospital";
    private static final BigDecimal SPECIALIST_PAYMENT = new BigDecimal(1000);
    private static final BigDecimal HOSPITAL_COST = new BigDecimal(3000);
    private static final BigDecimal SURGERY_COST = new BigDecimal(4000);

    private Consult consult;
    private SurgeryType surgeryType;
    private Surgery surgery;

    @BeforeEach
    void setUp() {
        consult = new Consult(
                CONSULT_ID,
                null,
                IS_INTERNADO,
                COSTO_CONSULTA,
                COSTO_TOTAL);

        surgeryType = new SurgeryType(
                SURGERY_TYPE_NAME,
                SURGERY_TYPE_DESCRIPTION,
                SPECIALIST_PAYMENT,
                HOSPITAL_COST,
                SURGERY_COST);
        surgeryType.setId(SURGERY_TYPE_ID);

        surgery = new Surgery(consult, surgeryType, surgeryType.getHospitalCost(), surgeryType.getSurgeryCost());
        surgery.setId(SURGERY_ID);
    }

    @Test
    void shouldReturnAllSurgeriesSuccessfully() {
        // Arrange
        List<Surgery> surgeries = List.of(surgery);
        when(surgeryRepository.findAll()).thenReturn(surgeries);

        // Act
        List<Surgery> result = sugeryServices.getSurgerys();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(surgery, result.get(0));
        verify(surgeryRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnSurgeryByIdSuccessfully() throws NotFoundException {
        // Arrange
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.of(surgery));

        // Act
        Surgery result = sugeryServices.getSurgery(SURGERY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(SURGERY_ID, result.getId());
        verify(surgeryRepository, times(1)).findById(SURGERY_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSurgeryByIdNotExists() {
        // Arrange
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> sugeryServices.getSurgery(SURGERY_ID));

        assertEquals("No se encontró la cirugía con id " + SURGERY_ID, ex.getMessage());
        verify(surgeryRepository, times(1)).findById(SURGERY_ID);
    }

    @Test
    void shouldCreateSurgerySuccessfully() throws NotFoundException {
        // Arrange
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID)).thenReturn(consult);
        when(forSurgeryTypePort.getSurgeryType(SURGERY_TYPE_ID)).thenReturn(surgeryType);
        when(surgeryRepository.save(any(Surgery.class))).thenAnswer(i -> i.getArgument(0)); // devuelve el mismo objeto

        ArgumentCaptor<Surgery> surgeryCaptor = ArgumentCaptor.forClass(Surgery.class);

        // Act
        Surgery result = sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CONSULT_ID, result.getConsult().getId());
        assertEquals(SURGERY_TYPE_ID, result.getSurgeryType().getId());
        assertEquals(surgeryType.getHospitalCost(), result.getHospitalCost());
        assertEquals(surgeryType.getSurgeryCost(), result.getSurgeryCost());

        verify(surgeryRepository).save(surgeryCaptor.capture());

        Surgery capturedSurgery = surgeryCaptor.getValue();
        assertEquals(CONSULT_ID, capturedSurgery.getConsult().getId());
        assertEquals(SURGERY_TYPE_ID, capturedSurgery.getSurgeryType().getId());
        assertEquals(HOSPITAL_COST, capturedSurgery.getHospitalCost());
        assertEquals(SURGERY_COST, capturedSurgery.getSurgeryCost());

        verify(forConsultPort).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forSurgeryTypePort).getSurgeryType(SURGERY_TYPE_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenConsultDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID)).thenThrow(new NotFoundException(""));

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID));

        verify(forConsultPort, times(1)).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forSurgeryTypePort, never()).getSurgeryType(any());
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSurgeryTypeDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID)).thenReturn(consult);
        when(forSurgeryTypePort.getSurgeryType(SURGERY_TYPE_ID))
                .thenThrow(new NotFoundException("Tipo de cirugía no encontrado"));

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID));

        assertEquals("Tipo de cirugía no encontrado", ex.getMessage());
        verify(forConsultPort, times(1)).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forSurgeryTypePort, times(1)).getSurgeryType(SURGERY_TYPE_ID);
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenConsultDoesNotExist_EvenIfSurgeryTypeIsAlsoMissing() throws NotFoundException {
        when(forConsultPort.findConsultAndIsNotPaid(CONSULT_ID))
                .thenThrow(new NotFoundException("Consulta no encontrada"));

        assertThrows(NotFoundException.class,
                () -> sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID));

        verify(forConsultPort).findConsultAndIsNotPaid(CONSULT_ID);
        verify(forSurgeryTypePort, never()).getSurgeryType(any());
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    void shouldDeleteSurgerySuccessfully() throws NotFoundException {
        // Arrange
        when(surgeryRepository.existsById(SURGERY_ID)).thenReturn(true);
        doNothing().when(surgeryRepository).deleteById(SURGERY_ID);

        // Act
        DeleteSurgeryResponseDTO result = sugeryServices.deleteSurgery(SURGERY_ID);

        // Assert
        // assertTrue(result);
        verify(surgeryRepository, times(1)).existsById(SURGERY_ID);
        verify(surgeryRepository, times(1)).deleteById(SURGERY_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentSurgery() {
        // Arrange
        when(surgeryRepository.existsById(SURGERY_ID)).thenReturn(false);

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> sugeryServices.deleteSurgery(SURGERY_ID));

        assertEquals("No se encontró la cirugía con id " + SURGERY_ID, ex.getMessage());
        verify(surgeryRepository, times(1)).existsById(SURGERY_ID);
        verify(surgeryRepository, times(0)).deleteById(SURGERY_ID);
    }

    @Test
    void shouldMarkSurgeryAsPerformedSuccessfully() throws NotFoundException {
        // Arrange
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.of(surgery));
        when(surgeryRepository.save(any(Surgery.class))).thenReturn(surgery);

        // Act
        Surgery result = sugeryServices.markSurgeryAsPerformed(SURGERY_ID);

        // Assert
        assertNotNull(result.getPerformedDate());
        verify(surgeryRepository).findById(SURGERY_ID);
        verify(surgeryRepository).save(surgery);
    }

    @Test
    void shouldThrowExceptionWhenMarkingAlreadyPerformedSurgery() {
        // Arrange
        surgery.setPerformedDate(LocalDate.now());
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.of(surgery));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> sugeryServices.markSurgeryAsPerformed(SURGERY_ID));
        verify(surgeryRepository, never()).save(any(Surgery.class));
    }

    @Test
    void shouldReturnTrueWhenSurgeryIsPerformed() throws NotFoundException {
        // Arrange
        surgery.setPerformedDate(LocalDate.now());
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.of(surgery));

        // Act
        boolean result = sugeryServices.surgeryAsPerformed(SURGERY_ID);

        // Assert
        assertTrue(result);
        verify(surgeryRepository).findById(SURGERY_ID);
    }

    @Test
    void shouldReturnFalseWhenSurgeryIsNotPerformed() throws NotFoundException {
        // Arrange
        surgery.setPerformedDate(null);
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.of(surgery));

        // Act
        boolean result = sugeryServices.surgeryAsPerformed(SURGERY_ID);

        // Assert
        assertFalse(result);
        verify(surgeryRepository).findById(SURGERY_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionInMarkSurgeryAsPerformed() {
        // Arrange
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sugeryServices.markSurgeryAsPerformed(SURGERY_ID));
        verify(surgeryRepository).findById(SURGERY_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionInSurgeryAsPerformed() {
        // Arrange
        when(surgeryRepository.findById(SURGERY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sugeryServices.surgeryAsPerformed(SURGERY_ID));
        verify(surgeryRepository).findById(SURGERY_ID);
    }
}
