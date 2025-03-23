package com.hospitalApi.employees.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.dtos.UpdateSpecialistEmpleoyeeRequestDTO;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.employees.ports.ForSpecialistEmployeePort;
import com.hospitalApi.employees.repositories.SpecialistEmployeeRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SpecialistEmployeeService implements ForSpecialistEmployeePort {

    private final SpecialistEmployeeRepository specialistEmployeeRepository;

    @Override
    public SpecialistEmployee createSpecialistEmployee(SpecialistEmployee specialistEmployee)
            throws DuplicatedEntryException {
        if (specialistEmployeeRepository.existsByDpi(specialistEmployee.getDpi())) {
            throw new DuplicatedEntryException("Ya existe un empleado con el DPI " + specialistEmployee.getDpi());
        }
        return specialistEmployeeRepository.save(specialistEmployee);
    }

    @Override
    public SpecialistEmployee getSpecialistEmployeeById(String specialistEmployeeId) throws NotFoundException {
        return specialistEmployeeRepository.findById(specialistEmployeeId)
                .orElseThrow(
                        () -> new NotFoundException("No se encontró el empleado con el ID " + specialistEmployeeId));
    }

    @Override
    public SpecialistEmployee getSpecialistEmployeeByDpi(String dpi) throws NotFoundException {
        if (!specialistEmployeeRepository.existsByDpi(dpi)) {
            throw new NotFoundException("No se encontró el empleado con el DPI " + dpi);
        }
        return specialistEmployeeRepository.findByDpi(dpi);
    }

    @Override
    public List<SpecialistEmployee> getSpecialistEmployees(String search) {
        if (search == null) {
            return specialistEmployeeRepository.findAll();
        }
        return specialistEmployeeRepository.findByNombresAndApellidos(search, search);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SpecialistEmployee updateSpecialistEmployee(
            UpdateSpecialistEmpleoyeeRequestDTO updateSpecialistEmpleoyeeRequestDTO, String specialistEmployeeId)
            throws NotFoundException, DuplicatedEntryException {
        if (!specialistEmployeeRepository.existsById(specialistEmployeeId)) {
            throw new NotFoundException("No se encontró el empleado con el ID " + specialistEmployeeId);
        }
        if (specialistEmployeeRepository.existsByDpiAndIdNot(updateSpecialistEmpleoyeeRequestDTO.getDpi(),
                specialistEmployeeId)) {
            throw new DuplicatedEntryException(
                    "Ya existe un empleado con el DPI " + updateSpecialistEmpleoyeeRequestDTO.getDpi());
        }
        SpecialistEmployee specialistEmployee = specialistEmployeeRepository.findById(specialistEmployeeId).get();
        specialistEmployee.updateFromDTO(updateSpecialistEmpleoyeeRequestDTO);
        return specialistEmployeeRepository.save(specialistEmployee);
    }

}
