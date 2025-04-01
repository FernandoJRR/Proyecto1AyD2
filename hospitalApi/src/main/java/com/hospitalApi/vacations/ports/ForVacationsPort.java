package com.hospitalApi.vacations.ports;

import java.util.List;
import java.util.Map;

import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.vacations.models.Vacations;

public interface ForVacationsPort {
    public List<Vacations> getAllVacationsForEmployeeOnPeriod(String employeeId, Integer period) throws NotFoundException;
    public Map<Integer, List<Vacations>> getAllVacationsForEmployee(String employeeId) throws NotFoundException;
    public List<Vacations> createVacationsForEmployeeOnPeriod(String employeeId, Integer period, List<Vacations> vacationsPeriods) throws NotFoundException, InvalidPeriodException;
    public List<Vacations> createRandomVacationsForEmployee(String employeeId) throws NotFoundException;
    public List<Vacations> updateVacationsForEmployeeOnPeriod(String employeeId, Integer period, List<Vacations> vacationsPeriods) throws NotFoundException, InvalidPeriodException;
}
