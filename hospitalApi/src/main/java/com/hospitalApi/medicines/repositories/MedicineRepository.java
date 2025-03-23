package com.hospitalApi.medicines.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.medicines.models.Medicine;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, String> {
    /**
     * Verifica si el nombre del medicamento existe en la bd
     *
     * @param name nombre del medicamento
     * @return boolean
     */
    public boolean existsByName(String name);

    /**
     * Obtiene todos los medicamentos
     *
     * @return List<Medicine>
     */
    public List<Medicine> findAll();

    @Query("SELECT m FROM medicamento m WHERE m.quantity < m.minQuantity")
    List<Medicine> findMedicinesWithLowStock();

    public List<Medicine> findByNameContainingIgnoreCase(String query);

}
