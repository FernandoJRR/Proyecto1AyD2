package com.hospitalApi.consult.models;

import org.apache.commons.lang3.DoubleRange;

import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity(name = "consulta")
public class Consult extends Auditor{
    
    private Patient patient;
    private Boolean isInternado;
    private Double costoConsulta;
    private Double constoTotal;
    //Habitacion
    // private Habitacion habitacion;


}
