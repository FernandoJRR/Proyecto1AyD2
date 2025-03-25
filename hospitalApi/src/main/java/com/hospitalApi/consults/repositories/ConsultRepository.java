package com.hospitalApi.consults.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.consults.models.Consult;


public interface ConsultRepository extends JpaRepository<Consult,String> {
    
}
