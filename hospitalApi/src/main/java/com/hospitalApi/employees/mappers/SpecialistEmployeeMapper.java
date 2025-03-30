package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.employees.dtos.SpecialistEmpleoyeeResponseDTO;
import com.hospitalApi.employees.models.SpecialistEmployee;

@Mapper(componentModel = "spring")
public interface SpecialistEmployeeMapper {
        @Mapping(target = "id", source = "specialistEmployee.id")
        public SpecialistEmpleoyeeResponseDTO fromSpecialistEmployeeToSpecialistEmpleoyeeResponseDTO(
                        SpecialistEmployee specialistEmployee);

        public List<SpecialistEmpleoyeeResponseDTO> fromSpecialistEmployeeListToSpecialistEmpleoyeeResponseDTOList(
                        List<SpecialistEmployee> specialistEmployees);
}
