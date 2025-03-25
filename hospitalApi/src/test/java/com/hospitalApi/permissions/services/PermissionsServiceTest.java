package com.hospitalApi.permissions.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.permissions.repositories.PermissionRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class PermissionsServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission permission;

    @BeforeEach
    public void setUp() {
        permission = new Permission("NAME_MOCK", "ACTION_MOCK");
        permission.setId("qwe-qweq-wer");

    }

    /**
     * dado: que el permiso no existe en la base de datos.
     * cuando: se intenta crear un nuevo permiso.
     * entonces: el permiso debe guardarse correctamente en la base de datos.
     */
    @Test
    public void shouldCreatePermissionSuccessfully() throws DuplicatedEntryException {
        // ARRANGE
        when(permissionRepository.existsByName(anyString())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        // ACT
        Permission result = permissionService.createPermission(permission);

        // ASSERT
        assertAll(
                // el retorno no debe ser nulo
                () -> assertNotNull(result),
                // la salida debe ser igual a la entrada een su nombre
                () -> assertEquals(permission.getName(), result.getName()));
        // estos metodos solo deben instanciarse una sola vez
        verify(permissionRepository, times(1)).existsByName(permission.getName());
        verify(permissionRepository, times(1)).save(permission);
    }

    /**
     * dado: que un permiso con el mismo nombre ya existe en la base de datos.
     * cuando: se intenta crear un nuevo permiso con ese nombre.
     * entonces: se lanza una excepción DuplicatedEntryException y no se guarda el
     * permiso.
     */
    @Test
    public void shouldThrowDuplicatedEntryExceptionWhenPermissionAlreadyExists() {
        // ARRANGE
        when(permissionRepository.existsByName(anyString())).thenReturn(true);

        // ACT y ASSERT
        assertThrows(DuplicatedEntryException.class, () -> permissionService.createPermission(permission));

        verify(permissionRepository, times(1)).existsByName(permission.getName());
        verify(permissionRepository, never()).save(any(Permission.class));
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

    /**
     * dado: que todos los permisos especificados existen en la base de datos.
     * cuando: se buscan los permisos por sus IDs.
     * entonces: el método devuelve una lista con los permisos encontrados.
     */
    @Test
    public void shouldFindAllPermissionsByIdSuccessfully() throws NotFoundException {
        // ARRANGE
        List<Permission> permissionsToFind = List.of(permission);
        when(permissionRepository.findById(anyString())).thenReturn(Optional.of(permission));

        // ACT
        List<Permission> result = permissionService.findAllById(permissionsToFind);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(permissionRepository, times(1)).findById(anyString());
    }
}
