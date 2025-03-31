package com.hospitalApi.medicines.models;

import java.math.BigDecimal;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DynamicUpdate
public class SaleMedicine extends Auditor {

    @ManyToOne
    @JoinColumn(name = "consult_id")
    private Consult consult;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    /**
     * Empleaod que realizio la venta
     */
    @ManyToOne
    private Employee employee;

    @Column(nullable = false)
    private Integer quantity;

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
