package com.hospitalApi.consults.services;

import java.math.BigDecimal;
import java.util.List;

import com.hospitalApi.consults.port.ForEmployeeConsultPort;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hospitalApi.consults.dtos.ConsutlFilterDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.consults.specifications.ConsultSpecifications;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.rooms.ports.ForRoomUsagePort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;
import org.springframework.data.domain.Sort;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConsultService implements ForConsultPort {

    private final ConsultRepository consultRepository;
    private final ForPatientPort forPatientPort;
    private final ForSurgeryCalculationPort forSurgeryCalculationService;
    private final ForEmployeeConsultPort forEmployeeConsultPort;
    private final ForRoomUsagePort forRoomUsagePort;

    @Override
    public Consult findById(String id) throws NotFoundException {
        return consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
    }

    @Override
    public Consult findConsultAndIsNotPaid(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada");
        }
        return consult;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Consult createConsult(String patientId, String employeeId, Double costoConsulta) throws NotFoundException {
        // Creamos la consulta con el paciente
        Patient patient = forPatientPort.getPatient(patientId);
        Consult consult = new Consult(patient, costoConsulta);
        Consult savedConsult = consultRepository.save(consult);
        // Asociamos la consulta con el empleado
        forEmployeeConsultPort.createEmployeeConsult(savedConsult, employeeId);
        return savedConsult;
    }

    @Override
    public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO)
            throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada no se puede modificar");
        }
        consult.updateConsultFromDTO(updateConsultRequestDTO);
        return consultRepository.save(consult);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Double obtenerTotalConsulta(String id) throws NotFoundException {
        Consult consult = findById(id);
        Double totalCirugias = forSurgeryCalculationService.totalSurgerisByConsult(id);
        Double totalHabitacion = 0.00;
        if (consult.getIsInternado()) {
            RoomUsage roomUsage = forRoomUsagePort.closeRoomUsage(consult);
            totalHabitacion = roomUsage.getPrice().multiply(BigDecimal.valueOf(roomUsage.getUsageDays())).doubleValue();
        }
        Double newTotalCost = consult.getCostoConsulta() + totalCirugias + totalHabitacion;
        consult.setCostoTotal(newTotalCost);
        consultRepository.save(consult);
        return consult.getCostoTotal();
    }

    @Override
    public Consult pagarConsulta(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada");
        }
        consult.setIsPaid(true);
        return consultRepository.save(consult);
    }

    @Override
    public List<Consult> getAllConsults() {
        return consultRepository.findAll();
    }

    @Override
    public List<Consult> getConsults(ConsutlFilterDTO consutlFilterDTO) {
        Specification<Consult> patientSpecification = Specification
                .where(ConsultSpecifications.hasPatientDpi(consutlFilterDTO.getPatientDpi()))
                .or(ConsultSpecifications.hasPatientFirstnames(consutlFilterDTO.getPatientFirstnames()))
                .or(ConsultSpecifications.hasPatientLastnames(consutlFilterDTO.getPatientLastnames()));

        Specification<Consult> employeeSpecification = Specification
                .where(ConsultSpecifications.hasEmployeeId(consutlFilterDTO.getEmployeeId()))
                .or(ConsultSpecifications.hasEmployeeFirstName(consutlFilterDTO.getEmployeeFirstName()))
                .or(ConsultSpecifications.hasEmployeeLastName(consutlFilterDTO.getEmployeeLastName()));

        Specification<Consult> specification = Specification
                .where(ConsultSpecifications.hasId(consutlFilterDTO.getConsultId()))
                .and(patientSpecification)
                .and(employeeSpecification)
                .and(ConsultSpecifications.isPaid(consutlFilterDTO.getIsPaid()))
                .and(ConsultSpecifications.isInternado(consutlFilterDTO.getIsInternado()));

        List<Consult> consults = consultRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"));
        return consults;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Consult markConsultInternado(String id, String habitacionId)
            throws NotFoundException, IllegalStateException, DuplicatedEntryException {
        Consult consult = findById(id);
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada no se puede modificar");
        }
        forRoomUsagePort.asignRoomToConsult(habitacionId, consult);
        consult.setIsInternado(true);
        consult = consultRepository.save(consult);
        return consult;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Consult endInternado(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        if (!consult.getIsInternado()) {
            throw new IllegalStateException("La consulta con id " + id + " no es de tipo internado");
        }
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada no se puede modificar");
        }
        obtenerTotalConsulta(id);
        consult = findById(id);
        return consult;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Consult endConsult(String id) throws NotFoundException, IllegalStateException {
        Consult consult = findById(id);
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada no se puede modificar");
        }
        obtenerTotalConsulta(id);
        consult = findById(id);
        return consult;
    }
}
