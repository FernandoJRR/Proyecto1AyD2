package com.hospitalApi.surgery.dtos;

import lombok.Value;

@Value
public class SurgeryTypeResponseDTO {
    String id;
    String type;
    String description;
    Double specialistPayment;
    Double hospitalCost;
    Double surgeryCost;
}
