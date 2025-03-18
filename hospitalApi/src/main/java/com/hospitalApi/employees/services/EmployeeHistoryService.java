package com.hospitalApi.employees.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.enums.HistoryTypeEnum;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForEmployeeHistoryPort;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.EmployeeHistoryRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeHistoryService implements ForEmployeeHistoryPort {

    private final EmployeeHistoryRepository employeeHistoryRepository;
    private final ForHistoryTypePort forHistoryTypePort;

    public EmployeeHistory createEmployeeHistoryHiring(Employee employee)
            throws NotFoundException {
        HistoryType historyTypeContratacion = forHistoryTypePort.findHistoryTypeByName(HistoryTypeEnum.CONTRATACION.name());

        EmployeeHistory employeeHistory = new EmployeeHistory("Se realizo la contratacion");

        employeeHistory.setHistoryType(historyTypeContratacion);
        employeeHistory.setEmployee(employee);

        return employeeHistoryRepository.save(employeeHistory);
    }

}
