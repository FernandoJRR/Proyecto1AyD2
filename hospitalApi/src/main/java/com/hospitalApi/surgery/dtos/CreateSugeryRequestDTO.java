package com.hospitalApi.surgery.dtos;

import lombok.Data;

@Data
public class CreateSugeryRequestDTO {
    private String consultId;
    private String surgeryTypeId;
}
