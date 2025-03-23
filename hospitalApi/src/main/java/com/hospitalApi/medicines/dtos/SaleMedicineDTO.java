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
    private String id;
    private MedicineResponseDTO medicine;
    private Integer quantity;
    private Double price;
    private String consultId;
    private Double total;

    public SaleMedicineDTO(SaleMedicine saleMedicine) {
        this.id = saleMedicine.getId();
        this.medicine = new MedicineResponseDTO(saleMedicine.getMedicine());
        this.quantity = saleMedicine.getQuantity();
        this.price = saleMedicine.getPrice();
        this.consultId = null;
        this.total = this.price * this.quantity;
    }
}
