package com.hospitalApi.surgery.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospitalApi.surgery.models.Surgery;

public interface SurgeryRepository extends JpaRepository<Surgery, String> {

    @Query("SELECT SUM(s.surgeryCost) FROM Surgery s WHERE s.consult.id = :consultId")
    Double sumSurgeryCostByConsultId(@Param("consultId") String consultId);

}
