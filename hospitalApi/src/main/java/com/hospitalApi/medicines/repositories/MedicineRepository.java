package com.hospitalApi.medicines.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.medicines.models.Medicine;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
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
}
