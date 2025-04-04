package com.hospitalApi.shared.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PasswordEncoderUtilTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderUtil passwordEncoderUtil;

    private static final String RAW_PASSWORD = "ADMIN123";
    private static final String HASHED_PASSWORD = "ESTOESUNHASHXD";

    /**
     * dado: una contraseña en texto plano.
     * cuando: se llama a encode().
     * entonces: se debe retornar una versión encriptada usando el PasswordEncoder.
     */
    @Test
    public void shouldEncodeRawPassword() {
        // arrage
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        // act
        String encoded = passwordEncoderUtil.encode(RAW_PASSWORD);
        // assert
        assertEquals(HASHED_PASSWORD, encoded); // validamos el patrón del hash bcrypt
    }

    /**
     * dado: una contraseña en texto plano y su versión encriptada correspondiente.
     * cuando: se llama a matches().
     * entonces: se debe retornar true si coinciden.
     */
    @Test
    public void shouldMatchPassword() {
        // arange
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        // act
        boolean result = passwordEncoderUtil.matches(RAW_PASSWORD, HASHED_PASSWORD);
        // assert
        assertTrue(result);
    }

    /**
     * dado: una contraseña en texto plano y un hash que no coincide.
     * cuando: se llama a matches().
     * entonces: se debe retornar false si no coinciden.
     */
    @Test
    public void shouldNotMatchPassword() {
        // arrange
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(false);
        // act
        boolean result = passwordEncoderUtil.matches(RAW_PASSWORD, HASHED_PASSWORD);
        // assert
        assertFalse(result);
    }
}
