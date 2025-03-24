package com.hospitalApi.rooms.models;

import java.math.BigDecimal;

import com.hospitalApi.consults.models.Consult;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.hospitalApi.shared.models.Auditor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class RoomUsage extends Auditor {

    /**
     * Consulta en la que se utilizó la habitación
     */
    @OneToOne
    @JoinColumn(unique = true)
    private Consult consult;

    /**
     * Habitación que fue utilizada
     */
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Room room;

    /**
     * Días que se usó la habitación
     */
    @Column(nullable = false)
    private int usageDays;

    /**
     * Precio total por el uso de la habitación
     */
    @Column(nullable = false, scale = 2)
    private BigDecimal price;

    public RoomUsage(Consult consult, Room room, int usageDays, BigDecimal price) {
        this.consult = consult;
        this.room = room;
        this.usageDays = usageDays;
        this.price = price;
    }
}
