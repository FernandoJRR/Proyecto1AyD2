package com.hospitalApi.rooms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomStatus {

    // indica que la habitacion ests adisponible para asignarse
    AVAILABLE,
    // indica que la habitacion esta ocupada
    OCCUPIED,
    // indica que esta feura de servicio
    OUT_OF_SERVICE
}
