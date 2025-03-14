package com.hospitalApi.employees.ports;

import com.hospitalApi.employees.models.EmployeeType;

public interface ForEmployeeTypePort {

    public boolean existsEmployeeTypeByName(EmployeeType employeeType);

    public boolean existsEmployeeTypeById(EmployeeType employeeType);
}
