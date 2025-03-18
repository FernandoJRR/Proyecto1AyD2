package com.hospitalApi.consult.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.consult.models.Consult;


public interface ConsultRepository extends JpaRepository<Consult,String> {
    
}
