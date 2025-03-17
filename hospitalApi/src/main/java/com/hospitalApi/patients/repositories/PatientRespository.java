package com.hospitalApi.patients.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.patients.models.Patient;

public interface PatientRespository extends JpaRepository<Patient, Long> {
    
}
