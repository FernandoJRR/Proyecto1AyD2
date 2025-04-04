package com.hospitalApi.consults.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.hospitalApi.consults.dtos.ConsutlFilterDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForEmployeeConsultPort;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.medicines.ports.ForSaleMedicineCalculationPort;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.rooms.ports.ForRoomUsagePort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;

@ExtendWith(MockitoExtension.class)
public class ConsultServiceTest {

    @Mock
    private ConsultRepository consultRepository;

    @Mock
    private ForPatientPort forPatientPort;

    @Mock
    private ForEmployeeConsultPort forEmployeeConsultPort;

    @Mock
    private ForSaleMedicineCalculationPort forSaleMedicineCalculationPort;

    @Mock
    private ForRoomUsagePort forRoomUsagePort;

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
    private static final BigDecimal CONSULT_COST = BigDecimal.valueOf(300.00);
    private static final BigDecimal UPDATED_CONSULT_COST = BigDecimal.valueOf(500.00);
    private static final Double SURGERY_TOTAL_COST = 700.00;

    // Constantes para el empleado
    private static final String EMPLOYEE_ID = "EMPLOYEE-001";

    // Constantes para RoomUsage
    private static final String ROOM_ID = "ROOM-123";

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
        assertEquals(patient, result.getPatient());
        assertEquals(CONSULT_COST, result.getCostoConsulta());
        assertEquals(CONSULT_COST, result.getCostoTotal());
        assertEquals(false, result.getIsPaid());

        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultNotFoundById() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.findById(CONSULT_ID));

        verify(consultRepository, times(1)).findById(CONSULT_ID);
    }

    @Test
    public void shouldReturnConsultWhenNotPaid() throws NotFoundException {
        // Arrange
        consult.setIsPaid(false);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act
        Consult result = consultService.findConsultAndIsNotPaid(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CONSULT_ID, result.getId());
        assertEquals(false, result.getIsPaid());
        verify(consultRepository).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultDoesNotExistInFindAndIsNotPaid() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.findConsultAndIsNotPaid(CONSULT_ID));

        verify(consultRepository).findById(CONSULT_ID);
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenConsultIsAlreadyPaid() {
        // Arrange
        consult.setIsPaid(true);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> consultService.findConsultAndIsNotPaid(CONSULT_ID));

        verify(consultRepository).findById(CONSULT_ID);
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
        Consult result = consultService.createConsult(PATIENT_ID, EMPLOYEE_ID, CONSULT_COST);

        // Assert
        assertNotNull(result);
        assertEquals(CONSULT_ID, result.getId());
        assertEquals(patient, result.getPatient());
        assertEquals(CONSULT_COST, result.getCostoConsulta());

        ArgumentCaptor<Consult> consultCaptor = ArgumentCaptor.forClass(Consult.class);
        verify(consultRepository).save(consultCaptor.capture());
        Consult captured = consultCaptor.getValue();

        assertEquals(patient, captured.getPatient());
        assertEquals(CONSULT_COST, captured.getCostoConsulta());

        verify(forPatientPort).getPatient(PATIENT_ID);
        verify(forEmployeeConsultPort).createEmployeeConsult(result, EMPLOYEE_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenPatientDoesNotExist() throws NotFoundException {
        // Arrange
        when(forPatientPort.getPatient(PATIENT_ID)).thenThrow(new NotFoundException("Paciente no encontrado"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            consultService.createConsult(PATIENT_ID, EMPLOYEE_ID, CONSULT_COST);
        });

        verify(forPatientPort).getPatient(PATIENT_ID);
        verify(consultRepository, never()).save(any());
        verify(forEmployeeConsultPort, never()).createEmployeeConsult(any(), any());
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
        verify(consultRepository).save(captor.capture());
        Consult captured = captor.getValue();
        assertEquals(UPDATED_CONSULT_COST, captured.getCostoConsulta());

        verify(consultRepository).findById(CONSULT_ID);
        verify(consultRepository).save(any(Consult.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultDoesNotExistOnUpdate() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            consultService.updateConsult(CONSULT_ID, updateConsultRequestDTO);
        });

        verify(consultRepository).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenConsultIsAlreadyPaidOnUpdate() {
        // Arrange
        consult.setIsPaid(true);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            consultService.updateConsult(CONSULT_ID, updateConsultRequestDTO);
        });

        verify(consultRepository).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldReturnTotalConsultaSuccessfullyWhenNotInternado() throws NotFoundException {
        // Arrange
        consult.setIsInternado(false);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.totalSurgerisByConsult(CONSULT_ID)).thenReturn(SURGERY_TOTAL_COST);
        when(forSaleMedicineCalculationPort.totalSalesMedicinesByConsult(CONSULT_ID)).thenReturn(300.0);
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedTotal = CONSULT_COST.add(BigDecimal.valueOf(SURGERY_TOTAL_COST))
                .add(BigDecimal.valueOf(0.0 + 300.0));

        // Act
        BigDecimal result = consultService.obtenerTotalConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(forSurgeryCalculationService).totalSurgerisByConsult(CONSULT_ID);
        verify(forSaleMedicineCalculationPort).totalSalesMedicinesByConsult(CONSULT_ID);
        verify(consultRepository).save(any(Consult.class));
    }

    @Test
    public void shouldReturnTotalConsultaSuccessfullyWhenInternado() throws NotFoundException {
        // Arrange
        consult.setIsInternado(true);
        // RoomUsage roomUsage = new RoomUsage(5, new BigDecimal("100.0"));
        RoomUsage roomUsage = new RoomUsage();
        roomUsage.setUsageDays(5);
        roomUsage.setDailyRoomPrice(new BigDecimal(100));
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.totalSurgerisByConsult(CONSULT_ID)).thenReturn(SURGERY_TOTAL_COST);
        when(forSaleMedicineCalculationPort.totalSalesMedicinesByConsult(CONSULT_ID)).thenReturn(200.0);
        when(forRoomUsagePort.calcRoomUsage(consult)).thenReturn(roomUsage);
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        double expectedRoom = 5 * 100.0;
        BigDecimal expectedTotal = CONSULT_COST.add(BigDecimal.valueOf(SURGERY_TOTAL_COST))
                .add(BigDecimal.valueOf(expectedRoom + 200.0));

        // Act
        BigDecimal result = consultService.obtenerTotalConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTotal, result);

        verify(forRoomUsagePort).calcRoomUsage(consult);
        verify(forSurgeryCalculationService).totalSurgerisByConsult(CONSULT_ID);
        verify(forSaleMedicineCalculationPort).totalSalesMedicinesByConsult(CONSULT_ID);
        verify(consultRepository).save(any(Consult.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenConsultDoesNotExistOnTotal() {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            consultService.obtenerTotalConsulta(CONSULT_ID);
        });

        verify(consultRepository).findById(CONSULT_ID);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldPayConsultSuccessfullyWhenAllValidationsPass() throws NotFoundException {
        // Arrange
        consult.setIsInternado(false);
        consult.setIsPaid(false);

        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(true);
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Consult result = consultService.pagarConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());

        verify(forSurgeryCalculationService).allSurgeriesPerformedByConsultId(CONSULT_ID);
        verify(consultRepository).save(consult);
        verify(forRoomUsagePort, never()).closeRoomUsage(any());
    }

    @Test
    public void shouldPayConsultAndCloseRoomUsageWhenInternado() throws NotFoundException {
        // Arrange
        consult.setIsInternado(true);
        consult.setIsPaid(false);

        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(true);
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Consult result = consultService.pagarConsulta(CONSULT_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());

        verify(forRoomUsagePort).closeRoomUsage(consult);
        verify(consultRepository).save(consult);
    }

    @Test
    public void shouldThrowWhenSurgeriesNotPerformed() throws NotFoundException {
        // Arrange
        consult.setIsInternado(false);
        consult.setIsPaid(false);

        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> consultService.pagarConsulta(CONSULT_ID));

        verify(consultRepository, never()).save(any());
        verify(forRoomUsagePort, never()).closeRoomUsage(any());
    }

    @Test
    public void shouldThrowWhenConsultAlreadyPaid() throws NotFoundException {
        // Arrange
        consult.setIsPaid(true);

        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(forSurgeryCalculationService.allSurgeriesPerformedByConsultId(CONSULT_ID)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> consultService.pagarConsulta(CONSULT_ID));

        verify(consultRepository, never()).save(any());
        verify(forRoomUsagePort, never()).closeRoomUsage(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionIfConsultDoesNotExist() throws IllegalStateException, NotFoundException {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> consultService.pagarConsulta(CONSULT_ID));

        verify(consultRepository, never()).save(any());
        verify(forRoomUsagePort, never()).closeRoomUsage(any());
        verify(forSurgeryCalculationService, never()).allSurgeriesPerformedByConsultId(CONSULT_ID);
    }

    @Test
    public void shouldReturnAllConsultsSuccessfully() {
        // Arrange
        Consult consult1 = new Consult("CONSULT-002", patient, false,
                BigDecimal.valueOf(200.00), BigDecimal.valueOf(200.00));
        Consult consult2 = new Consult("CONSULT-003", patient, true,
                BigDecimal.valueOf(400.00), BigDecimal.valueOf(450.00));
        List<Consult> consultList = List.of(consult, consult1, consult2);

        when(consultRepository.findAll()).thenReturn(consultList);

        // Act
        List<Consult> result = consultService.getAllConsults();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        assertAll(
                () -> assertEquals("CONSULT-001", result.get(0).getId()),
                () -> assertEquals("CONSULT-002", result.get(1).getId()),
                () -> assertEquals("CONSULT-003", result.get(2).getId()));

        verify(consultRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnEmptyListWhenNoConsultsExist() {
        // Arrange
        when(consultRepository.findAll()).thenReturn(List.of());

        // Act
        List<Consult> result = consultService.getAllConsults();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(consultRepository, times(1)).findAll();
    }

    @Test
    public void shouldMarkConsultAsInternadoSuccessfully() throws Exception {
        // Arrange
        consult.setIsPaid(false);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        when(consultRepository.save(any(Consult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Consult result = consultService.markConsultInternado(CONSULT_ID, ROOM_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsInternado());

        verify(consultRepository).findById(CONSULT_ID);
        verify(forRoomUsagePort).asignRoomToConsult(ROOM_ID, consult);
        verify(consultRepository).save(consult);
    }

    @Test
    public void shouldThrowNotFoundExceptionIfConsultDoesNotExistOnMarkInternado()
            throws IllegalStateException, NotFoundException, DuplicatedEntryException {
        // Arrange
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            consultService.markConsultInternado(CONSULT_ID, ROOM_ID);
        });

        verify(consultRepository).findById(CONSULT_ID);
        verify(forRoomUsagePort, never()).asignRoomToConsult(any(), any());
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldThrowIllegalStateIfConsultAlreadyPaidOnMarkInternado()
            throws IllegalStateException, NotFoundException, DuplicatedEntryException {
        // Arrange
        consult.setIsPaid(true);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            consultService.markConsultInternado(CONSULT_ID, ROOM_ID);
        });

        verify(consultRepository).findById(CONSULT_ID);
        verify(forRoomUsagePort, never()).asignRoomToConsult(any(), any());
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldThrowDuplicatedEntryIfRoomIsAlreadyAssigned() throws Exception {
        // Arrange
        consult.setIsPaid(false);
        when(consultRepository.findById(CONSULT_ID)).thenReturn(Optional.of(consult));
        doThrow(new DuplicatedEntryException("ya asignada")).when(forRoomUsagePort).asignRoomToConsult("ROOM-123",
                consult);

        // Act & Assert
        assertThrows(DuplicatedEntryException.class, () -> {
            consultService.markConsultInternado(CONSULT_ID, ROOM_ID);
        });

        verify(forRoomUsagePort).asignRoomToConsult(ROOM_ID, consult);
        verify(consultRepository, never()).save(any());
    }

    @Test
    public void shouldReturnFilteredConsultsSuccessfully() {
        // Arrange
        ConsutlFilterDTO filterDTO = new ConsutlFilterDTO();
        filterDTO.setConsultId("CONSULT-001");
        filterDTO.setPatientDpi("123456789");
        filterDTO.setPatientFirstnames("Jose");
        filterDTO.setPatientLastnames("Perez");
        filterDTO.setEmployeeId("EMPLOYEE-001");
        filterDTO.setEmployeeFirstName("Carlos");
        filterDTO.setEmployeeLastName("Lopez");
        filterDTO.setIsPaid(false);
        filterDTO.setIsInternado(false);

        Consult consult1 = new Consult("CONSULT-001", patient, false,
                new BigDecimal(200),
                new BigDecimal(200));
        Consult consult2 = new Consult("CONSULT-002", patient, false,
                new BigDecimal(300),
                new BigDecimal(300));
        List<Consult> filteredList = List.of(consult1, consult2);

        when(consultRepository.findAll(any(Specification.class), ArgumentMatchers.<Sort>any()))
                .thenReturn(filteredList);

        // Act
        List<Consult> result = consultService.getConsults(filterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CONSULT-001", result.get(0).getId());
        assertEquals("CONSULT-002", result.get(1).getId());

        verify(consultRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
    }

}
