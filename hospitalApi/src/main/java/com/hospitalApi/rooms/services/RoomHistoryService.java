package com.hospitalApi.rooms.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomHistory;
import com.hospitalApi.rooms.ports.ForRoomHistoryPort;
import com.hospitalApi.rooms.repositories.RoomHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class RoomHistoryService implements ForRoomHistoryPort {

    private final RoomHistoryRepository roomHistoryRepository;

    @Override
    public RoomHistory saveHistory(Patient patient, Room room) {
        RoomHistory roomHistory = new RoomHistory(
                LocalDate.now(), room, patient);
        return roomHistoryRepository.save(roomHistory);
    }

}
