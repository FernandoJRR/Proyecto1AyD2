package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.dtos.SaveEmployeeTypeRequestDTO;
import com.hospitalApi.employees.dtos.EmployeeTypeResponseDTO;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.shared.dtos.IdRequestDTO;

@Mapper(componentModel = "spring")
public interface EmployeeTypeMapper {

    public EmployeeType fromIdRequestDtoToEmployeeType(IdRequestDTO idRequestDTO);

    public EmployeeType fromCreateEmployeeTypeDtoToEmployeeType(SaveEmployeeTypeRequestDTO dto);

    public EmployeeTypeResponseDTO fromEmployeeTypeToEmployeeTypeResponseDto(EmployeeType employeeType);

    public List<EmployeeTypeResponseDTO> fromEmployeeTypeListToEmployeeTypeResponseDtoList(List<EmployeeType> types);
}
