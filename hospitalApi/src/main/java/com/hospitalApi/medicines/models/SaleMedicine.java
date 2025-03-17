package com.hospitalApi.medicines.models;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "sale_medicine")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DynamicUpdate
public class SaleMedicine extends Auditor {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotBlank(message = "La cantidad del medicamento es requerida")
    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad del medicamento no puede ser menor a 1")
    private Integer quantity;

    @NotNull(message = "El precio del medicamento es requerido")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio del medicamento debe ser mayor a 0")
    @Column(nullable = false)
    private Double price;

    /**
     * Inicializa una nueva instancia de la clase SaleMedicine en base a un medicamento y una cantidad.
     * @param medicine
     * @param quantity
     */
    public SaleMedicine(Medicine medicine, Integer quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.price = medicine.getPrice();
    }
}
