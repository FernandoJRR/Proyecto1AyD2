package com.hospitalApi.consults.port;

import java.util.List;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForEmployeeConsultPort {

    public EmployeeConsult createEmployeeConsult(Consult consult, String employeeId) throws NotFoundException;

    public List<EmployeeConsult> getEmployeeConsultsByConsultId(String consultId) throws NotFoundException;

    public List<EmployeeConsult> getEmployeeConsultsByEmployeeId(String employeeId) throws NotFoundException;

    public List<EmployeeConsult> deleteEmployeeConsultsByConsultIdAndEmployeeId(String consultId, String employeeId)
            throws NotFoundException, IllegalStateException;

    public List<EmployeeConsult> addEmployeeConsultsByConsultIdAndEmployeeId(Consult consult, String employeeId)
            throws NotFoundException, IllegalStateException;
}
