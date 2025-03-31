package com.hospitalApi.rooms.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.rooms.ports.ForRoomHistoryPort;
import com.hospitalApi.rooms.ports.ForRoomPort;
import com.hospitalApi.rooms.ports.ForRoomUsagePort;
import com.hospitalApi.rooms.repositories.RoomUsageRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class RoomUsageService implements ForRoomUsagePort {

    private final ForRoomPort forRoomPort;
    private final ForRoomHistoryPort forRoomHistoryPort;
    private final RoomUsageRepository roomUsageRepository;

    @Override
    public RoomUsage asignRoomToConsult(String roomId, Consult consult)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException {
        Room room = forRoomPort.findRoomById(roomId);
        if (!forRoomPort.roomIsAvailable(roomId)) {
            throw new IllegalStateException("La habitación no está disponible.");
        }
        if (roomUsageRepository.existsByConsultId(consult.getId())) {
            throw new DuplicatedEntryException("La consulta ya tiene una habitación asignada.");
        }
        if (roomUsageRepository.existsByRoomIdAndConsultId(room.getId(), consult.getId())) {
            throw new DuplicatedEntryException("La habitación ya está asignada a la consulta.");
        }
        forRoomPort.markOccupied(roomId);
        RoomUsage roomUsage = new RoomUsage(consult, room, 1, room.getDailyPrice());
        forRoomHistoryPort.saveHistory(consult.getPatient(), room);
        return roomUsageRepository.save(roomUsage);
    }

    @Override
    public RoomUsage closeRoomUsage(Consult consult)
            throws NotFoundException, IllegalStateException {
        if (!roomUsageRepository.existsByConsultId(consult.getId())) {
            throw new IllegalStateException("La consulta no tiene una habitación asignada.");
        }
        RoomUsage roomUsage = roomUsageRepository.findByConsultId(consult.getId());
        Room room = roomUsage.getRoom();
        forRoomPort.markVacant(room.getId());
        // Calculamos los dias de uso mediante la diferencia de fechas
        int daysUsed = (int) ChronoUnit.DAYS.between(roomUsage.getCreatedAt(), LocalDate.now());
        // Si no se ha usado la habitación, asignamos 1 día de uso
        daysUsed = daysUsed + 1;
        if (daysUsed <= 0) {
            throw new IllegalStateException("No se puede cerrar el uso de la habitación sin días de uso.");
        }
        roomUsage.setUsageDays(daysUsed);
        return roomUsageRepository.save(roomUsage);
    }

    public RoomUsage calcRoomUsage(Consult consult) throws IllegalStateException {
        if (!roomUsageRepository.existsByConsultId(consult.getId())) {
            throw new IllegalStateException("La consulta no tiene una habitación asignada.");
        }
        RoomUsage roomUsage = roomUsageRepository.findByConsultId(consult.getId());
        // Calculamos los dias de uso mediante la diferencia de fechas
        int daysUsed = (int) ChronoUnit.DAYS.between(roomUsage.getCreatedAt(), LocalDate.now());
        // Si no se ha usado la habitación, asignamos 1 día de uso
        daysUsed = daysUsed + 1;
        if (daysUsed <= 0) {
            throw new IllegalStateException("No se puede cerrar el uso de la habitación sin días de uso.");
        }
        roomUsage.setUsageDays(daysUsed);
        return roomUsageRepository.save(roomUsage);
    }
}
