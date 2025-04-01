package com.hospitalApi.consults.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.consults.port.ForEmployeeConsultPort;
import com.hospitalApi.consults.repositories.EmployeeConsultRepository;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class EmployeeConsultService implements ForEmployeeConsultPort {

    private final ForEmployeesPort forEmployeesPort;
    private final EmployeeConsultRepository employeeConsultRepository;

    @Override
    public EmployeeConsult createEmployeeConsult(Consult consult, String employeeId) throws NotFoundException {
        Employee employee = forEmployeesPort.findEmployeeById(employeeId);
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
    public List<EmployeeConsult> addEmployeeConsultsByConsultIdAndEmployeeId(Consult consult, String employeeId)
            throws NotFoundException, IllegalStateException {
        if (employeeConsultRepository.existsByConsultIdAndEmployeeId(consult.getId(), employeeId)) {
            throw new IllegalStateException(
                    "El empleado " + employeeId + " ya está asignado a la consulta " + consult.getId());
        }
        EmployeeConsult employeeConsult = createEmployeeConsult(consult, employeeId);
        return getEmployeeConsultsByConsultId(employeeConsult.getConsult().getId());
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
