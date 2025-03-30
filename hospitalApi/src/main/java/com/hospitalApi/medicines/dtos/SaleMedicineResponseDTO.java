package com.hospitalApi.medicines.dtos;

import lombok.Value;

@Value
public class SaleMedicineResponseDTO {
    String id;
    MedicineResponseDTO medicine;
    Integer quantity;
    Double price;
    String consultId;
    Double total;
    Double medicineCost;
}
