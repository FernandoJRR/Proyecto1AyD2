package com.hospitalApi.permissions.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.shared.dtos.IdRequestDTO;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    /**
     * Crea una lista de objetos Permission inicializando solamente sus ids.
     * 
     * @param dto
     * @return
     */
    public List<Permission> fromIdsRequestDtoToPermissions(List<IdRequestDTO> dto);

}
