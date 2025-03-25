package com.hospitalApi.surgery.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.employees.ports.ForSpecialistEmployeePort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryEmployee;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;
import com.hospitalApi.surgery.repositories.SurgeryEmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class SurgeryEmployeeServiceTest {

    @Mock
    private ForSurgeryPort forSurgeryPort;

    @Mock
    private ForEmployeesPort forEmployeesPort;

    @Mock
    private ForSpecialistEmployeePort forSpecialistEmployeePort;

    @Mock
    private ForSurgeryTypePort forSurgeryTypePort;

    @Mock
    private SurgeryEmployeeRepository surgeryEmployeeRepository;

    @InjectMocks
    private SurgeryEmployeeService surgeryEmployeeService;

    private static final String SURGERY_ID = "SURG-001";
    private static final String EMPLOYEE_ID = "EMP-001";
    private static final String SPECIALIST_ID = "SPEC-001";

    private Surgery surgery;
    private Employee employee;
    private SpecialistEmployee specialist;
    private SurgeryType surgeryType;
    private SurgeryEmployee surgeryEmployee;

    @BeforeEach
    public void setUp() {
        surgery = new Surgery();
        surgery.setId(SURGERY_ID);
        surgery.setHospitalCost(500.0);
        surgery.setSurgeryCost(1500.0);

        employee = new Employee();
        employee.setId(EMPLOYEE_ID);

        specialist = new SpecialistEmployee();
        specialist.setId(SPECIALIST_ID);

        surgeryType = new SurgeryType();
        surgeryType.setId("TYPE-001");
        surgeryType.setSpecialistPayment(1000.0);

        surgery.setSurgeryType(surgeryType);

        surgeryEmployee = new SurgeryEmployee();
        surgeryEmployee.setSurgery(surgery);
        surgeryEmployee.setEmployee(employee);
        surgeryEmployee.setSpecialistPayment(0.0);
    }

    @Test
    public void shouldGetSurgeryEmployeesSuccessfully() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID))
            .thenReturn(List.of(surgeryEmployee));

        List<SurgeryEmployee> result = surgeryEmployeeService.getSurgeryEmployees(SURGERY_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(forSurgeryPort).getSurgery(SURGERY_ID);
        verify(surgeryEmployeeRepository).findBySurgeryId(SURGERY_ID);
    }

    @Test
    public void shouldAddEmployeeToSurgerySuccessfully() throws NotFoundException, DuplicatedEntryException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(false);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of(surgeryEmployee));

        List<SurgeryEmployee> result = surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).save(any(SurgeryEmployee.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryWhenAddingExistingEmployee() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(true);

        assertThrows(DuplicatedEntryException.class, () -> {
            surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID);
        });

        verify(surgeryEmployeeRepository, never()).save(any(SurgeryEmployee.class));
    }

    @Test
    public void shouldRemoveEmployeeFromSurgerySuccessfully() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(true);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of());

        List<SurgeryEmployee> result = surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).deleteBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID);
    }

    @Test
    public void shouldThrowNotFoundWhenRemovingNonexistentEmployee() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);
        });

        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndEmployeeId(anyString(), anyString());
    }

    @Test
    public void shouldAddSpecialistToSurgerySuccessfully() throws NotFoundException, DuplicatedEntryException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(forSurgeryTypePort.getSurgeryType(surgeryType.getId())).thenReturn(surgeryType);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID)).thenReturn(false);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of());

        List<SurgeryEmployee> result = surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).save(any(SurgeryEmployee.class));
    }

    @Test
    public void shouldThrowDuplicatedEntryWhenAddingExistingSpecialist() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(forSurgeryTypePort.getSurgeryType(surgeryType.getId())).thenReturn(surgeryType);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID)).thenReturn(true);

        assertThrows(DuplicatedEntryException.class, () -> {
            surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);
        });

        verify(surgeryEmployeeRepository, never()).save(any(SurgeryEmployee.class));
    }

    @Test
    public void shouldRemoveSpecialistFromSurgerySuccessfully() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID)).thenReturn(true);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of());

        List<SurgeryEmployee> result = surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).deleteBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID);
    }

    @Test
    public void shouldThrowNotFoundWhenRemovingNonexistentSpecialist() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);
        });

        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndSpecialistEmployeeId(anyString(), anyString());
    }

}
