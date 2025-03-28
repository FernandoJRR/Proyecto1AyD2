package com.hospitalApi.consults.dtos;

import java.time.LocalDate;

import com.hospitalApi.consults.models.Consult;
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
    private String id;
    private PatientResponseDTO patient;
    private Boolean isInternado;
    private Boolean isPaid;
    private Double costoConsulta;
    private Double costoTotal;
    private LocalDate createdAt;
    private LocalDate updateAt;

    public ConsultResponseDTO(Consult consult) {
        this.id = consult.getId();
        this.patient = new PatientResponseDTO(consult.getPatient());
        this.isPaid = consult.getIsPaid();
        this.isInternado = consult.getIsInternado();
        this.costoConsulta = consult.getCostoConsulta();
        this.costoTotal = consult.getCostoTotal();
        this.createdAt = consult.getCreatedAt();
        this.updateAt = consult.getUpdateAt();
    }
}
