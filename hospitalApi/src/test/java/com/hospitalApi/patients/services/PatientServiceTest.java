package com.hospitalApi.patients.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.hospitalApi.patients.dtos.CreatePatientRequestDTO;
import com.hospitalApi.patients.dtos.UpdatePatientRequestDTO;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.repositories.PatientRespository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRespository patientRespository;

    @InjectMocks
    private PatientService patientService;

    private CreatePatientRequestDTO createPatientDTO;
    private Patient createPatient;
    private UpdatePatientRequestDTO updatePatientDTO;
    private Patient patient;

    private static final String PATIENT_ID = "abc-123-uuid";
    private static final String PATIENT_FIRSTNAME = "Carlos";
    private static final String PATIENT_LASTNAME = "Monterroso";
    private static final String PATIENT_DPI = "1234567890123";

    private static final String UPDATED_FIRSTNAME = "Juan";
    private static final String UPDATED_LASTNAME = "Pérez";
    private static final String UPDATED_DPI = "9876543210987";

    @BeforeEach
    public void setUp() {
        createPatientDTO = CreatePatientRequestDTO.builder()
                .firstnames(PATIENT_FIRSTNAME)
                .lastnames(PATIENT_LASTNAME)
                .dpi(PATIENT_DPI)
                .build();

        createPatient = new Patient(createPatientDTO);

        updatePatientDTO = UpdatePatientRequestDTO.builder()
                .firstnames(UPDATED_FIRSTNAME)
                .lastnames(UPDATED_LASTNAME)
                .dpi(UPDATED_DPI)
                .build();

        patient = new Patient(
                PATIENT_ID,
                PATIENT_FIRSTNAME,
                PATIENT_LASTNAME,
                PATIENT_DPI);
    }

    @Test
    public void shouldCreatePatientSuccessfully() throws DuplicatedEntryException {
        // ARRANGE
        when(patientRespository.existsByDpi(PATIENT_DPI)).thenReturn(false);
        when(patientRespository.save(any(Patient.class))).thenReturn(patient);

        // ACT
        Patient result = patientService.createPatient(createPatient);

        // ASSERT
        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRespository).save(patientCaptor.capture());
        Patient captured = patientCaptor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(PATIENT_FIRSTNAME, captured.getFirstnames()),
                () -> assertEquals(PATIENT_LASTNAME, captured.getLastnames()),
                () -> assertEquals(PATIENT_DPI, captured.getDpi()));

        verify(patientRespository, times(1)).existsByDpi(PATIENT_DPI);
        verify(patientRespository, times(1)).save(any(Patient.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenCreatingPatientWithExistingDpi() {
        // ARRANGE
        when(patientRespository.existsByDpi(PATIENT_DPI)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(DuplicatedEntryException.class, () -> {
            patientService.createPatient(createPatient);
        });

        verify(patientRespository, times(1)).existsByDpi(PATIENT_DPI);
        verify(patientRespository, times(0)).save(any(Patient.class));
    }

    @Test
    public void shouldGetPatientByIdSuccessfully() throws NotFoundException {
        // ARRANGE
        when(patientRespository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));

        // ACT
        Patient result = patientService.getPatient(PATIENT_ID);

        // ASSERT
        assertNotNull(result);
        assertEquals(PATIENT_ID, result.getId());

        verify(patientRespository, times(1)).findById(PATIENT_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGettingPatientByNonexistentId() {
        // ARRANGE
        when(patientRespository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            patientService.getPatient(PATIENT_ID);
        });

        verify(patientRespository, times(1)).findById(PATIENT_ID);
    }

    @Test
    public void shouldGetPatientByDpiSuccessfully() throws NotFoundException {
        // ARRANGE
        when(patientRespository.existsByDpi(PATIENT_DPI)).thenReturn(true);
        when(patientRespository.findByDpi(PATIENT_DPI)).thenReturn(patient);

        // ACT
        Patient result = patientService.getPatientByDpi(PATIENT_DPI);

        // ASSERT
        assertNotNull(result);
        assertEquals(PATIENT_DPI, result.getDpi());

        verify(patientRespository, times(1)).existsByDpi(PATIENT_DPI);
        verify(patientRespository, times(1)).findByDpi(PATIENT_DPI);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenGettingPatientByNonexistentDpi() {
        // ARRANGE
        when(patientRespository.existsByDpi(PATIENT_DPI)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            patientService.getPatientByDpi(PATIENT_DPI);
        });

        verify(patientRespository, times(1)).existsByDpi(PATIENT_DPI);
        verify(patientRespository, times(0)).findByDpi(PATIENT_DPI);
    }

    @Test
    public void shouldUpdatePatientSuccessfully() throws DuplicatedEntryException, NotFoundException {
        // ARRANGE
        when(patientRespository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(patientRespository.existsByDpiAndIdNot(UPDATED_DPI,PATIENT_ID)).thenReturn(false);
        when(patientRespository.save(any(Patient.class))).thenReturn(patient);

        // ACT
        Patient updated = patientService.updatePatient(PATIENT_ID, updatePatientDTO);

        // ASSERT
        assertAll(
                () -> assertNotNull(updated),
                () -> assertEquals(UPDATED_FIRSTNAME, updated.getFirstnames()),
                () -> assertEquals(UPDATED_LASTNAME, updated.getLastnames()),
                () -> assertEquals(UPDATED_DPI, updated.getDpi()));

        verify(patientRespository, times(1)).findById(PATIENT_ID);
        verify(patientRespository, times(1)).existsByDpiAndIdNot(UPDATED_DPI,PATIENT_ID);
        verify(patientRespository, times(1)).save(any(Patient.class));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdatingNonexistentPatient() {
        // ARRANGE
        when(patientRespository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> {
            patientService.updatePatient(PATIENT_ID, updatePatientDTO);
        });

        verify(patientRespository, times(1)).findById(PATIENT_ID);
        verify(patientRespository, times(0)).save(any(Patient.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenUpdatingPatientWithExistingDpi() {
        // ARRANGE
        // El paciente actual tiene un DPI diferente al nuevo DPI del DTO
        when(patientRespository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(patientRespository.existsByDpiAndIdNot(UPDATED_DPI,PATIENT_ID)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(DuplicatedEntryException.class, () -> {
            patientService.updatePatient(PATIENT_ID, updatePatientDTO);
        });

        verify(patientRespository, times(1)).findById(PATIENT_ID);
        verify(patientRespository, times(1)).existsByDpiAndIdNot(UPDATED_DPI,PATIENT_ID);
        verify(patientRespository, times(0)).save(any(Patient.class));
    }

    @Test
    public void shouldGetAllPatientsOrderedByCreatedAtDesc() {
        List<Patient> patients = List.of(patient);

        when(patientRespository.findAllByOrderByCreatedAtDesc()).thenReturn(patients);

        List<Patient> result = patientService.getPatients(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PATIENT_ID, result.get(0).getId());

        verify(patientRespository, times(1)).findAllByOrderByCreatedAtDesc();
        verify(patientRespository, times(0))
                .findByFirstnamesContainingIgnoreCaseOrLastnamesContainingIgnoreCase(null, null);
    }

    @Test
    public void shouldSearchPatientsByFirstnamesOrLastnamesIgnoreCase() {
        String query = "carlos";

        List<Patient> searchResults = List.of(patient);

        when(patientRespository.findByFirstnamesContainingIgnoreCaseOrLastnamesContainingIgnoreCase(query, query))
                .thenReturn(searchResults);

        List<Patient> result = patientService.getPatients(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PATIENT_FIRSTNAME, result.get(0).getFirstnames());

        verify(patientRespository, times(1))
                .findByFirstnamesContainingIgnoreCaseOrLastnamesContainingIgnoreCase(query, query);
    }

    @Test
    public void shouldReturnEmptyListWhenSearchPatientsDoesNotFindAny() {
        String query = "noExiste";

        when(patientRespository.findByFirstnamesContainingIgnoreCaseOrLastnamesContainingIgnoreCase(query, query))
                .thenReturn(List.of());

        List<Patient> result = patientService.getPatients(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientRespository, times(1))
                .findByFirstnamesContainingIgnoreCaseOrLastnamesContainingIgnoreCase(query, query);
    }

}
