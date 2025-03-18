package com.hospitalApi.employees.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HistoryTypeEnum {
    CONTRATACION ("Contratacion"),
    DESPIDO ("Despido"),
    RECONTRATACION ("Recontratacion"),
    AUMENTO_SALARIAL ("Aumento Salarial"),
    DISMINUCION_SALARIAL ("Disminucion Salarial");

    private final String type;
}
