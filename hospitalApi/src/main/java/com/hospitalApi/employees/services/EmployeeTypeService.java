package com.hospitalApi.employees.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.repositories.EmployeeTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeTypeService implements ForEmployeeTypePort {

    private final EmployeeTypeRepository employeeTypeRepository;

    public boolean existsEmployeeTypeByName(EmployeeType employeeType) {
        return employeeTypeRepository.existsByName(employeeType.getName());
    }

    public boolean existsEmployeeTypeById(EmployeeType employeeType) {
        return employeeTypeRepository.existsById(employeeType.getId());
    }

    public List<EmployeeType> findAllEmployeesTypes() {
        return employeeTypeRepository.findAll();
    }
}
