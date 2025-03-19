package com.hospitalApi.employees.ports;

import java.time.LocalDate;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForEmployeeHistoryPort {
    public EmployeeHistory createEmployeeHistoryHiring(Employee employee, LocalDate hiringDate)
                    throws NotFoundException;

}
