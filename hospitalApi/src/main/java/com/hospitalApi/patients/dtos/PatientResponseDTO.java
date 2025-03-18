package com.hospitalApi.patients.dtos;

import com.hospitalApi.patients.models.Patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {
    String id;
    String firstnames;
    String lastnames;
    String dpi;

    public PatientResponseDTO(Patient patient) {
        this.id = patient.getId();
        this.firstnames = patient.getFirstnames();
        this.lastnames = patient.getLastnames();
        this.dpi = patient.getDpi();
    }
}
