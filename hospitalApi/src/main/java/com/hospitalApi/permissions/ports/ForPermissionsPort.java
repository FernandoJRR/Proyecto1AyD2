package com.hospitalApi.permissions.ports;

import java.util.List;

import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForPermissionsPort {

    public Permission createPermission(Permission permissionToCreate) throws DuplicatedEntryException;

    /**
     * Busca todos los permisos en base al id de los permisos enviados
     * (estos deben estar preferiblemente inicializados solo con el id).
     * 
     * @param permissions permisos con solo el id inicializado
     * @return permisos encontrados con todos sus parametros inicializados
     * @throws NotFoundException
     */
    public List<Permission> findAllById(List<Permission> permissions) throws NotFoundException;

    public Permission findPermissionByName(Permission permission) throws NotFoundException;

    public Permission findPermissionById(Permission permission) throws NotFoundException;

    /**
     * Recupera todos los permisos registrados en el sistema.
     *
     * @return una lista de Permission con todos los permisos existentes.
     */
    public List<Permission> findAllPemrissions();

}
