package com.hospitalApi.surgery.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.surgery.models.SurgeryType;

public interface SurgeryTypeRepository extends JpaRepository<SurgeryType, String> {
    public boolean existsByType(String type);

    public boolean existsByTypeAndIdNot(String type, String id);

    public List<SurgeryType> findByTypeContainingIgnoreCase(String type);
}
