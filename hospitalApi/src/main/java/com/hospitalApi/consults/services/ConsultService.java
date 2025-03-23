package com.hospitalApi.consults.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.surgery.ports.ForSurgeryCalculationPort;
import com.hospitalApi.surgery.ports.ForSurgeryPort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConsultService implements ForConsultPort {

    private final ConsultRepository consultRepository;
    private final ForPatientPort forPatientPort;
    private final ForSurgeryCalculationPort forSurgeryCalculationService;

    @Override
    public Consult findById(String id) throws NotFoundException {
        return consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
    }

    @Override
    public Consult createConsult(String patientId, Double costoConsulta) throws NotFoundException {
        Patient patient = forPatientPort.getPatient(patientId);
        Consult consult = new Consult(patient, costoConsulta);
        return consultRepository.save(consult);
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
}
