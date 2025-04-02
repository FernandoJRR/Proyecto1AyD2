package com.hospitalApi.consults.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsultDeletedResponseDTO {
    private String message;
    private String consultId;
    private boolean deleted;
}
