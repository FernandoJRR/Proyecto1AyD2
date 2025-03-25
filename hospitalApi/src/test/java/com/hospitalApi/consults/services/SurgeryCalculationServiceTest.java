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
        when(surgeryRepository.sumSurgeryCostByConsultId(CONSULT_ID)).thenReturn(EXPECTED_TOTAL);

        // Act
        Double result = surgeryCalculationService.totalSurgerisByConsult(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_TOTAL, result);
        verify(surgeryRepository, times(1)).sumSurgeryCostByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldReturnZeroWhenNoSurgeriesAreFoundForConsult() {
        // Arrange
        when(surgeryRepository.sumSurgeryCostByConsultId(CONSULT_ID)).thenReturn(null);

        // Act
        Double result = surgeryCalculationService.totalSurgerisByConsult(CONSULT_ID);

        // Assert
        assertNull(result); // o assertEquals(0.0, result); si se quiere manejar como cero
        verify(surgeryRepository, times(1)).sumSurgeryCostByConsultId(CONSULT_ID);
    }
}
