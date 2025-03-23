package com.hospitalApi.surgery.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.surgery.dtos.SurgeryTypeResponseDTO;
import com.hospitalApi.surgery.models.SurgeryType;

@Mapper(componentModel = "spring")
public interface SurgeryTypeMapper {
    public List<SurgeryTypeResponseDTO> fromSurgeryTypeListToSurgeryTypeResponseDTOList(List<SurgeryType> surgeryTypes);
}
