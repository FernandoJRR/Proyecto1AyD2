package com.hospitalApi.medicines.dtos;

import com.hospitalApi.medicines.models.Medicine;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineResponseDTO {
    String id;
    String name;
    String description;
    Integer quantity;
    Integer minQuantity;
    Double price;

    public MedicineResponseDTO(Medicine medicine) {
        this.id = medicine.getId();
        this.name = medicine.getName();
        this.description = medicine.getDescription();
        this.quantity = medicine.getQuantity();
        this.minQuantity = medicine.getMinQuantity();
        this.price = medicine.getPrice();
    }
}
