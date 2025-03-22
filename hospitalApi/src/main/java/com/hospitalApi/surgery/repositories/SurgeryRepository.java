package com.hospitalApi.surgery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.surgery.models.Surgery;

public interface SurgeryRepository extends JpaRepository<Surgery, String> {

}
