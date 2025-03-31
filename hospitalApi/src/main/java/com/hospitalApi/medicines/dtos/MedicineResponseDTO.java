package com.hospitalApi.medicines.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponseDTO {
    private String id;
    private String name;
    private String description;
    private Integer quantity;
    private Integer minQuantity;
    private BigDecimal price;
    private BigDecimal cost;
}
