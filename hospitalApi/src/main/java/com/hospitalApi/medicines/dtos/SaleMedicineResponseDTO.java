package com.hospitalApi.medicines.dtos;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class SaleMedicineResponseDTO {
        String id;
        MedicineResponseDTO medicine;
        String consultId;
        Integer quantity;
        BigDecimal price;
        BigDecimal total;
        BigDecimal medicineCost;
        BigDecimal profit;
}
