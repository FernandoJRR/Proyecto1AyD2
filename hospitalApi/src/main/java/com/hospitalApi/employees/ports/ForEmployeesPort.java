package com.hospitalApi.employees.ports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.core.annotation.MergedAnnotations.Search;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;

public interface ForEmployeesPort {

        public Employee createEmployee(Employee newEmployee, EmployeeType employeeType, User newUser,
                        EmployeeHistory employeeHistoryDate)
                        throws DuplicatedEntryException, NotFoundException;

        public Employee updateEmployee(String currentId, Employee newData, EmployeeType employeeType)
                        throws NotFoundException;

        public Employee updateEmployeeSalary(String currentId, BigDecimal newSalary, LocalDate salaryDate)
                        throws NotFoundException, InvalidPeriodException;

        public Employee reassignEmployeeType(String employeeId, String employeeTypeId) throws NotFoundException;

        public List<Employee> reassignEmployeeType(List<Employee> employeeIds, String employeeTypeId)
                        throws NotFoundException;

        public Employee findEmployeeById(String employeeId) throws NotFoundException;

        public List<Employee> findEmployees();

        public Employee desactivateEmployee(String currentId)
                        throws NotFoundException, IllegalStateException;

        public List<Employee> getEmployeesByType(String employeeTypeId, String search) throws NotFoundException;

        public List<Employee> getDoctors(String search) throws NotFoundException;

}
