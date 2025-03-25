package com.hospitalApi.employees.ports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForEmployeeHistoryPort {
    public EmployeeHistory createEmployeeHistoryHiring(Employee employee, LocalDate hiringDate)
                    throws NotFoundException;

    public EmployeeHistory createEmployeeHistorySalaryIncrease(Employee employee, BigDecimal newSalary, LocalDate date)
                    throws NotFoundException, InvalidPeriodException;
    public EmployeeHistory createEmployeeHistorySalaryDecrease(Employee employee, BigDecimal newSalary, LocalDate date)
                    throws NotFoundException, InvalidPeriodException;

    public List<EmployeeHistory> getEmployeeHistory(Employee employee) throws NotFoundException;

    public Optional<EmployeeHistory> getLastEmployeeSalaryUntilDate(Employee employee, LocalDate date) throws NotFoundException;
    public Optional<EmployeeHistory> getMostRecentEmployeeSalary(Employee employee) throws NotFoundException;

    public Optional<EmployeeHistory> getEmployeeHiringDate(Employee employee) throws NotFoundException;

    public boolean isValidEmployeePeriod(Employee employee, LocalDate date);
}
