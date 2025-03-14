package com.hospitalApi.medicines.models;

import com.hospitalApi.medicines.dtos.UpdateMedicineRequestDTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "medicamento")
@EqualsAndHashCode(of = "id")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Min(value = 1, message = "El precio del medicamento no puede ser menor a Q1.00")
    @Column(nullable = false)
    private Double price;

    public void updateFromDTO(UpdateMedicineRequestDTO dto) {
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
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", minQuantity=" + minQuantity +
                ", price=" + price +
                '}';
    }

}
