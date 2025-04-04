package com.hospitalApi.surgery.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.SpecialistEmployee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.employees.ports.ForSpecialistEmployeePort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.models.Surgery;
import com.hospitalApi.surgery.models.SurgeryEmployee;
import com.hospitalApi.surgery.models.SurgeryType;
import com.hospitalApi.surgery.ports.ForSurgeryEmployeePort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import com.hospitalApi.surgery.ports.ForSurgeryTypePort;
import com.hospitalApi.surgery.repositories.SurgeryEmployeeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgeryEmployeeService implements ForSurgeryEmployeePort {

    private final ForSurgeryPort forSurgeryPort;
    private final ForEmployeesPort forEmployeesPort;
    private final ForSpecialistEmployeePort forSpecialistEmployeePort;
    private final ForSurgeryTypePort forSurgeryTypePort;
    private final SurgeryEmployeeRepository sugeryEmployeeRepository;

    @Override
    public List<SurgeryEmployee> getSurgeryEmployees(String surgeryId) throws NotFoundException {
        Surgery surgery = forSurgeryPort.getSurgery(surgeryId);
        return sugeryEmployeeRepository.findBySurgeryId(surgery.getId());
    }

    @Override
    public List<SurgeryEmployee> addEmpleoyeeToSurgery(String surgeryId, String employeeId)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException {
        Surgery surgery = forSurgeryPort.getSurgery(surgeryId);
        if (forSurgeryPort.surgeryAsPerformed(surgeryId)) {
            throw new IllegalStateException(
                    "No se puede eliminar el especialista porque la cirugía ya ha sido realizada.");
        }
        Employee employee = forEmployeesPort.findEmployeeById(employeeId);
        if (sugeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(surgery.getId(), employeeId)) {
            throw new DuplicatedEntryException("El empleado con id " + employeeId + " ya está asignado a la cirugía");
        }
        SurgeryEmployee surgeryEmployee = new SurgeryEmployee(surgery, employee, null, BigDecimal.ZERO);
        sugeryEmployeeRepository.save(surgeryEmployee);
        return sugeryEmployeeRepository.findBySurgeryId(surgery.getId());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<SurgeryEmployee> removeEmployeeFromSurgery(String surgeryId, String employeeId)
            throws NotFoundException, IllegalStateException {
        Surgery surgery = forSurgeryPort.getSurgery(surgeryId);
        if (forSurgeryPort.surgeryAsPerformed(surgeryId)) {
            throw new IllegalStateException(
                    "No se puede eliminar el especialista porque la cirugía ya ha sido realizada.");
        }
        // Verificamos si la cantidad de asignaciones es mayor a 1, si es así no se
        // puede eliminar
        if (!(getSurgeryEmployees(surgeryId).size() > 1)) {
            throw new IllegalStateException("Debe de haber al menos un doctor asignado a la cirugía.");
        }
        Employee employee = forEmployeesPort.findEmployeeById(employeeId);
        if (!sugeryEmployeeRepository.existsBySurgeryIdAndEmployeeId(surgery.getId(), employeeId)) {
            throw new NotFoundException("El empleado con id " + employeeId + " no está asignado a la cirugía");
        }
        sugeryEmployeeRepository.deleteBySurgeryIdAndEmployeeId(surgery.getId(), employee.getId());
        return sugeryEmployeeRepository.findBySurgeryId(surgery.getId());
    }

    @Override
    public List<SurgeryEmployee> addSpecialistToSurgery(String surgeryId, String specialistId)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException {
        Surgery surgery = forSurgeryPort.getSurgery(surgeryId);
        if (forSurgeryPort.surgeryAsPerformed(surgeryId)) {
            throw new IllegalStateException(
                    "No se puede eliminar el especialista porque la cirugía ya ha sido realizada.");
        }
        SpecialistEmployee specialist = forSpecialistEmployeePort.getSpecialistEmployeeById(specialistId);
        SurgeryType surgeryType = forSurgeryTypePort.getSurgeryType(surgery.getSurgeryType().getId());
        if (sugeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(surgery.getId(), specialistId)) {
            throw new DuplicatedEntryException(
                    "El especialista con id " + specialistId + " ya está asignado a la cirugía");
        }
        SurgeryEmployee surgeryEmployee = new SurgeryEmployee(
                surgery, null, specialist, surgeryType.getSpecialistPayment());
        sugeryEmployeeRepository.save(surgeryEmployee);
        return sugeryEmployeeRepository.findBySurgeryId(surgery.getId());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<SurgeryEmployee> removeSpecialistFromSurgery(String surgeryId, String specialistId)
            throws NotFoundException, IllegalStateException {
        Surgery surgery = forSurgeryPort.getSurgery(surgeryId);
        if (forSurgeryPort.surgeryAsPerformed(surgeryId)) {
            throw new IllegalStateException(
                    "No se puede eliminar el especialista porque la cirugía ya ha sido realizada.");
        }
        // Verificamos si la cantidad de asignaciones es mayor a 1, si es así no se
        // puede eliminar
        if (!(getSurgeryEmployees(surgeryId).size() > 1)) {
            throw new IllegalStateException("Debe de haber al menos un doctor asignado a la cirugía.");
        }
        SpecialistEmployee specialist = forSpecialistEmployeePort.getSpecialistEmployeeById(specialistId);
        if (!sugeryEmployeeRepository.existsBySurgeryIdAndSpecialistEmployeeId(surgery.getId(), specialistId)) {
            throw new NotFoundException("El especialista con id " + specialistId + " no está asignado a la cirugía");
        }
        sugeryEmployeeRepository.deleteBySurgeryIdAndSpecialistEmployeeId(surgery.getId(), specialist.getId());
        return sugeryEmployeeRepository.findBySurgeryId(surgery.getId());
    }

    @Override
    public List<SurgeryEmployee> addDoctorToSurgery(String surgeryId, String doctorId, Boolean isSpecialist)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException {
        if (isSpecialist == null) {
            throw new IllegalStateException("El tipo de doctor no puede ser nulo");
        }
        if (isSpecialist) {
            return addSpecialistToSurgery(surgeryId, doctorId);
        } else {
            return addEmpleoyeeToSurgery(surgeryId, doctorId);
        }
    }

    @Override
    public List<SurgeryEmployee> removeDoctorFromSurgery(String surgeryId, String doctorId, Boolean isSpecialist)
            throws NotFoundException, IllegalStateException {
        if (isSpecialist == null) {
            throw new IllegalStateException("El tipo de doctor no puede ser nulo");
        }
        if (isSpecialist) {
            return removeSpecialistFromSurgery(surgeryId, doctorId);
        } else {
            return removeEmployeeFromSurgery(surgeryId, doctorId);
        }
    }
}
