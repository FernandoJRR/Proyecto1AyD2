package com.hospitalApi.rooms.models;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.hospitalApi.rooms.enums.RoomStatus;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@DynamicUpdate
public class Room extends Auditor {

    /**
     * Este es el numero d ela habitacion
     */
    @Column(length = 100, unique = true, nullable = false)
    private String number;
    /*
     * Costo de mantenimiento diario
     */
    @Column(scale = 2, nullable = false)
    private BigDecimal dailyMaintenanceCost;

    /**
     * Precio diario por la ocupacion de esta habitacion
     */
    @Column(scale = 2, nullable = false)
    private BigDecimal dailyPrice;

    /**
     * Los diferentes estatus de la apliacion
     */
    @Column(length = 100, nullable = false)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomHistory> roomHistories;

    public Room(String id) {
        super(id);
    }

    public Room(String number, BigDecimal dailyPrice, BigDecimal dailyMaintenanceCost, RoomStatus status) {
        this.number = number;
        this.dailyPrice = dailyPrice;
        this.dailyMaintenanceCost = dailyMaintenanceCost;
        this.status = status;
    }

    public void toggleAvailability() throws IllegalStateException {
        if (status == RoomStatus.OCCUPIED) {
            throw new IllegalStateException(
                    "La habitación " + number + " está ocupada. No se puede cambiar su estado mientras esté en uso.");
        }
        if (status == RoomStatus.AVAILABLE) {
            status = RoomStatus.OUT_OF_SERVICE;
            return;
        }
        status = RoomStatus.AVAILABLE;
    }

    public RoomStatus markVacant() throws IllegalStateException {
        if (status == RoomStatus.OCCUPIED) {
            status = RoomStatus.AVAILABLE;
            return status;
        } else {
            throw new IllegalStateException(
                    "La habitación " + number + " no está ocupada. No se puede marcar como vacía.");
        }
    }

    public RoomStatus markAvailable() throws IllegalStateException {
        if (status == RoomStatus.OCCUPIED) {
            throw new IllegalStateException(
                    "La habitación " + number + " está ocupada. No se puede cambiar su estado mientras esté en uso.");
        }
        status = RoomStatus.AVAILABLE;
        return status;
    }

    public RoomStatus markOutOfService() throws IllegalStateException {
        if (status == RoomStatus.OCCUPIED) {
            throw new IllegalStateException(
                    "La habitación " + number + " está ocupada. No se puede cambiar su estado mientras esté en uso.");
        }
        status = RoomStatus.OUT_OF_SERVICE;
        return status;
    }

    public RoomStatus markOccupied() throws IllegalStateException {
        if (status == RoomStatus.OUT_OF_SERVICE) {
            throw new IllegalStateException(
                    "La habitación " + number
                            + " está fuera de servicio. No se puede cambiar su estado");
        }
        status = RoomStatus.OCCUPIED;
        return status;
    }

}
