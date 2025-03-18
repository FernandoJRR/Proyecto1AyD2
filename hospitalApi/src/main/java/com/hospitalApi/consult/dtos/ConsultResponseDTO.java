package com.hospitalApi.consult.dtos;

import java.time.LocalDate;

import com.hospitalApi.consult.models.Consult;
import com.hospitalApi.patients.dtos.PatientResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultResponseDTO {
    String id;
    PatientResponseDTO patient;
    Boolean isInternado;
    Double costoConsulta;
    Double costoTotal;
    LocalDate createdAt;
    LocalDate updateAt;

    public ConsultResponseDTO(Consult consult) {
        this.id = consult.getId();
        this.patient = new PatientResponseDTO(consult.getPatient());
        this.isInternado = consult.getIsInternado();
        this.costoConsulta = consult.getCostoConsulta();
        this.costoTotal = consult.getCostoTotal();
        this.createdAt = consult.getCreatedAt();
        this.updateAt = consult.getUpdateAt();
    }
}
