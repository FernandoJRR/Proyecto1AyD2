package com.hospitalApi.permissions.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.permissions.dtos.PermissionResponseDTO;
import com.hospitalApi.permissions.mappers.PermissionMapper;
import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.permissions.ports.ForPermissionsPort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final ForPermissionsPort forPermissionsPort;
    private final PermissionMapper permissionMapper;

    @Operation(summary = "Obtener todos los permisos", description = "Este endpoint permite la obtención de todos los permisos registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permisos obtenidos exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PermissionResponseDTO> findEmployees() {
        // mandamos a traer todos los permisos
        List<Permission> result = forPermissionsPort.findAllPemrissions();
        // convertir el la lista a lista de dtos
        List<PermissionResponseDTO> response = permissionMapper.fromPermissionsToPermissionsReponseDtos(result);
        return response;
    }
}
