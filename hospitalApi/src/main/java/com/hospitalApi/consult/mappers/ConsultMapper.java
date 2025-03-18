package com.hospitalApi.consult.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.consult.dtos.ConsultResponseDTO;
import com.hospitalApi.consult.models.Consult;

@Mapper(componentModel = "spring")
public interface ConsultMapper {
    public List<ConsultResponseDTO> fromConsultsToResponse(List<Consult> consults);

    public ConsultResponseDTO fromConsultToResponse(Consult consult);
}