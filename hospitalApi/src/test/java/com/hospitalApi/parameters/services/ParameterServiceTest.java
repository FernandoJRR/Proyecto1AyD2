package com.hospitalApi.parameters.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.parameters.models.Parameter;
import com.hospitalApi.parameters.repositories.ParameterRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class ParameterServiceTest {
    private static final String PARAM_KEY = "KEYMOOOK";
    private static final String PARAM_NAME = "el nombre";
    private static final String PARAM_VALUE = "el valor - fernando debio testearlo :)";

    @Mock
    private ParameterRepository parameterRepository;

    @InjectMocks
    private ParameterService parameterService;

    private Parameter parameter;

    @BeforeEach
    public void setUp() {
        parameter = new Parameter(PARAM_KEY, PARAM_VALUE, PARAM_NAME);
    }

    /**
     * dado: una llave válida de parámetro.
     * cuando: se invoca findParameterByKey().
     * entonces: se retorna el parámetro correspondiente.
     */
    @Test
    public void shouldReturnParameterWhenKeyExists() throws NotFoundException {
        // arrange
        when(parameterRepository.findOneByParameterKey(anyString()))
                .thenReturn(Optional.of(parameter));
        // act
        Parameter result = parameterService.findParameterByKey(PARAM_KEY);
        // assert
        assertAll(
                () -> assertEquals(PARAM_KEY, result.getParameterKey()),
                () -> assertEquals(PARAM_VALUE, result.getValue()),
                () -> assertEquals(PARAM_NAME, result.getName()));
    }

    /**
     * dado: una llave de parámetro que no existe en la base de datos.
     * cuando: se invoca findParameterByKey().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenKeyDoesNotExist() {
        // arrange
        when(parameterRepository.findOneByParameterKey(anyString()))
                .thenReturn(Optional.empty());
        // assert y act
        assertThrows(NotFoundException.class,
                () -> parameterService.findParameterByKey(PARAM_KEY));
    }
}