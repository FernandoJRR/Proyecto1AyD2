package com.hospitalApi.consults.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.repositories.ConsultRepository;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConsultService implements ForConsultPort {

    private final ConsultRepository consultRepository;
    private final ForPatientPort forPatientPort;

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
    public Double obtenerTotalConsulta(String id) throws NotFoundException {
        Consult consult = consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
        Double total = consult.getCostoTotal();
        return total;
    }

    @Override
    public Consult pagarConsulta(String id) throws NotFoundException {
        Consult consult = consultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta con id " + id + " no encontrada"));
        consult.setIsPaid(true);
        return consultRepository.save(consult);
    }
}
