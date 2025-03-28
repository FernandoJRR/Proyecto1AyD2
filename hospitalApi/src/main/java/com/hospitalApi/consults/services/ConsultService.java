package com.hospitalApi.consults.services;

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

    @Override
    public Consult findById(String id) throws NotFoundException {
        return consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
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
    public Consult updateConsult(String id, UpdateConsultRequestDTO updateConsultRequestDTO) throws NotFoundException {
        Consult consult = consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
        consult.updateConsultFromDTO(updateConsultRequestDTO);
        return consultRepository.save(consult);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Double obtenerTotalConsulta(String id) throws NotFoundException {
        Consult consult = findById(id);
        Double totalCirugias = forSurgeryCalculationService.totalSurgerisByConsult(id);
        Double newTotalCost = consult.getCostoConsulta() + totalCirugias;
        consult.setCostoTotal(newTotalCost);
        consultRepository.save(consult);
        return consult.getCostoTotal();
    }

    @Override
    public Consult pagarConsulta(String id) throws NotFoundException, IllegalStateException {
        Consult consult = consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
        // Verificar si la consulta ya fue pagada
        if (consult.getIsPaid()) {
            throw new IllegalStateException("La consulta con id " + id + " ya fue pagada");
        }
        consult.setIsPaid(true);
        return consultRepository.save(consult);
    }

    @Override
    public Consult addHabitacionToConsult(String id, String habitacionId)
            throws NotFoundException, IllegalStateException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addHabitacionToConsult'");
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
        System.out.println("Consults: " + consults);
        return consults;
    }

}
