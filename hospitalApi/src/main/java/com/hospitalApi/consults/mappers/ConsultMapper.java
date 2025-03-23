package com.hospitalApi.consults.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.consults.models.Consult;

@Mapper(componentModel = "spring")
public interface ConsultMapper {
    public List<ConsultResponseDTO> fromConsultsToResponse(List<Consult> consults);

    public ConsultResponseDTO fromConsultToResponse(Consult consult);
}