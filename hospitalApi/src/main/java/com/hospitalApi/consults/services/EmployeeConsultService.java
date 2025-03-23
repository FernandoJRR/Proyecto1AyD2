package com.hospitalApi.consults.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.port.ForEmployeeConsultPort;
import com.hospitalApi.consults.repositories.EmployeeConsultRepository;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmployeeConsultService implements ForEmployeeConsultPort {

    private final ForEmployeesPort forEmployeesPort;
    private final ForConsultPort forConsultPort;
    private final EmployeeConsultRepository employeeConsultRepository;

    @Override
    public EmployeeConsult createEmployeeConsult(String consultId, String employeeId) throws NotFoundException {
        Employee employee = forEmployeesPort.findEmployeeById(employeeId);
        Consult consult = forConsultPort.findById(consultId);
        EmployeeConsult employeeConsult = new EmployeeConsult(consult, employee);
        employeeConsult = employeeConsultRepository.save(employeeConsult);
        return employeeConsult;
    }

    @Override
    public List<EmployeeConsult> getEmployeeConsultsByConsultId(String consultId) throws NotFoundException {
        return employeeConsultRepository.findByConsultId(consultId);
    }

    @Override
    public List<EmployeeConsult> getEmployeeConsultsByEmployeeId(String employeeId) throws NotFoundException {
        return employeeConsultRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<EmployeeConsult> deleteEmployeeConsultsByConsultIdAndEmployeeId(String consultId, String employeeId)
            throws NotFoundException, IllegalStateException {

        if (!employeeConsultRepository.existsByConsultIdAndEmployeeId(consultId, employeeId)) {
            throw new NotFoundException("El empleado " + employeeId + " no está asignado a la consulta " + consultId);
        }

        // Debemos de verificar que la cantidad de empleados asignados a la consulta sea
        // mayor a 1
        if (employeeConsultRepository.countByConsultId(consultId) <= 1) {
            throw new IllegalStateException(
                    "No se puede eliminar el empleado " + employeeId + " de la consulta " + consultId
                            + " porque es el único empleado asignado a la consulta");
        }

        employeeConsultRepository.deleteByConsultIdAndEmployeeId(consultId, employeeId);
        return getEmployeeConsultsByConsultId(consultId);
    }

}
