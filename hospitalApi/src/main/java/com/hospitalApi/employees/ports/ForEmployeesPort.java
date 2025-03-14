package com.hospitalApi.employees.ports;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;

public interface ForEmployeesPort {

    public Employee createEmployee(Employee newEmployee, EmployeeType employeeType, User newUser)
            throws DuplicatedEntryException, NotFoundException;

}
