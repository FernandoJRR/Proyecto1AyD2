package com.hospitalApi.patients.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.patients.dtos.PatientResponseDTO;
import com.hospitalApi.patients.models.Patient;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    public PatientResponseDTO toPatientResponseDTO(Patient patient);
    public List<PatientResponseDTO> fromPatientListToPatientResponseDTOList(List<Patient> patients);
}
