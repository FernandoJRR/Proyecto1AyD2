package com.hospitalApi.parameters.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.parameters.models.Parameter;

public interface ParameterRepository extends JpaRepository<Parameter, String>{
    public Optional<Parameter> findOneByParameterKey(String parameterKey);
}
