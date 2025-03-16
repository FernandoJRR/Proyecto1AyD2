package com.hospitalApi.medicines.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.medicines.models.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

}
