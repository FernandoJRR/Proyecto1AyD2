package com.hospitalApi.auth.jwt.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospitalApi.users.models.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtManagerUtilTest {

    private JwtManagerUtil jwtManagerUtil;
    private JwtGeneratorUtil jwtGeneratorUtil;

    private String validToken;
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password";

    @BeforeEach
    public void setUp() {
        jwtManagerUtil = new JwtManagerUtil();
        jwtGeneratorUtil = new JwtGeneratorUtil();
        // se esta generando un jwt siempre, es de prueba
        validToken = jwtGeneratorUtil.generateToken(new User(USERNAME, PASSWORD), Set.of());
    }

    /**
     * dado: un token jwt válido generado para un usuario.
     * cuando: se extrae el nombre de usuario del token.
     * entonces: el nombre de usuario obtenido no debe ser nulo y debe coincidir con
     * el esperado.
     *
     */
    @Test
    public void shouldExtractUsernameFromValidToken() {
        // ACT
        String result = jwtManagerUtil.extractUsername(validToken);

        // ASSERT - no debe ser nulo y debe ser igual a la constante
        assertAll(() -> assertNotNull(result),
                () -> assertEquals(USERNAME, result));

    }

    /**
     * dado: un token jwt válido con una fecha de expiración establecida.
     * cuando: se extrae la fecha de expiración del token.
     * entonces: la fecha de expiración obtenida no debe ser nula.
     */
    @Test
    public void shouldExtractExpirationDateFromValidToken() {
        // ACT
        Date expirationDate = jwtManagerUtil.extractExpiration(validToken);

        // ASSERT - no debe estar nula
        assertNotNull(expirationDate);
    }

    /**
     * dado: un token jwt válido generado para un usuario específico.
     * cuando: se valida el token con el nombre de usuario correspondiente.
     * entonces: la validación debe ser exitosa y retornar true.
     */
    @Test
    public void shouldValidateTokenCorrectly() {
        // ACT
        boolean isValid = jwtManagerUtil.isTokenValid(validToken, USERNAME);

        // ASSERT ' debe estar verdadero porque tiene 1 dia de duracion el jwt
        assertTrue(isValid);
    }

    @Test
    public void shouldReturnFalseWhenTokenIsExpired() {
        // Generamos un token inspirado
        String expiredToken = Jwts.builder()
                .subject(USERNAME)
                // esta es la feecha en la que fue creado el jwt, este date se crea con el
                // currentMilis
                .issuedAt(new Date())
                // entonces creamos este date con el currentMilis decrementado
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(JwtGeneratorUtil.SECRET_KEY.getBytes()))
                .compact();

        // ACT y ASSERT, deberia lanzar una excepcion porque el token esta invalido
        assertThrows(JwtException.class, () -> {
            jwtManagerUtil.isTokenExpired(expiredToken);
        });

    }

}
