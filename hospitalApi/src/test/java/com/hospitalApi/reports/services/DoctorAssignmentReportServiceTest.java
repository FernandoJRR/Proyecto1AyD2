package com.hospitalApi.reports.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.consults.mappers.ConsultMapper;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.reports.dtos.request.DoctorAssignmentFilter;
import com.hospitalApi.reports.dtos.response.doctorAssignmentReport.EmployeeAssignableResponseDTO;
import com.hospitalApi.shared.enums.EmployeeTypeEnum;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryEmployee;

@ExtendWith(MockitoExtension.class)
public class DoctorAssignmentReportServiceTest {

    @Mock
    private ForEmployeesPort forEmployeesPort;

    @Mock
    private ConsultMapper consultMapper;

    @InjectMocks
    private DoctorAssignmentReportService doctorAssignmentReportService;

    private static final String DOCTOR_CUI = "1234567890101";
    private static final String DOCTOR_FIRST_NAME = "Juan";
    private static final String DOCTOR_LAST_NAME = "Pérez";
    private static final BigDecimal DOCTOR_SALARY = new BigDecimal(8000);
    private static final BigDecimal DOCTOR_SPECIALIST_PAYMENT = new BigDecimal(200);

    private Employee doctor;
    private Consult consult;
    private EmployeeConsult employeeConsult;
    private Surgery surgery;
    private SurgeryEmployee surgeryEmployee;
    private ConsultResponseDTO consultResponseDTO;

    @BeforeEach
    public void setUp() {
        doctor = new Employee(DOCTOR_CUI, DOCTOR_FIRST_NAME, DOCTOR_LAST_NAME, DOCTOR_SALARY, null, null);
        doctor.setEmployeeType(EmployeeTypeEnum.DOCTOR.getEmployeeType());

        consult = new Consult();
        employeeConsult = new EmployeeConsult();
        employeeConsult.setConsult(consult);
        doctor.setEmployeeConsults(List.of(employeeConsult));

        surgery = new Surgery();
        surgeryEmployee = new SurgeryEmployee(surgery, doctor, null, DOCTOR_SPECIALIST_PAYMENT);
        doctor.setSurgeryEmployees(List.of(surgeryEmployee));

        consultResponseDTO = new ConsultResponseDTO();
    }

    /**
     * dado: un médico con asignaciones activas.
     * cuando: se genera el reporte con filtro para mostrar solo médicos asignados.
     * entonces: se retorna correctamente el listado con sus datos y consultas.
     */
    @Test
    public void shouldReturnOnlyAssignedDoctorsReport() throws Exception {
        // ARRANGE
        when(forEmployeesPort.getDoctors(anyString())).thenReturn(List.of(doctor));
        when(consultMapper.fromConsultsToResponse(anyList()))
                .thenReturn(List.of(consultResponseDTO));

        DoctorAssignmentFilter filter = new DoctorAssignmentFilter(true, false);

        // ACT
        List<EmployeeAssignableResponseDTO> result = doctorAssignmentReportService.generateReport(filter);
        EmployeeAssignableResponseDTO dto = result.get(0);

        // ASSERT
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(doctor.getFullName(), dto.getEmployeeFullName()),
                () -> assertEquals(doctor.getCui(), dto.getCui()),
                () -> assertEquals(doctor.getSalary(), dto.getSalary()),
                () -> assertEquals(1, dto.getAssignedConsults().size()));
    }

    @Test
    public void shouldReturnOnlyNotAssignedDoctorsReport() throws Exception {
        // ARRANGE
        when(forEmployeesPort.getDoctors(anyString())).thenReturn(List.of(doctor));
        DoctorAssignmentFilter filter = new DoctorAssignmentFilter(false, true);

        // ACT
        List<EmployeeAssignableResponseDTO> result = doctorAssignmentReportService.generateReport(filter);
        // ASSERT
        assertAll(// como estamos indicando que queremos que devuelva los que no tienen
                  // asiganciones y este si tiene entonces debe devolver vacio
                () -> assertEquals(0, result.size()));
    }

    /**
     * dado: un filtro sin restricciones.
     * cuando: se genera el reporte.
     * entonces: se incluye al médico aunque no tenga asignaciones.
     */
    @Test
    public void shouldIncludeAllDoctorsWhenFilterIsNull() throws Exception {
        // arrange
        // esto simula que el doc no tiene asignacioens
        doctor.setSurgeryEmployees(null);
        when(forEmployeesPort.getDoctors(anyString())).thenReturn(List.of(doctor));
        when(consultMapper.fromConsultsToResponse(anyList()))
                .thenReturn(List.of(consultResponseDTO));

        DoctorAssignmentFilter filter = new DoctorAssignmentFilter(false, false);

        List<EmployeeAssignableResponseDTO> result = doctorAssignmentReportService.generateReport(filter);

        assertEquals(1, result.size());
    }
}
