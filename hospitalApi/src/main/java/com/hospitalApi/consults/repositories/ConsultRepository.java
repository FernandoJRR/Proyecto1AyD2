package com.hospitalApi.consults.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hospitalApi.consults.models.Consult;

public interface ConsultRepository extends JpaRepository<Consult, String>, JpaSpecificationExecutor<Consult> {

    /**
     * Obtiene todas las consultas pagadas dentro del rango de fechas indicado.
     * Si una fecha es nula, no se aplica ese límite.
     *
     * @param startDate fecha de inicio del rango, puede ser nula.
     * @param endDate   fecha de fin del rangoF, puede ser nula.
     * @return lista de consultas pagadas en el rango de fechas.
     */
    @Query("""
            SELECT c FROM Consult c
            WHERE (:startDate IS NULL OR c.createdAt >= :startDate)
              AND (:endDate IS NULL OR c.createdAt <= :endDate)
              AND c.isPaid = true
            """)
    public List<Consult> findPaidConsultsByCreatedAtBetween(
            LocalDate startDate, LocalDate endDate);
}
