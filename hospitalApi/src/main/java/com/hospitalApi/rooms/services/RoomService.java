package com.hospitalApi.rooms.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.rooms.enums.RoomStatus;
import com.hospitalApi.rooms.models.Room;
import com.hospitalApi.rooms.ports.ForRoomPort;
import com.hospitalApi.rooms.repositories.RoomRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class RoomService implements ForRoomPort {

    private final RoomRepository roomRepository;

    @Override
    public Room createRoom(Room room) throws IllegalStateException {
        // si ya existe una room con el mismo numero entonces retornamos excepcion
        if (roomRepository.existsByNumber(room.getNumber())) {
            throw new IllegalStateException("Ya existe otra habitación con el número " + room.getNumber());
        }
        // por defecto dejamos la room como disponible
        room.setStatus(RoomStatus.AVAILABLE);
        // retornar el guardado
        return roomRepository.save(room);
    }

    @Override
    public Room editRoom(String idUpdatedRoom, Room room) throws DuplicatedEntryException, NotFoundException {
        // mandamos atraer por su id
        Room existingRoom = findRoomById(idUpdatedRoom);
        // si ya existe otra room con el mismo numero y no es el que se esta intentando
        // editar
        if (roomRepository.existsByNumberAndIdIsNot(room.getNumber(), existingRoom.getId())) {
            throw new DuplicatedEntryException("Ya existe otra habitación con el número " + room.getNumber());
        }
        // ahoara podemos hacer el set de la nueva info
        existingRoom.setNumber(room.getNumber());
        existingRoom.setDailyMaintenanceCost(room.getDailyMaintenanceCost());
        existingRoom.setDailyPrice(room.getDailyPrice());

        return roomRepository.save(existingRoom);
    }

    @Override
    public Room toggleRoomAvailability(String roomId) throws NotFoundException {
        // mandamos atraer por su id
        Room existingRoom = findRoomById(roomId);
        // mandamos a hacer el toggle de la availability
        existingRoom.toggleAvailability();
        return roomRepository.save(existingRoom);
    }

    @Override
    public Room findRoomByNumber(String number) throws NotFoundException {
        String errorMessage = String.format("No se encontró una habitación con el número %s", number);
        return roomRepository.findByNumber(number)
                .orElseThrow(() -> new NotFoundException(errorMessage));
    }

    @Override
    public Room findRoomById(String id) throws NotFoundException {
        String errorMessage = String.format("No se encontró una habitación con el ID %s", id);
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(errorMessage));
    }

    @Override
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> findAllRoomsAvailable() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    @Override
    public boolean roomIsAvailable(String roomId) throws NotFoundException {
        Room room = findRoomById(roomId);
        return room.getStatus() == RoomStatus.AVAILABLE;
    }

    @Override
    public Room markVacant(String roomId) throws NotFoundException, IllegalStateException {
        Room room = findRoomById(roomId);
        room.markVacant();
        return roomRepository.save(room);
    }

    @Override
    public Room markAvailable(String roomId) throws NotFoundException, IllegalStateException {
        Room room = findRoomById(roomId);
        room.markAvailable();
        return roomRepository.save(room);
    }

    @Override
    public Room markOccupied(String roomId) throws NotFoundException, IllegalStateException {
        Room room = findRoomById(roomId);
        room.markOccupied();
        return roomRepository.save(room);
    }

    @Override
    public Room markOutOfService(String roomId) throws NotFoundException, IllegalStateException {
        Room room = findRoomById(roomId);
        room.markOutOfService();
        return roomRepository.save(room);
    }
}
