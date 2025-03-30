package com.hospitalApi.surgery.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospitalApi.surgery.models.Surgery;

public interface SurgeryRepository extends JpaRepository<Surgery, String> {

    @Query("SELECT SUM(s.surgeryCost) FROM Surgery s WHERE s.consult.id = :consultId")
    Double sumSurgeryCostByConsultId(@Param("consultId") String consultId);

    List<Surgery> findByConsultId(String consultId);

    boolean existsByPerformedDateIsNotNullAndId(String id);

    @Query("SELECT COUNT(s) = (SELECT COUNT(s2) FROM Surgery s2 WHERE s2.consult.id = :consultId) FROM Surgery s WHERE s.consult.id = :consultId AND s.performedDate IS NOT NULL")
    boolean allSurgeriesPerformedByConsultId(@Param("consultId") String consultId);
}
