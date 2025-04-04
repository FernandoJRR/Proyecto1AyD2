package com.hospitalApi.patients.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.patients.dtos.UpdatePatientRequestDTO;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.patients.repositories.PatientRespository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService implements ForPatientPort {
    private final PatientRespository patientRespository;

    @Override
    public Patient createPatient(Patient patient) throws DuplicatedEntryException {
        if (patientRespository.existsByDpi(patient.getDpi())) {
            throw new DuplicatedEntryException("El DPI " + patient.getDpi() + " ya existe");
        }
        return patientRespository.save(patient);
    }

    @Override
    public Patient getPatient(String id) throws NotFoundException {
        return patientRespository.findById(id)
                .orElseThrow(() -> new NotFoundException("El paciente con id " + id + " no existe"));
    }

    @Override
    public Patient getPatientByDpi(String dpi) throws NotFoundException {
        if (patientRespository.existsByDpi(dpi)) {
            return patientRespository.findByDpi(dpi);
        } else {
            throw new NotFoundException("El paciente con DPI " + dpi + " no existe");
        }
    }

    @Override
    public Patient updatePatient(String id, UpdatePatientRequestDTO updatePatientRequestDTO)
            throws DuplicatedEntryException, NotFoundException {
        Patient patient = patientRespository.findById(id)
                .orElseThrow(() -> new NotFoundException("El paciente con id " + id + " no existe"));
        // Verificamos si el DPI es el mismo
        if (patientRespository.existsByDpiAndIdNot(updatePatientRequestDTO.getDpi(), id)) {
            throw new DuplicatedEntryException("El DPI " + updatePatientRequestDTO.getDpi() + " ya existe");
        }
        patient.updateFromDTO(updatePatientRequestDTO);
        return patientRespository.save(patient);
    }

    @Override
    public List<Patient> getPatients(String query) {
        if (query != null) {
            return patientRespository.findByFirstnamesContainingIgnoreCaseOrLastnamesContainingIgnoreCase(query, query);
        }
        return patientRespository.findAllByOrderByCreatedAtDesc();
    }
}
