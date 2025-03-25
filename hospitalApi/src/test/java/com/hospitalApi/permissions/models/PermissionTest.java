package com.hospitalApi.permissions.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    private static final String PERMISSION_ID = "perm-001";
    private static final String PERMISSION_NAME = "Create Employee";
    private static final String PERMISSION_ACTION = "CREATE_EMPLOYEE";

    /**
     * dado: un nombre y acción válidos.
     * cuando: se crea una instancia de Permission usando el constructor con nombre
     * y acción.
     * entonces: los campos name y action se asignan correctamente y el id permanece
     * nulo.
     */
    @Test
    public void shouldCreatePermissionWithoutId() {
        Permission permission = new Permission(PERMISSION_NAME, PERMISSION_ACTION);

        assertAll(
                () -> assertNull(permission.getId()),
                () -> assertEquals(PERMISSION_NAME, permission.getName()),
                () -> assertEquals(PERMISSION_ACTION, permission.getAction()));
    }

    /**
     * dado: un ID válido.
     * cuando: se crea una instancia de Permission usando el constructor con ID.
     * entonces: el campo id se asigna correctamente y los campos name y action
     * permanecen nulos.
     */
    @Test
    public void shouldCreatePermissionWithIdOnly() {
        Permission permission = new Permission(PERMISSION_ID);

        assertAll(
                () -> assertEquals(PERMISSION_ID, permission.getId()),
                () -> assertNull(permission.getName()),
                () -> assertNull(permission.getAction()));
    }
}
