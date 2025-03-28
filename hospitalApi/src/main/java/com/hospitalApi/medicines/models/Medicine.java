package com.hospitalApi.medicines.models;

import java.util.List;

import com.hospitalApi.medicines.dtos.CreateMedicineRequestDTO;
import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class Medicine extends Auditor {

    @NotBlank(message = "El nombre del medicamento es requerido")
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @NotBlank(message = "La descripción del medicamento es requerida")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "La cantidad del medicamento es requerida")
    @Min(value = 0, message = "La cantidad del medicamento no puede ser menor a 0")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "La cantidad mínima del medicamento es requerida")
    @Min(value = 0, message = "La cantidad mínima del medicamento no puede ser menor a 0")
    @Column(nullable = false)
    private Integer minQuantity;

    @NotNull(message = "El precio del medicamento es requerido")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio del medicamento debe ser mayor a 0")
    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "medicine", fetch = FetchType.LAZY)
    private List<SaleMedicine> saleMedicines;

    public Medicine(String id, String name, String description, Integer quantity, Integer minQuantity, Double price) {
        super(id);
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.price = price;
    }

    public Medicine(String name, String description, Integer quantity, Integer minQuantity, Double price) {
        super();
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.price = price;
    }

    public Medicine(CreateMedicineRequestDTO createMedicineRequestDTO) {
        this.name = createMedicineRequestDTO.getName();
        this.description = createMedicineRequestDTO.getDescription();
        this.quantity = createMedicineRequestDTO.getQuantity();
        this.minQuantity = createMedicineRequestDTO.getMinQuantity();
        this.price = createMedicineRequestDTO.getPrice();
    }

    public Medicine updateFromDTO(UpdateMedicineRequestDTO dto) {
        if (dto.getName() != null) {
            this.name = dto.getName();
        }
        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }
        if (dto.getQuantity() != null) {
            this.quantity = dto.getQuantity();
        }
        if (dto.getMinQuantity() != null) {
            this.minQuantity = dto.getMinQuantity();
        }
        if (dto.getPrice() != null) {
            this.price = dto.getPrice();
        }
        return this;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + this.getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", minQuantity=" + minQuantity +
                ", price=" + price +
                '}';
    }

}
