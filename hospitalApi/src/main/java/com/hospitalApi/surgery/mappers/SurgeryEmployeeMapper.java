package com.hospitalApi.surgery.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.surgery.dtos.SurgeryEmpleoyeeResponseDTO;
import com.hospitalApi.surgery.models.SurgeryEmployee;

@Mapper(componentModel = "spring")
public interface SurgeryEmployeeMapper {
    public List<SurgeryEmpleoyeeResponseDTO> fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(
            List<SurgeryEmployee> surgeryEmployees);
}
