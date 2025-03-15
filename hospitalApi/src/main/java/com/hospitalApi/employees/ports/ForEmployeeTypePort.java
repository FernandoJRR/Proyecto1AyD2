package com.hospitalApi.employees.ports;

import java.util.List;

import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForEmployeeTypePort {

    public boolean verifyExistsEmployeeTypeByName(String employeeType) throws NotFoundException;

    public boolean verifyExistsEmployeeTypeById(String employeeType) throws NotFoundException;

    public List<EmployeeType> findAllEmployeesTypes();
}
