package com.hospitalApi.employees.ports;

import java.util.List;

import com.hospitalApi.employees.dtos.UpdateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForSpecialistEmployeePort {
    public SpecialistEmployee createSpecialistEmployee(SpecialistEmployee specialistEmployee)
            throws DuplicatedEntryException;

    public SpecialistEmployee getSpecialistEmployeeById(String specialistEmployeeId) throws NotFoundException;

    public SpecialistEmployee getSpecialistEmployeeByDpi(String dpi) throws NotFoundException;

    public List<SpecialistEmployee> getSpecialistEmployees(String search);

    public SpecialistEmployee updateSpecialistEmployee(
            UpdateSpecialistEmpleoyeeRequestDTO updateSpecialistEmpleoyeeRequestDTO, String specialistEmployeeId)
            throws NotFoundException, DuplicatedEntryException;
}
