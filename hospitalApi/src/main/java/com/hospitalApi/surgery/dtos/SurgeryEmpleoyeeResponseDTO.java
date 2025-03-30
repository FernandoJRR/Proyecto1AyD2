package com.hospitalApi.surgery.dtos;

import lombok.Value;

@Value
public class SurgeryEmpleoyeeResponseDTO {
    String surgeryId;
    String employeeId;
    String employeeName;
    String employeeLastName;
    String specialistEmployeeId;
    Double specialistPayment;
}
