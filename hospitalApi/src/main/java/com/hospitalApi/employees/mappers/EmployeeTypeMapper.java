package com.hospitalApi.employees.mappers;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.shared.dtos.IdRequestDTO;

@Mapper(componentModel = "spring")
public interface EmployeeTypeMapper {

    public EmployeeType fromIdRequestDtoTo(IdRequestDTO idRequestDTO);
}
