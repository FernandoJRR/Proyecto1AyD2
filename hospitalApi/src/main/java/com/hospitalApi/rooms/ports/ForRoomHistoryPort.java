package com.hospitalApi.rooms.ports;

import com.hospitalApi.patients.models.Patient;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.models.RoomHistory;

public interface ForRoomHistoryPort {
    public RoomHistory saveHistory(Patient patient, Room room);
}
