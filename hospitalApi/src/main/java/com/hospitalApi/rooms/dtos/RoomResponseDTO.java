package com.hospitalApi.rooms.dtos;

import java.math.BigDecimal;

import com.hospitalApi.rooms.enums.RoomStatus;

import lombok.Value;

@Value
public class RoomResponseDTO {

    String id;

    String number;

    BigDecimal dailyMaintenanceCost;

    BigDecimal dailyPrice;

    RoomStatus status;
}
