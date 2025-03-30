package com.hospitalApi.rooms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.rooms.models.RoomHistory;

public interface RoomHistoryRepository extends JpaRepository<RoomHistory, String> {

}
