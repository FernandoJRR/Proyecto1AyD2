package com.hospitalApi.surgery.ports;

import java.util.List;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.models.SurgeryEmployee;

public interface ForSurgeryEmployeePort {

    public List<SurgeryEmployee> getSurgeryEmployees(String surgeryId) throws NotFoundException;

    public List<SurgeryEmployee> addEmpleoyeeToSurgery(String surgeryId, String employeeId)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException;

    public List<SurgeryEmployee> removeEmployeeFromSurgery(String surgeryId, String employeeId)
            throws NotFoundException, IllegalStateException;

    public List<SurgeryEmployee> addSpecialistToSurgery(String surgeryId, String specialistId)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException;

    public List<SurgeryEmployee> removeSpecialistFromSurgery(String surgeryId, String specialistId)
            throws NotFoundException, IllegalStateException;

    public List<SurgeryEmployee> addDoctorToSurgery(String surgeryId, String doctorId,
            Boolean isSpecialist)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException;

    public List<SurgeryEmployee> removeDoctorFromSurgery(String surgeryId, String doctorId,
            Boolean isSpecialist) throws NotFoundException, IllegalStateException;

}
