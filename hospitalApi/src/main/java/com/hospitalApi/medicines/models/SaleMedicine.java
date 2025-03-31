package com.hospitalApi.medicines.models;

import java.math.BigDecimal;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DynamicUpdate
public class SaleMedicine extends Auditor {

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "consult_id", nullable = true)
    private Consult consult;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotNull(message = "La cantidad del medicamento es requerida")
    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad del medicamento no puede ser menor a 1")
    private Integer quantity;

    @NotNull(message = "El precio del medicamento es requerido")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio del medicamento debe ser mayor a 0")
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal medicineCost;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private BigDecimal profit;

    /**
     * Inicializa una nueva instancia de la clase SaleMedicine en base a un
     * medicamento y una cantidad.
     * 
     * @param medicine
     * @param quantity
     */
    public SaleMedicine(Medicine medicine, Integer quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.price = medicine.getPrice();
        this.medicineCost = medicine.getCost();
        calculateTotal();
        calculateProfit();
    }

    /**
     * Inicializa una nueva instancia de la clase SaleMedicine en base a una
     * consulta, un medicamento y una cantidad.
     * 
     * @param consult
     * @param medicine
     * @param quantity
     */
    public SaleMedicine(Consult consult, Medicine medicine, Integer quantity) {
        this.consult = consult;
        this.medicine = medicine;
        this.quantity = quantity;
        this.price = medicine.getPrice();
        this.medicineCost = medicine.getCost();
        calculateTotal();
        calculateProfit();
    }

    /**
     * Calcula el total multiplicando el precio por la cantidad de articulos
     * vendidos
     */
    private void calculateTotal() {
        this.total = this.price.multiply(BigDecimal.valueOf(this.quantity));
    }

    /**
     * Calcula la ganacia restando al total el costo total por vender esos articos
     */
    private void calculateProfit() {
        BigDecimal totalCost = this.medicineCost.multiply(BigDecimal.valueOf(this.quantity));
        this.profit = this.total.subtract(totalCost);
    }
}
