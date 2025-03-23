package com.hospitalApi.consults.services;

import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.consults.services.ConsultService;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsultServiceTest {

    @Mock
    private ConsultRepository consultRepository;

    @Mock
    private ForPatientPort forPatientPort;

    @InjectMocks
    private ConsultService consultService;

    private static final String CONSULT_ID = "CONSULT-001";
    private static final String PATIENT_ID = "PATIENT-001";
    private static final Double CONSULT_COST = 300.00;
    private static final Double UPDATED_CONSULT_COST = 500.00;

    private Patient patient;
    private Consult consult;
    private UpdateConsultRequestDTO updateConsultRequestDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient("Jose", "Perez", "123456789");
        patient.setId(PATIENT_ID);

        consult = new Consult(CONSULT_ID, patient, false, CONSULT_COST, CONSULT_COST);

        updateConsultRequestDTO = new UpdateConsultRequestDTO();
        updateConsultRequestDTO.setCostoConsulta(UPDATED_CONSULT_COST);
    }

    @Test
    public void shouldFindConsultByIdSuccessfully() throws NotFoundException {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act
        Consult result = consultService.findById(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CONSULT_ID, result.getId());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultDoesNotExist() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.findById(CONSULT_ID));
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldCreateConsultSuccessfully() throws NotFoundException {
        // Arrange
        when(forPatientPort.getPatient(PATIENT_ID)).thenReturn(patient);
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> {
            Consult saved = invocation.getArgument(0);
            saved.setId(CONSULT_ID);
            return saved;
        });

        // Act
        Consult result = consultService.createConsult(PATIENT_ID, CONSULT_COST);

        // Assert
        assertNotNull(result);
        assertEquals(CONSULT_ID, result.getId());
        assertEquals(patient, result.getPatient());
        assertEquals(CONSULT_COST, result.getCostoConsulta());

        ArgumentCaptor<Consult> captor = ArgumentCaptor.forClass(Consult.class);
        verify(consultRepository, times(1)).save(captor.capture());
        Consult captured = captor.getValue();

        assertEquals(patient, captured.getPatient());
        assertEquals(CONSULT_COST, captured.getCostoConsulta());
        verify(forPatientPort, times(1)).getPatient(PATIENT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenPatientDoesNotExistOnCreateConsult() throws NotFoundException {
        // Arrange
        when(forPatientPort.getPatient(PATIENT_ID)).thenThrow(new NotFoundException("Paciente no encontrado"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.createConsult(PATIENT_ID, CONSULT_COST));

        verify(forPatientPort, times(1)).getPatient(PATIENT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldUpdateConsultSuccessfully() throws NotFoundException {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Consult result = consultService.updateConsult(CONSULT_ID, updateConsultRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(UPDATED_CONSULT_COST, result.getCostoConsulta());

        ArgumentCaptor<Consult> captor = ArgumentCaptor.forClass(Consult.class);
        verify(consultRepository, times(1)).save(captor.capture());
        Consult captured = captor.getValue();

        assertEquals(UPDATED_CONSULT_COST, captured.getCostoConsulta());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingConsultNotExist() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.updateConsult(CONSULT_ID, updateConsultRequestDTO));

        verify(consultRepository, times(1)).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldObtainTotalConsultSuccessfully() throws NotFoundException {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act
        Double result = consultService.obtenerTotalConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(consult.getCostoTotal(), result);
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultNotExistOnGetTotal() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.obtenerTotalConsulta(CONSULT_ID));

        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldPayConsultSuccessfully() throws NotFoundException {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Consult result = consultService.pagarConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());

        ArgumentCaptor<Consult> captor = ArgumentCaptor.forClass(Consult.class);
        verify(consultRepository, times(1)).save(captor.capture());
        Consult captured = captor.getValue();

        assertTrue(captured.getIsPaid());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultNotExistOnPay() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.pagarConsulta(CONSULT_ID));

        verify(consultRepository, times(1)).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }
}
