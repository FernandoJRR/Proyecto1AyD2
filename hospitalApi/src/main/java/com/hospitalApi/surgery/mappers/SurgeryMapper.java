package com.hospitalApi.surgery.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.surgery.dtos.SurgeryResponseDTO;
import com.hospitalApi.surgery.models.Surgery;

@Mapper(componentModel = "spring", uses = {SurgeryEmployeeMapper.class, SurgeryTypeMapper.class})
public interface SurgeryMapper {
    
    @Mapping(target = "consultId", source = "surgery.consult.id")
    public SurgeryResponseDTO fromSurgeryToSurgeryResponseDTO(Surgery surgery);
    public List<SurgeryResponseDTO> fromSurgeryListToSurgeryResponseDTOList(List<Surgery> surgeries);
}
