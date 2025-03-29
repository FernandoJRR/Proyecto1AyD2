package com.hospitalApi.surgery.dtos;

import com.hospitalApi.surgery.models.SurgeryEmployee;

import lombok.Value;

@Value
public class SurgeryEmpleoyeeResponseDTO {
    private String surgeryId;
    private String employeeId;
    private String employeeName;
    private String employeeLastName;
    private String specialistEmployeeId;
    private Double specialistPayment;

    public SurgeryEmpleoyeeResponseDTO(SurgeryEmployee surgeryEmployee) {
        this.surgeryId = surgeryEmployee.getSurgery().getId();

        this.employeeId = surgeryEmployee.getEmployee() != null
                ? surgeryEmployee.getEmployee().getId()
                : null;

        this.specialistEmployeeId = surgeryEmployee.getSpecialistEmployee() != null
                ? surgeryEmployee.getSpecialistEmployee().getId()
                : null;

        this.specialistPayment = surgeryEmployee.getSpecialistPayment();

        if (surgeryEmployee.getEmployee() != null) {
            this.employeeName = surgeryEmployee.getEmployee().getFirstName();
            this.employeeLastName = surgeryEmployee.getEmployee().getLastName();
        } else if (surgeryEmployee.getSpecialistEmployee() != null) {
            this.employeeName = surgeryEmployee.getSpecialistEmployee().getNombres();
            this.employeeLastName = surgeryEmployee.getSpecialistEmployee().getApellidos();
        } else {
            this.employeeName = null;
            this.employeeLastName = null;
        }
    }
}
