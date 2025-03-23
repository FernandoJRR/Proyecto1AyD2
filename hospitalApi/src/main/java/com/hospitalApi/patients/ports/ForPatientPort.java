package com.hospitalApi.patients.ports;

import java.util.List;

import com.hospitalApi.patients.dtos.UpdatePatientRequestDTO;
import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForPatientPort {

    public Patient createPatient(Patient patient) throws DuplicatedEntryException;

    public Patient updatePatient(String id, UpdatePatientRequestDTO updatePatientRequestDTO)
            throws DuplicatedEntryException, NotFoundException;

    public Patient getPatient(String id) throws NotFoundException;

    public Patient getPatientByDpi(String dpi) throws NotFoundException;

    public List<Patient> getPatients(String query);
}