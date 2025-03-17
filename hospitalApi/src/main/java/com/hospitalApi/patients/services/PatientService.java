package com.hospitalApi.patients.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.patients.ports.ForPatientPort;
import com.hospitalApi.patients.repositories.PatientRespository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService implements ForPatientPort {
    private final PatientRespository patientRespository;
}
