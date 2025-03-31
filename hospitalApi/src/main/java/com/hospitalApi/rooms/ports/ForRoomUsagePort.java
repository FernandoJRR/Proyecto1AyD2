package com.hospitalApi.rooms.ports;

import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.rooms.models.RoomUsage;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForRoomUsagePort {
    public RoomUsage asignRoomToConsult(String roomId, Consult consult)
            throws NotFoundException, DuplicatedEntryException, IllegalStateException;

    public RoomUsage closeRoomUsage(Consult consult)
            throws NotFoundException, IllegalStateException;

    public RoomUsage calcRoomUsage(Consult consult)
            throws NotFoundException, IllegalStateException;

}
