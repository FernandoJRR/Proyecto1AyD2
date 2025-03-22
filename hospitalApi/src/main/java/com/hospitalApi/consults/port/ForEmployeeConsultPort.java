package com.hospitalApi.consults.port;

import java.util.List;

import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.shared.exceptions.BadRequestException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.validation.ConstraintViolationException;

public interface ForEmployeeConsultPort {

    public EmployeeConsult createEmployeeConsult(String consultId, String employeeId) throws NotFoundException;

    public List<EmployeeConsult> getEmployeeConsultsByConsultId(String consultId) throws NotFoundException;

    public List<EmployeeConsult> getEmployeeConsultsByEmployeeId(String employeeId) throws NotFoundException;

    public List<EmployeeConsult> deleteEmployeeConsultsByConsultIdAndEmployeeId(String consultId, String employeeId)
            throws NotFoundException, BadRequestException;
}
