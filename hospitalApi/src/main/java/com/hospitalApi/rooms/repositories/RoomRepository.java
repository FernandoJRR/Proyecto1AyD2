package com.hospitalApi.rooms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.rooms.models.Room;

public interface RoomRepository extends JpaRepository<Room, String> {

    public boolean existsByNumber(String number);

    public Optional<Room> findByNumber(String number);

    public boolean existsByNumberAndIdIsNot(String number, String id);

}
