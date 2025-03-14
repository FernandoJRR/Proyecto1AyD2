package com.hospitalApi.employees.ports;

import java.util.List;

import com.hospitalApi.employees.models.EmployeeType;

public interface ForEmployeeTypePort {

    public boolean existsEmployeeTypeByName(EmployeeType employeeType);

    public boolean existsEmployeeTypeById(EmployeeType employeeType);

    public List<EmployeeType> findAllEmployeesTypes();
}
