package com.hospitalApi.patients.dtos;

import lombok.Value;

@Value
public class PatientResponseDTO {
    String id;
    String firstnames;
    String lastnames;
    String dpi;
}
