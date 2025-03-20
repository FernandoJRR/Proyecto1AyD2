package com.hospitalApi.auth.login.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.hospitalApi.auth.jwt.ports.ForJwtGenerator;
import com.hospitalApi.auth.login.dtos.LoginResponseDTO;
import com.hospitalApi.auth.login.ports.ForUserLoader;
import com.hospitalApi.auth.login.service.LoginService;
import com.hospitalApi.employees.dtos.EmployeeHistoriesResponseDTO;
import com.hospitalApi.employees.dtos.EmployeeHistoryResponseDTO;
import com.hospitalApi.employees.dtos.EmployeeResponseDTO;
import com.hospitalApi.employees.dtos.EmployeeTypeResponseDTO;
import com.hospitalApi.employees.dtos.HistoryTypeResponseDTO;
import com.hospitalApi.employees.mappers.EmployeeMapper;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.ports.ForUsersPort;

public class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ForJwtGenerator forJwtGenerator;

    @Mock
    private ForUsersPort forUsersPort;

    @Mock
    private ForUserLoader forUserLoader;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private LoginService loginService;

    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "tetsPassword";
    private static final String JWT_TOKEN = "jwt-token";

    // Para el employee response
    private static final String ID = "EMP001";
    private static final String FIRST_NAME = "Juan";
    private static final String LAST_NAME = "Pérez";
    private static final BigDecimal SALARY = new BigDecimal("5000.00");
    private static final BigDecimal IGSS_PERCENTAGE = new BigDecimal("4.83");
    private static final BigDecimal IRTRA_PERCENTAGE = new BigDecimal("1.00");
    private static final String EMPLOYEE_TYPE = "FARMACIA";
    private static final String EMPLOYEE_HISTORY_COMMENTARY = "Comentario";
    private static final LocalDate EMPLOYEE_HISTORY_DATE = LocalDate.of(2022, 11, 22);
    private static final LocalDateTime RESIGN_DATE = null;

    // objetos a devolver en las pruebas
    private User user;
    private Set<GrantedAuthority> permissions;
    private EmployeeResponseDTO employeeResponseDTO;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(USERNAME, PASSWORD);
        // el usuairo siempre estara activo en las pruebas CAMBIARLO MANUAL
        user.setDesactivatedAt(null);

        // configuracion de permisos

        permissions = Set.of();
        // configuramos la respuesta que se da
        List<EmployeeHistoryResponseDTO> employeeHistoryResponseDTOs = Arrays.asList(
                    new EmployeeHistoryResponseDTO(
                        new HistoryTypeResponseDTO(),
                        EMPLOYEE_HISTORY_COMMENTARY,
                        EMPLOYEE_HISTORY_DATE
                    )
        );
        employeeResponseDTO = new EmployeeResponseDTO(ID,
                FIRST_NAME,
                LAST_NAME,
                SALARY,
                IGSS_PERCENTAGE,
                IRTRA_PERCENTAGE,
                new EmployeeTypeResponseDTO(ID, EMPLOYEE_TYPE),
                new EmployeeHistoriesResponseDTO()
                );
    }

    /**
     *
     * dado: un usuario existente con credenciales correctas.
     * cuando: se llama al método login con su nombre de usuario y contraseña.
     * entonces: el usuario es autenticado, se cargan sus permisos, se genera un jwt
     * y se retorna un `LoginResponseDTO` con los datos correspondientes.
     *
     */
    @Test
    public void shouldDoLoginAndReturnLoginResponseWhenValidCredentials() throws NotFoundException {
        // ARREANGE

        // que devuelva el usuario cuando se busque por nombre
        when(forUsersPort.findUserByUsername(user.getUsername())).thenReturn(user);
        // que devuelva correcamente la autenticacion
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD));
        // cuando se carguen los permissos entonces devolver el mok
        when(forUserLoader.loadUserPermissions(user)).thenReturn(permissions);
        // cuando se cree el token devovler el mok
        when(forJwtGenerator.generateToken(user, permissions)).thenReturn(JWT_TOKEN);
        // cuando se trate de mapear el empleado devolver el mok
        when(employeeMapper.fromEmployeeToResponse(user.getEmployee())).thenReturn(employeeResponseDTO);

        // ACT
        LoginResponseDTO result = loginService.login(user.getUsername(), user.getPassword());

        // ASSERTS
        assertAll(
                () -> assertNotNull(result),

                () -> assertEquals(USERNAME, result.getUsername()),
                () -> assertEquals(JWT_TOKEN, result.getToken()),
                () -> assertEquals(employeeResponseDTO, result.getEmployee()));

        verify(forUsersPort, times(1)).findUserByUsername(user.getUsername());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(forUserLoader, times(1)).loadUserPermissions(user);
        verify(forJwtGenerator, times(1)).generateToken(user, permissions);
        verify(employeeMapper, times(1)).fromEmployeeToResponse(user.getEmployee());

    }

    /**
     * dado: que el usuario no existe en el sistema.
     * cuando: se intenta iniciar sesión con un nombre de usuario inexistente.
     * entonces: se lanza una excepción `NotFoundException` y no se ejecuta
     * la autenticación, carga de permisos ni generación de jwt.
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenUserDoesNotExist() throws NotFoundException {
        // ARREANGE

        // que lance excepcion de no encontrado
        when(forUsersPort.findUserByUsername(user.getUsername())).thenThrow(new NotFoundException(anyString()));

        // ACT y ASSERT
        assertThrows(NotFoundException.class, () -> {
            LoginResponseDTO result = loginService.login(user.getUsername(), user.getPassword());
        });

        verify(forUsersPort, times(1)).findUserByUsername(user.getUsername());
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(forUserLoader, times(0)).loadUserPermissions(user);
        verify(forJwtGenerator, times(0)).generateToken(user, permissions);
        verify(employeeMapper, times(0)).fromEmployeeToResponse(user.getEmployee());

    }

    /**
     * dado: un usuario existente en el sistema que está desactivado.
     * cuando: se intenta iniciar sesión con ese usuario.
     * entonces: se lanza una excepción `NotFoundException` y no se ejecutan
     * la autenticación, la carga de permisos ni la generación de jwt.
     *
     * @throws NotFoundException
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenUserIsDeactivated() throws NotFoundException {
        // ARREANGE

        // que devuelva al usuario cuando se busque popr nombre
        when(forUsersPort.findUserByUsername(user.getUsername())).thenReturn(user);
        // seteamos el desactivated porque asi se lanza una excepcion notfound
        user.setDesactivatedAt(LocalDate.now());

        // ACT y ASSERT
        assertThrows(NotFoundException.class, () -> {
            loginService.login(user.getUsername(), user.getPassword());
        });

        verify(forUsersPort, times(1)).findUserByUsername(user.getUsername());
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(forUserLoader, times(0)).loadUserPermissions(user);
        verify(forJwtGenerator, times(0)).generateToken(user, permissions);
        verify(employeeMapper, times(0)).fromEmployeeToResponse(user.getEmployee());

    }

    /**
     * dado: un usuario existente en el sistema con credenciales incorrectas.
     * cuando: se intenta iniciar sesión con ese usuario.
     * entonces: se lanza una excepción `BadCredentialsException` y no se ejecutan
     * la carga de permisos ni la generación de jwt.
     *
     * @throws NotFoundException
     */
    @Test
    public void shouldThrowBadCredentialsExceptionWhenAuthenticationFails() throws NotFoundException {
        // ARREANGE

        // que devuelva al usuario cuando se busque popr nombre
        when(forUsersPort.findUserByUsername(user.getUsername())).thenReturn(user);
        // cuando se haga la autenticacion debe fallar
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(""));

        // ACT y ASSERT
        assertThrows(BadCredentialsException.class, () -> {
            loginService.login(user.getUsername(), user.getPassword());
        });

        verify(forUsersPort, times(1)).findUserByUsername(user.getUsername());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(forUserLoader, times(0)).loadUserPermissions(user);
        verify(forJwtGenerator, times(0)).generateToken(user, permissions);
        verify(employeeMapper, times(0)).fromEmployeeToResponse(user.getEmployee());

    }

}
