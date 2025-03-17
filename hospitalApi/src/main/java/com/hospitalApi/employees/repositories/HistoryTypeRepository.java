package com.hospitalApi.employees.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.employees.models.HistoryType;

public interface HistoryTypeRepository extends JpaRepository<HistoryType, String>{

}
