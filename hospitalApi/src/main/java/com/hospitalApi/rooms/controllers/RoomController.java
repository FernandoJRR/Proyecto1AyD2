package com.hospitalApi.rooms.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.rooms.dtos.RoomResponseDTO;
import com.hospitalApi.rooms.dtos.SaveRoomRequestDTO;
import com.hospitalApi.rooms.mappers.RoomMapper;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.ports.ForRoomPort;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final ForRoomPort forRoomPort;
    private final RoomMapper roomMapper;

    @Operation(summary = "Creación de nuevas habitaciones en el sistema.", description = "Este endpoint permite la creación de nuevas habitaciones, validando que no exista otra con el mismo número de habitación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Habitación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una habitación con el mismo número")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CREATE_ROOM')")
    public RoomResponseDTO createRoom(
            @RequestBody @Valid SaveRoomRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        // mapear el request dto a nuestro modelo
        Room roomToCreate = roomMapper.fromSaveRequestDtoToRoom(request);
        // mandamos a crear el room
        Room createdRoom = forRoomPort.createRoom(roomToCreate);
        // mapear el room creado al response
        return roomMapper.fromRoomToResponseDto(createdRoom);
    }

    @Operation(summary = "Actualización de una habitación existente.", description = "Este endpoint permite actualizar los datos de una habitación existente. Valida que no exista otra habitación con el mismo número.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habitación actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe otra habitación con el mismo número"),
    })
    @PatchMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('EDIT_ROOM')")
    public RoomResponseDTO updateRoom(
            @PathVariable String roomId,
            @RequestBody @Valid SaveRoomRequestDTO request) throws DuplicatedEntryException, NotFoundException {
        // mapear el request dto a nuestro modelo
        Room roomToUpdate = roomMapper.fromSaveRequestDtoToRoom(request);
        // mandamos a editar el room
        Room updatedRoom = forRoomPort.editRoom(roomId, roomToUpdate);
        // mapear el room editado al response
        return roomMapper.fromRoomToResponseDto(updatedRoom);
    }

    @Operation(summary = "Alternar la disponibilidad de una habitación.", description = "Este endpoint cambia el estado de una habitación entre AVAILABLE y OUT_OF_SERVICE. "
            + "No se permite el cambio si la habitación está OCCUPIED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la habitación actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Habitación no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede cambiar el estado de una habitación ocupada")
    })
    @PatchMapping("/{roomId}/toggle-availability")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('TOGGLE_ROOM_AVAILABILITY')")
    public RoomResponseDTO toggleRoomAvailability(
            @PathVariable String roomId) throws DuplicatedEntryException, NotFoundException {
        // mandamos a editar el room
        Room updatedRoom = forRoomPort.toggleRoomAvailability(roomId);
        // mapear el room editado al response
        return roomMapper.fromRoomToResponseDto(updatedRoom);
    }

    @Operation(summary = "Obtener habitación por su numero.", description = "Este endpoint permite obtener los datos de una habitación específica utilizando su numero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habitación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la habitación con el numero proporcionado")
    })
    @GetMapping("/{roomNumber}/by-number")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('EDIT_ROOM', 'TOGGLE_ROOM_AVAILABILITY', 'CREATE_ROOM')")
    public RoomResponseDTO getRoomByNumber(
            @PathVariable String roomNumber) throws NotFoundException {
        // mandmaos a traer la room por su number
        Room findedRoom = forRoomPort.findRoomByNumber(roomNumber);
        // mapear el room encontrado al response
        return roomMapper.fromRoomToResponseDto(findedRoom);
    }

    @Operation(summary = "Obtener habitación por ID.", description = "Este endpoint permite obtener los datos de una habitación específica utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habitación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la habitación con el ID proporcionado")
    })
    @GetMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('EDIT_ROOM', 'TOGGLE_ROOM_AVAILABILITY', 'CREATE_ROOM')")
    public RoomResponseDTO getRoomById(
            @PathVariable String roomId) throws NotFoundException {
        // mandmaos a traer la room por su id
        Room findedRoom = forRoomPort.findRoomById(roomId);
        // mapear el room encontrado al response
        return roomMapper.fromRoomToResponseDto(findedRoom);
    }

    @Operation(summary = "Obtener todas las habitaciones.", description = "Este endpoint permite obtener la lista de todas las habitaciones registradas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de habitaciones obtenido exitosamente"),
    })
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('EDIT_ROOM', 'TOGGLE_ROOM_AVAILABILITY', 'CREATE_ROOM')")
    public List<RoomResponseDTO> getAllRooms() {
        // mandmaos a traer todas las rooms
        List<Room> findedRooms = forRoomPort.findAllRooms();
        // mapear el room creado al response
        return roomMapper.fromRoomsToResponseDtos(findedRooms);
    }
}
