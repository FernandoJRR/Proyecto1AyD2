package com.hospitalApi.surgery.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;
import com.hospitalApi.surgery.repositories.SurgeryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class SugeryServicesTest {

    @Mock
    private ForConsultPort forConsultPort;

    @Mock
    private ForSurgeryTypePort forSurgeryTypePort;

    @Mock
    private SurgeryRepository surgeryRepository;

    @InjectMocks
    private SugeryServices sugeryServices;

    // Constantes
    private static final String SURGERY_ID = "SURGERY-1234";

    private static final String CONSULT_ID = "CONSULT-5678";
    private static final boolean IS_INTERNADO = false;
    private static final double COSTO_CONSULTA = 500.0;
    private static final double COSTO_TOTAL = 500.0;

    private static final String SURGERY_TYPE_ID = "TYPE-9999";
    private static final String SURGERY_TYPE_NAME = "Cirugía Mayor";
    private static final String SURGERY_TYPE_DESCRIPTION = "Cirugía mayor en hospital";
    private static final double SPECIALIST_PAYMENT = 1000.0;
    private static final double HOSPITAL_COST = 3000.0;
    private static final double SURGERY_COST = 4000.0;

    private Consult consult;
    private SurgeryType surgeryType;
    private Surgery surgery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
        when(forConsultPort.findById(CONSULT_ID)).thenReturn(consult);
        when(forSurgeryTypePort.getSurgeryType(SURGERY_TYPE_ID)).thenReturn(surgeryType);
        when(surgeryRepository.save(any(Surgery.class))).thenReturn(surgery);

        ArgumentCaptor<Surgery> surgeryCaptor = ArgumentCaptor.forClass(Surgery.class);

        // Act
        Surgery result = sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(consult, result.getConsult());
        assertEquals(surgeryType, result.getSurgeryType());
        assertEquals(surgeryType.getHospitalCost(), result.getHospitalCost());
        assertEquals(surgeryType.getSurgeryCost(), result.getSurgeryCost());

        verify(surgeryRepository, times(1)).save(surgeryCaptor.capture());

        Surgery capturedSurgery = surgeryCaptor.getValue();
        assertEquals(consult, capturedSurgery.getConsult());
        assertEquals(surgeryType, capturedSurgery.getSurgeryType());
        assertEquals(HOSPITAL_COST, capturedSurgery.getHospitalCost());
        assertEquals(SURGERY_COST, capturedSurgery.getSurgeryCost());

        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forSurgeryTypePort, times(1)).getSurgeryType(SURGERY_TYPE_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenConsultDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findById(CONSULT_ID))
                .thenThrow(new NotFoundException("Consulta no encontrada"));

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID));

        assertEquals("Consulta no encontrada", ex.getMessage());
        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forSurgeryTypePort, never()).getSurgeryType(any());
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSurgeryTypeDoesNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findById(CONSULT_ID)).thenReturn(consult);
        when(forSurgeryTypePort.getSurgeryType(SURGERY_TYPE_ID))
                .thenThrow(new NotFoundException("Tipo de cirugía no encontrado"));

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID));

        assertEquals("Tipo de cirugía no encontrado", ex.getMessage());
        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forSurgeryTypePort, times(1)).getSurgeryType(SURGERY_TYPE_ID);
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBothConsultAndSurgeryTypeDoNotExist() throws NotFoundException {
        // Arrange
        when(forConsultPort.findById(CONSULT_ID))
                .thenThrow(new NotFoundException("Consulta no encontrada"));

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> sugeryServices.createSurgery(CONSULT_ID, SURGERY_TYPE_ID));

        assertEquals("Consulta no encontrada", ex.getMessage());
        verify(forConsultPort, times(1)).findById(CONSULT_ID);
        verify(forSurgeryTypePort, never()).getSurgeryType(any());
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    void shouldDeleteSurgerySuccessfully() throws NotFoundException {
        // Arrange
        when(surgeryRepository.existsById(SURGERY_ID)).thenReturn(true);
        doNothing().when(surgeryRepository).deleteById(SURGERY_ID);

        // Act
        boolean result = sugeryServices.deleteSurgery(SURGERY_ID);

        // Assert
        assertTrue(result);
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

}
