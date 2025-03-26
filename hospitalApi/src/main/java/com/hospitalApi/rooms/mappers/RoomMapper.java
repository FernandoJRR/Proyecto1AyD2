package com.hospitalApi.rooms.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.rooms.dtos.RoomResponseDTO;
import com.hospitalApi.rooms.dtos.SaveRoomRequestDTO;
import com.hospitalApi.rooms.models.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    public RoomResponseDTO fromRoomToResponseDto(Room room);

    public List<RoomResponseDTO> fromRoomsToResponseDtos(List<Room> rooms);

    public Room fromSaveRequestDtoToRoom(SaveRoomRequestDTO saveRoomRequestDTO);
}
