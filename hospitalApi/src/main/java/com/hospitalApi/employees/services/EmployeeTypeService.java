package com.hospitalApi.employees.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.repositories.EmployeeTypeRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeTypeService implements ForEmployeeTypePort {

    private final EmployeeTypeRepository employeeTypeRepository;

    /**
     * 
     * @param employeeType
     * @throws NotFoundException si el nombre del tipo empleado no existe
     */
    public boolean verifyExistsEmployeeTypeByName(EmployeeType employeeType) throws NotFoundException {

        if (!employeeTypeRepository.existsByName(employeeType.getName())) {
            throw new NotFoundException("No existe n tipo de empleado con el nombre especificado.");
        }

        return true;

    }

    public boolean verifyExistsEmployeeTypeById(EmployeeType employeeType) throws NotFoundException {

        if (!employeeTypeRepository.existsById(employeeType.getId())) {
            throw new NotFoundException("No existe n tipo de empleado con el nombre especificado.");
        }

        return true;

    }

    public List<EmployeeType> findAllEmployeesTypes() {
        return employeeTypeRepository.findAll();
    }
}
