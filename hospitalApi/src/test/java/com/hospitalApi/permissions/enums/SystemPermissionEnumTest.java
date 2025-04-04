package com.hospitalApi.permissions.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class SystemPermissionEnumTest {

    /**
     * dado: todas las constantes del enum SystemPermissionEnum.
     * cuando: se obtienen sus nombres de permiso.
     * entonces: no debe existir ningún nombre duplicado.
     */
    @Test
    public void shouldHaveUniqueKeys() {
        // obtenemos todos los permisos en el enum
        SystemPermissionEnum[] allKeys = SystemPermissionEnum.values();

        // con un stream vamos mapeando todos los nombres, luego devolemos otro stream
        // con los nombres unicos y contamos el
        // nuevo stream
        long uniqueCount = Stream.of(allKeys)
                .map(p -> p.getPermission().getName())
                .distinct()
                .count();
        // el tamanio del array tiene que tener el mismo tamanio que el stream ya
        // filtrado con nombres unicos
        assertEquals(allKeys.length, uniqueCount);
    }
}
