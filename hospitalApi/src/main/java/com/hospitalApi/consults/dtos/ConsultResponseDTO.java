package com.hospitalApi.consults.dtos;

import java.time.LocalDate;

import com.hospitalApi.patients.dtos.PatientResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultResponseDTO {
    private String id;
    private PatientResponseDTO patient;
    private Boolean isInternado;
    private Boolean isPaid;
    private Double costoConsulta;
    private Double costoTotal;
    private LocalDate createdAt;
    private LocalDate updateAt;

    private String createdAtString;
    private String updateAtString;
}
