package com.hospitalApi.consults.dtos;

import lombok.Data;

@Data
public class ConsutlFilterDTO {
    private String patientId;
    private String patientDpi;
    private String patientFirstnames;
    private String patientLastnames;
    private String employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String consultId;
    private Boolean isPaid;
    private Boolean isInternado;
}
