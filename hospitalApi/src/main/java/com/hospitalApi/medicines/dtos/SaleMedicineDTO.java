package com.hospitalApi.medicines.dtos;

import com.hospitalApi.medicines.models.SaleMedicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleMedicineDTO {
    MedicineResponseDTO medicine;
    Integer quantity;
    Double price;
    String consultId;

    public SaleMedicineDTO(SaleMedicine saleMedicine) {
        this.medicine = new MedicineResponseDTO(saleMedicine.getMedicine());
        this.quantity = saleMedicine.getQuantity();
        this.price = saleMedicine.getPrice();
        this.consultId = null;
        // this.consultId = saleMedicine.getConsultId();
    }
}
