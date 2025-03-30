package com.hospitalApi.consults.services;

import com.hospitalApi.surgery.repositories.SurgeryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SurgeryCalculationServiceTest {

    @Mock
    private SurgeryRepository surgeryRepository;

    @InjectMocks
    private SurgeryCalculationService surgeryCalculationService;

    private static final String CONSULT_ID = "CONSULT-999";
    private static final Double EXPECTED_TOTAL = 1500.0;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnTotalSurgeriesByConsultSuccessfully() {
        // Arrange
        when(surgeryRepository.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(true);
        when(surgeryRepository.sumSurgeryCostByConsultId(CONSULT_ID)).thenReturn(EXPECTED_TOTAL);

        // Act
        Double result = surgeryCalculationService.totalSurgerisByConsult(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_TOTAL, result);
        verify(surgeryRepository).allSurgeriesPerformedByConsultId(CONSULT_ID);
        verify(surgeryRepository).sumSurgeryCostByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldReturnZeroWhenTotalSurgeriesIsNull() {
        // Arrange
        when(surgeryRepository.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(true);
        when(surgeryRepository.sumSurgeryCostByConsultId(CONSULT_ID)).thenReturn(null);

        // Act
        Double result = surgeryCalculationService.totalSurgerisByConsult(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result);
        verify(surgeryRepository).allSurgeriesPerformedByConsultId(CONSULT_ID);
        verify(surgeryRepository).sumSurgeryCostByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldThrowWhenNotAllSurgeriesPerformed() {
        // Arrange
        when(surgeryRepository.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            surgeryCalculationService.totalSurgerisByConsult(CONSULT_ID);
        });

        verify(surgeryRepository).allSurgeriesPerformedByConsultId(CONSULT_ID);
        verify(surgeryRepository, never()).sumSurgeryCostByConsultId(any());
    }

    @Test
    public void shouldReturnTrueIfAllSurgeriesPerformed() {
        // Arrange
        when(surgeryRepository.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(true);

        // Act
        Boolean result = surgeryCalculationService.allSurgeriesPerformedByConsultId(CONSULT_ID);

        // Assert
        assertTrue(result);
        verify(surgeryRepository).allSurgeriesPerformedByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldReturnFalseIfNotAllSurgeriesPerformed() {
        // Arrange
        when(surgeryRepository.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(false);

        // Act
        Boolean result = surgeryCalculationService.allSurgeriesPerformedByConsultId(CONSULT_ID);

        // Assert
        assertFalse(result);
        verify(surgeryRepository).allSurgeriesPerformedByConsultId(CONSULT_ID);
    }
}
