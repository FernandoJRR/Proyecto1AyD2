package com.hospitalApi.surgery.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteSurgeryResponseDTO {
    private String surgeryId;
    private String message;
    private Boolean isDeleted;
}
