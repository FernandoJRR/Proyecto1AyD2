package com.hospitalApi.rooms.models;

import java.math.BigDecimal;

import com.hospitalApi.consults.models.Consult;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.hospitalApi.shared.models.Auditor;

/**
 * Representa el uso de una habitación dentro del hospital,
 * asociado a una consulta médica específica.
 * Registra la cantidad de días de uso, el precio diario y el costo de
 * mantenimiento al que la habitacion se dio.
 *
 * @param consult                  consulta médica asociada al uso de la
 *                                 habitación.
 * @param room                     habitación utilizada.
 * @param usageDays                cantidad de días de uso de la habitación.
 * @param dailyRoomPrice           precio diario de uso de la habitación.
 * @param dailyRoomMaintenanceCost costo diario de mantenimiento de la
 *                                 habitación.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class RoomUsage extends Auditor {

    @OneToOne
    @JoinColumn(unique = true)
    private Consult consult;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Room room;

    @Column(nullable = false)
    private int usageDays;

    @Column(nullable = false, scale = 2)
    private BigDecimal dailyRoomPrice;

    @Column(nullable = false, scale = 2)
    private BigDecimal dailyRoomMaintenanceCost;

    public RoomUsage(Consult consult, Room room, int usageDays, BigDecimal dailyRoomPrice,
            BigDecimal dailyRoomMaintenanceCost) {
        this.consult = consult;
        this.room = room;
        this.usageDays = usageDays;
        this.dailyRoomPrice = dailyRoomPrice;
        this.dailyRoomMaintenanceCost = dailyRoomMaintenanceCost;
    }
}
