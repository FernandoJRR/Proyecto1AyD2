package com.hospitalApi.consults.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalConsultResponseDTO {
    private String consultId;
    private Double totalCost;
}
