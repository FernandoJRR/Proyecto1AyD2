package com.hospitalApi.consults.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;

@ExtendWith(MockitoExtension.class)
public class ConsultServiceTest {

    @Mock
    private ConsultRepository consultRepository;

    @Mock
    private ForPatientPort forPatientPort;

    @Mock
    private ForSurgeryCalculationPort forSurgeryCalculationService;

    @InjectMocks
    private ConsultService consultService;

    // Constantes para el paciente
    private static final String PATIENT_ID = "PATIENT-001";
    private static final String PATIENT_NAME = "Jose";
    private static final String PATIENT_LASTNAME = "Perez";
    private static final String PATIENT_DPI = "123456789";

    // Constantes para la consulta
    private static final String CONSULT_ID = "CONSULT-001";
    private static final Double CONSULT_COST = 300.00;
    private static final Double UPDATED_CONSULT_COST = 500.00;
    private static final Double SURGERY_TOTAL_COST = 700.00;

    private Patient patient;
    private Consult consult;
    private UpdateConsultRequestDTO updateConsultRequestDTO;

    @BeforeEach
    public void setUp() {
        patient = new Patient(PATIENT_NAME, PATIENT_LASTNAME, PATIENT_DPI);
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
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> consultService.findById(CONSULT_ID));

        assertEquals("Consulta con id " + CONSULT_ID + " no encontrada", ex.getMessage());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    // @Test
    // public void shouldCreateConsultSuccessfully() throws NotFoundException {
    //     // Arrange
    //     when(forPatientPort.getPatient(PATIENT_ID)).thenReturn(patient);
    //     when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> {
    //         Consult saved = invocation.getArgument(0);
    //         saved.setId(CONSULT_ID);
    //         return saved;
    //     });

    //     // Act
    //     Consult result = consultService.createConsult(PATIENT_ID, CONSULT_COST);

    //     // Assert
    //     assertNotNull(result);
    //     assertEquals(CONSULT_ID, result.getId());
    //     assertEquals(patient, result.getPatient());
    //     assertEquals(CONSULT_COST, result.getCostoConsulta());

    //     ArgumentCaptor<Consult> captor = ArgumentCaptor.forClass(Consult.class);
    //     verify(consultRepository).save(captor.capture());

    //     Consult captured = captor.getValue();
    //     assertEquals(patient, captured.getPatient());
    //     assertEquals(CONSULT_COST, captured.getCostoConsulta());
    //     verify(forPatientPort, times(1)).getPatient(PATIENT_ID);
    // }

    // @Test
    // public void shouldThrowNotFoundExceptionWhenPatientDoesNotExistOnCreateConsult() throws NotFoundException {
    //     // Arrange
    //     when(forPatientPort.getPatient(PATIENT_ID)).thenThrow(new NotFoundException("Paciente no encontrado"));

    //     // Act & Assert
    //     NotFoundException ex = assertThrows(NotFoundException.class,
    //             () -> consultService.createConsult(PATIENT_ID, CONSULT_COST));

    //     assertEquals("Paciente no encontrado", ex.getMessage());
    //     verify(forPatientPort, times(1)).getPatient(PATIENT_ID);
    //     verify(consultRepository, never()).save(any());
    // }

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
        verify(consultRepository).save(captor.capture());

        Consult captured = captor.getValue();
        assertEquals(UPDATED_CONSULT_COST, captured.getCostoConsulta());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingConsultNotExist() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> consultService.updateConsult(CONSULT_ID, updateConsultRequestDTO));

        assertEquals("Consulta con id " + CONSULT_ID + " no encontrada", ex.getMessage());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldReturnTotalConsultaSuccessfully() throws NotFoundException {
        // Arrange
        Double expectedTotal = CONSULT_COST + SURGERY_TOTAL_COST;

        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.totalSurgerisByConsult(CONSULT_ID)).thenReturn(SURGERY_TOTAL_COST);
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Double total = consultService.obtenerTotalConsulta(CONSULT_ID);

        // Assert
        assertNotNull(total);
        assertEquals(expectedTotal, total);

        ArgumentCaptor<Consult> captor = ArgumentCaptor.forClass(Consult.class);
        verify(consultRepository).save(captor.capture());

        Consult captured = captor.getValue();
        assertEquals(expectedTotal, captured.getCostoTotal());

        verify(forSurgeryCalculationService, times(1)).totalSurgerisByConsult(CONSULT_ID);
        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGettingTotalConsultaAndConsultDoesNotExist() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> consultService.obtenerTotalConsulta(CONSULT_ID));

        assertEquals("Consulta con id " + CONSULT_ID + " no encontrada", ex.getMessage());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
        verify(forSurgeryCalculationService, times(0)).totalSurgerisByConsult(any());
    }

    @Test
    public void shouldPayConsultSuccessfully() throws NotFoundException {
        // Arrange
        consult.setIsPaid(false);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Consult result = consultService.pagarConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());

        ArgumentCaptor<Consult> captor = ArgumentCaptor.forClass(Consult.class);
        verify(consultRepository).save(captor.capture());

        Consult captured = captor.getValue();
        assertTrue(captured.getIsPaid());

        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultNotExistOnPay() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> consultService.pagarConsulta(CONSULT_ID));

        assertEquals("Consulta con id " + CONSULT_ID + " no encontrada", ex.getMessage());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenConsultIsAlreadyPaid() {
        // Arrange
        consult.setIsPaid(true);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> consultService.pagarConsulta(CONSULT_ID));

        assertEquals("La consulta con id " + CONSULT_ID + " ya fue pagada", ex.getMessage());
        verify(consultRepository, times(1)).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldGetAllConsultsSuccessfully() {
        // Arrange
        Consult consult1 = new Consult("CONSULT-002", patient, false, 200.00, 200.00);
        Consult consult2 = new Consult("CONSULT-003", patient, false, 400.00, 400.00);
        List<Consult> consultList = List.of(consult, consult1, consult2);

        when(consultRepository.findAll()).thenReturn(consultList);

        // Act
        List<Consult> result = consultService.getAllConsults();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        assertAll(
                () -> assertEquals(CONSULT_ID, result.get(0).getId()),
                () -> assertEquals("CONSULT-002", result.get(1).getId()),
                () -> assertEquals("CONSULT-003", result.get(2).getId()));

        verify(consultRepository, times(1)).findAll();
    }

}
