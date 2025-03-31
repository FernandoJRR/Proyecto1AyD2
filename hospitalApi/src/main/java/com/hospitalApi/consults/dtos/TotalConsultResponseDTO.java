package com.hospitalApi.consults.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalConsultResponseDTO {
    private String consultId;
    private Double totalCost;
}
