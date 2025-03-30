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
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(false);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of(surgeryEmployee));

        List<SurgeryEmployee> result = surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(surgeryEmployeeRepository).save(any(SurgeryEmployee.class));
        verify(surgeryEmployeeRepository).findBySurgeryId(SURGERY_ID);
    }

    @Test
    public void shouldThrowIllegalStateWhenSurgeryAlreadyPerformed() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID));

        verify(forEmployeesPort, never()).findEmployeeById(any());
        verify(surgeryEmployeeRepository, never()).save(any());
    }

    @Test
    public void shouldThrowDuplicatedEntryWhenEmployeeAlreadyAssigned() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(true);

        assertThrows(DuplicatedEntryException.class,
                () -> surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID));

        verify(surgeryEmployeeRepository, never()).save(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSurgeryNotFound() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID));

        verify(forSurgeryPort).getSurgery(SURGERY_ID);
        verify(forEmployeesPort, never()).findEmployeeById(any());
        verify(surgeryEmployeeRepository, never()).save(any());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenEmployeeNotFound() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> surgeryEmployeeService.addEmpleoyeeToSurgery(SURGERY_ID, EMPLOYEE_ID));

        verify(surgeryEmployeeRepository, never()).save(any());
    }

    @Test
    public void shouldRemoveEmployeeSuccessfully() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(true);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of(surgeryEmployee));

        List<SurgeryEmployee> result = surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).deleteBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID);
        verify(surgeryEmployeeRepository).findBySurgeryId(SURGERY_ID);
    }

    @Test
    public void shouldThrowWhenSurgeryAlreadyPerformedOnRemoveEmployee() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);
        });

        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndEmployeeId(any(), any());
    }

    @Test
    public void shouldThrowWhenEmployeeNotAssignedToSurgery() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);

        SurgeryEmployee anotherEmployee = new SurgeryEmployee();
        anotherEmployee.setEmployee(new Employee());
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID))
                .thenReturn(List.of(surgeryEmployee, anotherEmployee));

        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);
        });

        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndEmployeeId(any(), any());
    }

    @Test
    public void shouldThrowWhenSurgeryNotFoundOnRemoveEmployee() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);
        });
    }

    @Test
    public void shouldThrowWhenEmployeeNotFoundOnRemoveEmployee() throws NotFoundException {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);

        SurgeryEmployee anotherEmployee = new SurgeryEmployee();
        anotherEmployee.setEmployee(new Employee());
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID))
                .thenReturn(List.of(surgeryEmployee, anotherEmployee));

        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeEmployeeFromSurgery(SURGERY_ID, EMPLOYEE_ID);
        });

        verify(forEmployeesPort).findEmployeeById(EMPLOYEE_ID);
        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndEmployeeId(any(), any());
    }

    @Test
    public void shouldAddSpecialistSuccessfully() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(forSurgeryTypePort.getSurgeryType(surgeryType.getId())).thenReturn(surgeryType);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID))
                .thenReturn(false);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of(surgeryEmployee));

        List<SurgeryEmployee> result = surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).save(any(SurgeryEmployee.class));
        verify(surgeryEmployeeRepository).findBySurgeryId(SURGERY_ID);
    }

    @Test
    public void shouldThrowWhenSurgeryAlreadyPerformedOnAddSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldThrowWhenSpecialistAlreadyAssigned() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(forSurgeryTypePort.getSurgeryType(surgeryType.getId())).thenReturn(surgeryType);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID))
                .thenReturn(true);

        assertThrows(DuplicatedEntryException.class, () -> {
            surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldThrowWhenSurgeryNotFoundOnAddSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldThrowWhenSpecialistNotFoundOnAddSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID))
                .thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldThrowWhenSurgeryTypeNotFoundOnAddSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(forSurgeryTypePort.getSurgeryType(surgeryType.getId())).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.addSpecialistToSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldRemoveSpecialistSuccessfully() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID))
                .thenReturn(true);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of());

        List<SurgeryEmployee> result = surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).deleteBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID);
        verify(surgeryEmployeeRepository).findBySurgeryId(SURGERY_ID);
    }

    @Test
    public void shouldThrowWhenSurgeryAlreadyPerformedOnRemoveSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldThrowWhenSpecialistNotAssignedOnRemove() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);

        SurgeryEmployee another = new SurgeryEmployee();
        another.setEmployee(new Employee());
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of(surgeryEmployee, another));

        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);
        });

        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndSpecialistEmployeeId(any(), any());
    }

    @Test
    public void shouldThrowWhenSurgeryNotFoundOnRemoveSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);
        });
    }

    @Test
    public void shouldThrowWhenSpecialistNotFoundOnRemoveSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);

        SurgeryEmployee doctor = new SurgeryEmployee();
        doctor.setEmployee(new Employee());
        SurgeryEmployee specialistAssigned = new SurgeryEmployee();
        specialistAssigned.setSpecialistEmployee(specialist);

        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID))
                .thenReturn(List.of(doctor, specialistAssigned));

        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID))
                .thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> {
            surgeryEmployeeService.removeSpecialistFromSurgery(SURGERY_ID, SPECIALIST_ID);
        });

        verify(forSpecialistEmployeePort).getSpecialistEmployeeById(SPECIALIST_ID);
        verify(surgeryEmployeeRepository, never()).deleteBySurgeryIdAndSpecialistEmployeeId(any(), any());
    }

    @Test
    public void shouldAddDoctorToSurgeryAsSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forSpecialistEmployeePort.getSpecialistEmployeeById(SPECIALIST_ID)).thenReturn(specialist);
        when(forSurgeryTypePort.getSurgeryType(surgeryType.getId())).thenReturn(surgeryType);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(SURGERY_ID, SPECIALIST_ID))
                .thenReturn(false);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of());

        List<SurgeryEmployee> result = surgeryEmployeeService.addDoctorToSurgery(SURGERY_ID, SPECIALIST_ID, true);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).save(any(SurgeryEmployee.class));
    }

    @Test
    public void shouldAddDoctorToSurgeryAsEmployee() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(false);
        when(forEmployeesPort.findEmployeeById(EMPLOYEE_ID)).thenReturn(employee);
        when(surgeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(SURGERY_ID, EMPLOYEE_ID)).thenReturn(false);
        when(surgeryEmployeeRepository.findBySurgeryId(SURGERY_ID)).thenReturn(List.of());

        List<SurgeryEmployee> result = surgeryEmployeeService.addDoctorToSurgery(SURGERY_ID, EMPLOYEE_ID, false);

        assertNotNull(result);
        verify(surgeryEmployeeRepository).save(any(SurgeryEmployee.class));
    }

    @Test
    public void shouldThrowWhenDoctorTypeIsNull() {
        assertThrows(IllegalStateException.class, () -> {
            surgeryEmployeeService.addDoctorToSurgery(SURGERY_ID, EMPLOYEE_ID, null);
        });
    }

    @Test
    public void shouldThrowWhenSurgeryAlreadyPerformedOnAddDoctorAsSpecialist() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            surgeryEmployeeService.addDoctorToSurgery(SURGERY_ID, SPECIALIST_ID, true);
        });
    }

    @Test
    public void shouldThrowWhenSurgeryAlreadyPerformedOnAddDoctorAsEmployee() throws Exception {
        when(forSurgeryPort.getSurgery(SURGERY_ID)).thenReturn(surgery);
        when(forSurgeryPort.surgeryAsPerformed(SURGERY_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            surgeryEmployeeService.addDoctorToSurgery(SURGERY_ID, EMPLOYEE_ID, false);
        });
    }

}
