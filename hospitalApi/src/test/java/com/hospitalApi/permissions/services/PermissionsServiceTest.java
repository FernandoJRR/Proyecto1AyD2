package com.hospitalApi.permissions.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.permissions.repositories.PermissionRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

public class PermissionsServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission permission;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        permission = new Permission("NAME_MOCK", "ACTION_MOCK");
        permission.setId("qwe-qweq-wer");

    }

    /**
     * dado: que existe un permiso en el repositorio con un nombre específico.
     * cuando: se busca el permiso por su nombre a través del servicio.
     * entonces: el servicio debe devolver el permiso encontrado y verificar que se
     * realizó la consulta en el repositorio.
     */
    @Test
    public void findPermissionByNameShouldReturnExistentPermission() throws NotFoundException {
        // arrange
        when(permissionRepository.findByName(anyString())).thenReturn(Optional.of(permission));

        // act
        Permission result = permissionService.findPermissionByName(permission);

        // assert
        assertEquals(result.getName(), result.getName());
        // se verifica que la consulta se haya hecho con el nombre del permiso
        verify(permissionRepository, times(1)).findByName(permission.getName());

    }

    /**
     * dado: que no existe un permiso con el nombre especificado en el repositorio.
     * cuando: se intenta buscar el permiso por su nombre a través del servicio.
     * entonces: se debe lanzar una excepción `NotFoundException` y verificar que se
     * intentó realizar la consulta en el repositorio.
     */
    @Test
    public void findPermissionByNameShouldThrowNotFoundException() throws NotFoundException {
        // arrange
        when(permissionRepository.findByName(anyString())).thenReturn(Optional.empty());

        // act y assert
        assertThrows(NotFoundException.class, () -> {
            permissionService.findPermissionByName(permission);
        });

        // se verifica que la consulta se haya hecho con el nombre del permiso
        verify(permissionRepository, times(1)).findByName(permission.getName());

    }

    /**
     * dado: que existe un permiso en el repositorio con un ID específico.
     * cuando: se busca el permiso por su ID a través del servicio.
     * entonces: el servicio debe devolver el permiso encontrado y verificar que se
     * realizó la consulta en el repositorio.
     */
    @Test
    public void findPermissionByIdShouldReturnExistentPermission() throws NotFoundException {
        // arrange
        when(permissionRepository.findById(anyString())).thenReturn(Optional.of(permission));

        // act
        Permission result = permissionService.findPermissionById(permission);

        // assert
        assertEquals(result.getName(), result.getName());
        // se verifica que la consulta se haya hecho con el id del permiso
        verify(permissionRepository, times(1)).findById(permission.getId());

    }

    /**
     * dado: que no existe un permiso con el ID especificado en el repositorio.
     * cuando: se intenta buscar el permiso por su ID a través del servicio.
     * entonces: se debe lanzar una excepción `NotFoundException` y verificar que se
     * intentó realizar la consulta en el repositorio.
     */
    @Test
    public void findPermissionByIdhouldThrowNotFoundException() throws NotFoundException {
        // arrange
        when(permissionRepository.findById(anyString())).thenReturn(Optional.empty());

        // act y assert
        assertThrows(NotFoundException.class, () -> {
            permissionService.findPermissionById(permission);
        });

        // se verifica que la consulta se haya hecho con el id del permiso
        verify(permissionRepository, times(1)).findById(permission.getId());

    }
}
