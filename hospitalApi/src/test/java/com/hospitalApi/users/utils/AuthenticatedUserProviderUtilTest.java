package com.hospitalApi.users.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserProviderUtilTest {

    @Mock
    private ForEmployeesPort forEmployeesPort;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthenticatedUserProviderUtil authenticatedUserProviderUtil;

    private Employee expectedEmployee;
    private static final String USERNAME = "usuario";
    private static final String EMPLOYEE_ID = "32435-34254-1324";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        expectedEmployee = new Employee();
        expectedEmployee.setId(EMPLOYEE_ID);
    }

    /**
     * dado: que hay un usuario autenticado y existe un empleado con su nombre de
     * usuario.
     * cuando: se llama a getAutenticatedEmployee().
     * entonces: retorna el objeto Employee correspondiente.
     */
    @Test
    public void shouldReturnEmployeeWhenUserIsAuthenticated() throws NotFoundException {
        // arrgne
        when(userDetails.getUsername()).thenReturn(USERNAME);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(forEmployeesPort.findEmployeeByUsername(anyString())).thenReturn(expectedEmployee);

        // act
        Employee result = authenticatedUserProviderUtil.getAutenticatedEmployee();

        // assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(EMPLOYEE_ID, result.getId()));
    }

    /**
     * dado: que no hay ningún usuario autenticado en el contexto de seguridad.
     * cuando: se llama a getAutenticatedEmployee().
     * entonces: se lanza una excepción IllegalStateException.
     */
    @Test
    public void shouldThrowIllegalStateExceptionWhenNoAuthenticationPresent() {
        // arragne
        SecurityContextHolder.clearContext();
        // assert y act
        assertThrows(
                IllegalStateException.class,
                () -> authenticatedUserProviderUtil.getAutenticatedEmployee());

    }

    /**
     * dado: que hay un usuario autenticado pero no se encuentra el empleado
     * correspondiente.
     * cuando: se llama a getAutenticatedEmployee().
     * entonces: se lanza una excepción NotFoundException.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenEmployeeNotFound() throws NotFoundException {
        // arrange
        when(userDetails.getUsername()).thenReturn(USERNAME);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(forEmployeesPort.findEmployeeByUsername(USERNAME))
                .thenThrow(new NotFoundException(""));

        // assert
        assertThrows(
                NotFoundException.class,
                () -> authenticatedUserProviderUtil.getAutenticatedEmployee());
    }
}
