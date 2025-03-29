package com.hospitalApi.surgery.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.dtos.CreateSugeryRequestDTO;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.ports.ForSurgeryEmployeePort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SurgeryCreateServiceTest {

    @Mock
    private ForSurgeryPort forSurgeryPort;

    @Mock
    private ForSurgeryEmployeePort forSurgeryEmployeePort;

    @InjectMocks
    private SurgeryCreateService surgeryCreateService;

    private Surgery surgery;
    private CreateSugeryRequestDTO request;

    private static final String SURGERY_ID = "SURG-001";
    private static final String CONSULT_ID = "CONSULT-123";
    private static final String SURGERY_TYPE_ID = "TYPE-456";
    private static final String DOCTOR_ID = "DOCTOR-789";

    @BeforeEach
    public void setUp() {
        surgery = new Surgery();
        surgery.setId(SURGERY_ID);

        request = new CreateSugeryRequestDTO();
        request.setConsultId(CONSULT_ID);
        request.setSurgeryTypeId(SURGERY_TYPE_ID);
        request.setAsignedDoctorId(DOCTOR_ID);
        request.setIsSpecialist(true);
    }

    @Test
    public void shouldCreateSurgerySuccessfully() throws Exception {
        when(forSurgeryPort.createSurgery(CONSULT_ID, SURGERY_TYPE_ID)).thenReturn(surgery);
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);

        Surgery result = surgeryCreateService.createSurgery(request);

        assertNotNull(result);
        assertEquals(SURGERY_ID, result.getId());

        verify(forSurgeryPort).createSurgery(CONSULT_ID, SURGERY_TYPE_ID);
        verify(forSurgeryEmployeePort).addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);
        verify(forSurgeryPort).getSurgery(SURGERY_ID);
    }

    @Test
    public void shouldThrowIllegalStateExceptionFromAddDoctorToSurgery() throws Exception {
        when(forSurgeryPort.createSurgery(CONSULT_ID, SURGERY_TYPE_ID)).thenReturn(surgery);
        doThrow(IllegalStateException.class).when(forSurgeryEmployeePort)
            .addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);

        assertThrows(IllegalStateException.class, () -> {
            surgeryCreateService.createSurgery(request);
        });

        verify(forSurgeryPort).createSurgery(CONSULT_ID, SURGERY_TYPE_ID);
        verify(forSurgeryEmployeePort).addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);
        verify(forSurgeryPort, never()).getSurgery(SURGERY_ID);
    }

    @Test
    public void shouldThrowNotFoundExceptionFromAddDoctorToSurgery() throws Exception {
        when(forSurgeryPort.createSurgery(CONSULT_ID, SURGERY_TYPE_ID)).thenReturn(surgery);
        doThrow(NotFoundException.class).when(forSurgeryEmployeePort)
            .addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);

        assertThrows(NotFoundException.class, () -> {
            surgeryCreateService.createSurgery(request);
        });

        verify(forSurgeryPort).createSurgery(CONSULT_ID, SURGERY_TYPE_ID);
        verify(forSurgeryEmployeePort).addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);
        verify(forSurgeryPort, never()).getSurgery(SURGERY_ID);
    }

    @Test
    public void shouldThrowDuplicatedEntryExceptionFromAddDoctorToSurgery() throws Exception {
        when(forSurgeryPort.createSurgery(CONSULT_ID, SURGERY_TYPE_ID)).thenReturn(surgery);
        doThrow(DuplicatedEntryException.class).when(forSurgeryEmployeePort)
            .addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);

        assertThrows(DuplicatedEntryException.class, () -> {
            surgeryCreateService.createSurgery(request);
        });

        verify(forSurgeryPort).createSurgery(CONSULT_ID, SURGERY_TYPE_ID);
        verify(forSurgeryEmployeePort).addDoctorToSurgery(SURGERY_ID, DOCTOR_ID, true);
        verify(forSurgeryPort, never()).getSurgery(SURGERY_ID);
    }
}
