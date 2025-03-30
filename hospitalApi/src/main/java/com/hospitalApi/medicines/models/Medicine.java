package com.hospitalApi.medicines.models;

import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@DynamicUpdate
public class Medicine extends Auditor {

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer minQuantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double cost;

    @OneToMany(mappedBy = "medicine")
    private List<SaleMedicine> saleMedicines;

    public Medicine(String id, String name, String description, Integer quantity, Integer minQuantity, Double price,
            Double cost) {
        super(id);
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.price = price;
        this.cost = cost;
    }

    public Medicine(String name, String description, Integer quantity, Integer minQuantity, Double price,
            Double cost) {
        super();
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.price = price;
        this.cost = cost;
    }

    public Medicine(CreateMedicineRequestDTO createMedicineRequestDTO) {
        this.name = createMedicineRequestDTO.getName();
        this.description = createMedicineRequestDTO.getDescription();
        this.quantity = createMedicineRequestDTO.getQuantity();
        this.minQuantity = createMedicineRequestDTO.getMinQuantity();
        this.price = createMedicineRequestDTO.getPrice();
        this.cost = createMedicineRequestDTO.getCost();
    }

    public Medicine updateFromDTO(UpdateMedicineRequestDTO dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.quantity = dto.getQuantity();
        this.minQuantity = dto.getMinQuantity();
        this.price = dto.getPrice();
        this.cost = dto.getCost();

        return this;
    }

}
