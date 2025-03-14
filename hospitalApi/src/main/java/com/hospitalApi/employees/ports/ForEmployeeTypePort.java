package com.hospitalApi.employees.ports;

import java.util.List;

import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForEmployeeTypePort {

    public boolean verifyExistsEmployeeTypeByName(EmployeeType employeeType) throws NotFoundException;

    public boolean verifyExistsEmployeeTypeById(EmployeeType employeeType) throws NotFoundException;

    public List<EmployeeType> findAllEmployeesTypes();
}
