package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.dtos.HistoryTypeResponseDTO;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.shared.dtos.IdRequestDTO;

@Mapper(componentModel = "spring")
public interface HistoryTypeMapper {
    HistoryTypeResponseDTO fromHistoryTypeToHistoryTypeResponseDTO(HistoryType historyType);
    HistoryType fromHistoryTypeDtoToHistoryType(HistoryTypeResponseDTO historyTypeResponseDTO);
    HistoryType fromIdRequestDtoToHistoryType(IdRequestDTO idRequestDTO);
}
