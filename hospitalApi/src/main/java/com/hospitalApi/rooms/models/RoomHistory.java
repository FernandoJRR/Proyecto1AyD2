package com.hospitalApi.rooms.models;

import java.time.LocalDate;

import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.shared.models.Auditor;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class RoomHistory extends Auditor {

    /**
     * Fecha de en la que se hospedo un paciente
     */
    private LocalDate date;

    /**
     * LA habitacion d ehospedaje
     */
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Room room;

    /**
     * El paciente que se hospedo
     */
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Patient patient;

    public RoomHistory(LocalDate date, Room room, Patient patient) {
        this.date = date;
        this.room = room;
        this.patient = patient;
    }

    public RoomHistory(String id) {
        super(id);
    }

}
