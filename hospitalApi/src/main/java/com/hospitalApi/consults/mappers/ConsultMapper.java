package com.hospitalApi.consults.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.shared.utils.DateFormatterUtil;

@Mapper(componentModel = "spring", uses = { DateFormatterUtil.class })
public interface ConsultMapper {
    public List<ConsultResponseDTO> fromConsultsToResponse(List<Consult> consults);

    @Mapping(target = "createdAtString", source = "createdAt", qualifiedByName = "formatDateToLocalFormat")
    @Mapping(target = "updateAtString", source = "updateAt", qualifiedByName = "formatDateToLocalFormat")
    public ConsultResponseDTO fromConsultToResponse(Consult consult);
}