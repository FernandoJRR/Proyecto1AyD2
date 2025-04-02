package com.hospitalApi.reports.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.consults.mappers.ConsultMapper;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.reports.dtos.request.DoctorAssignmentFilter;
import com.hospitalApi.reports.dtos.response.doctorAssignmentReport.EmployeeAssignableResponseDTO;
import com.hospitalApi.reports.ports.ReportService;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorAssignmentReportService
        implements ReportService<List<EmployeeAssignableResponseDTO>, DoctorAssignmentFilter> {

    private final ForEmployeesPort forEmployeesPort;
    private final ConsultMapper consultMapper;

    /**
     * Genera el reporte de doctores con su información y consultas asignadas.
     * Si el filtro lo indica, devuelve solo los médicos que tienen asignaciones.
     *
     * @param filter objeto con la opción para filtrar solo médicos asignados o no
     * @return lista de doctores con sus consultas asignadas (puede ser vacía)
     */
    @Override
    public List<EmployeeAssignableResponseDTO> generateReport(DoctorAssignmentFilter filter) {
        try {
            // mandamos a traer a todos los doctores
            List<Employee> doctors = forEmployeesPort.getDoctors("");
            // ahora vemos si el filtro especifica que solo se desean los asignados
            // de ser asi entonces mandamos a filtrar
            if (filter.getOnlyAssigneds()) {
                doctors = filterOnlyDoctorsWithAssignments(doctors);
            } else if (filter.getOnlyNotAssigneds()) {
                doctors = filterOnlyDoctorsWithoutAssignments(doctors);
            }

            // mandamos a construir los dtos para la respuesta y la respuesta final

            return buildDoctorAssignmentResponses(doctors);

        } catch (NotFoundException e) {
            return List.of();// si algo faloo entonces devolver una lista vacia
        }
    }

    /**
     * Filtra la lista de médicos y deja solo aquellos que tienen asignaciones.
     *
     * @param doctors lista de todos los médicos
     * @return lista con solo los médicos que tienen asignaciones
     */
    private List<Employee> filterOnlyDoctorsWithAssignments(List<Employee> doctors) {
        // filtramos solo los doctores en donde sus asignaciones no esten vacias
        List<Employee> onyDoctorsAssigneds = doctors.stream()
                .filter(doctor -> doctor.getEmployeeConsults() != null && !doctor.getEmployeeConsults().isEmpty())
                .collect(Collectors.toList());// reconstruimos la lista
        return onyDoctorsAssigneds;
    }

    private List<Employee> filterOnlyDoctorsWithoutAssignments(List<Employee> doctors) {
        // filtramos solo los doctores en donde sus asignaciones no esten vacias
        List<Employee> onyDoctorsAssigneds = doctors.stream()
                .filter(doctor -> doctor.getEmployeeConsults() == null || doctor.getEmployeeConsults().isEmpty())
                .collect(Collectors.toList());// reconstruimos la lista
        return onyDoctorsAssigneds;
    }

    /**
     * Crea la lista de respuestas para el reporte de doctores.
     * Por cada doctor construye un DTO con sus datos y sus consultas asignadas.
     *
     * @param doctors lista de médicos (ya filtrados si es necesario)
     * @return lista de respuestas que se usarán para el reporte
     */
    private List<EmployeeAssignableResponseDTO> buildDoctorAssignmentResponses(List<Employee> doctors) {
        // aqui guardaremos nuestras respuesta
        List<EmployeeAssignableResponseDTO> responses = new ArrayList<>();
        // por cada uno de los doctores ya filtrados
        for (Employee assignedDoctor : doctors) {
            // extraemos las cosunltas del doctor de sus asignacioens
            List<Consult> doctorConsults = extractConsultsFromAssignments(assignedDoctor.getEmployeeConsults());
            // las convertimos dto
            List<ConsultResponseDTO> consultResponseDTOs = consultMapper.fromConsultsToResponse(doctorConsults);
            // con la info recabada creamos un elemento de toda la respuesta
            EmployeeAssignableResponseDTO response = new EmployeeAssignableResponseDTO(assignedDoctor.getFullName(),
                    assignedDoctor.getCui(),
                    assignedDoctor.getSalary(),
                    assignedDoctor.getEmployeeType().getName(),
                    consultResponseDTOs);
            // afgregamos la repsuesta todas las demasF
            responses.add(response);
        }

        return responses;
    }

    /**
     * Extrae todas las consultas que están asignadas a un médico.
     *
     * @param employeeConsults lista de asignaciones (relación doctor-consulta)
     * @return lista de consultas asociadas a ese doctor
     */
    private List<Consult> extractConsultsFromAssignments(List<EmployeeConsult> employeeConsults) {
        // aqui vamos a extraer todas las consultas que estan a cargo del doctor en
        // cuestion
        List<Consult> doctorConsults = new ArrayList<>();
        // por cada una de las asignaciones
        for (EmployeeConsult asignment : employeeConsults) {
            doctorConsults.add(asignment.getConsult());// agregamos la consulta
        }
        return doctorConsults;
    }

}
