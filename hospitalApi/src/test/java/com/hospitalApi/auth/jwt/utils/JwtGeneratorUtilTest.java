package com.hospitalApi.auth.jwt.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;

import com.hospitalApi.users.models.User;

public class JwtGeneratorUtilTest {

    @InjectMocks
    private JwtGeneratorUtil jwtGeneratorUtil;

    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "tetsPassword";

    private User user;

    private Set<GrantedAuthority> permissions;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(USERNAME, PASSWORD);
        permissions = Set.of();
    }

    /**
     * dado: un usuario válido con permisos asignados.
     * cuando: se genera un token JWT para el usuario.
     * entonces: el token generado debe ser válido, no nulo, no vacío y debe cumplir
     * con la estructura de un JWT (estar compuesto por tres partes
     * separadas por puntos).
     *
     */
    @Test
    void shouldGenerateValidJwtToken() {
        // ACT
        String token = jwtGeneratorUtil.generateToken(user, permissions);
        // ASSERT
        assertAll(
                () -> assertNotNull(token),
                () -> assertFalse(token.isEmpty()),
                // un JWT válido tiene tres partes separadas por puntos
                () -> assertTrue(token.split("\\.").length == 3));

    }

}
