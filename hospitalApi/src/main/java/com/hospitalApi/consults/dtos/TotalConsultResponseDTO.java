package com.hospitalApi.consults.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalConsultResponseDTO {
    private String consultId;
    private BigDecimal totalCost;
}
