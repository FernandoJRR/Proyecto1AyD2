package com.hospitalApi.rooms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.rooms.models.RoomUsage;

public interface RoomUsageRepository extends JpaRepository<RoomUsage, String> {
    public boolean existsByRoomIdAndConsultId(String roomId, String consultId);

    public boolean existsByConsultId(String consultId);

    public RoomUsage findByConsultId(String consultId);
}