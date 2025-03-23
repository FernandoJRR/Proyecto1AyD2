package com.hospitalApi.auth.login.utils;

import com.hospitalApi.auth.login.ports.ForUserLoader;
import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.*;
import com.hospitalApi.users.ports.ForUsersPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserLoaderUtilTest {

    @Mock
    private ForUsersPort forUsersPort;

    @InjectMocks
    private UserLoaderUtil userLoaderUtil;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "securepass";
    private static final String PERMISSION_ACTION = "READ";
    private static final String PERMISSION_NAME = "CAN_READ";

    private Permission permission;
    private EmployeeType employeeType;
    private Employee employee;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // construir los objetos base con valores constantes
        permission = new Permission(PERMISSION_NAME, PERMISSION_ACTION);
        employeeType = new EmployeeType("ADMIN", List.of(permission));
        employee = new Employee();
        employee.setEmployeeType(employeeType);

        user = new User(USERNAME, PASSWORD);
        user.setEmployee(employee);
    }

    /**
     * dado: un usuario válido con permisos.
     * cuando: se carga con loadUserByUsername.
     * entonces: se devuelve un UserDetails con permisos correctamente asignados.
     */
    @Test
    void shouldLoadUserByUsernameWithPermissions() throws NotFoundException {
        // arrange

        when(forUsersPort.findUserByUsername(USERNAME)).thenReturn(user);

        // act
        UserDetails userDetails = userLoaderUtil.loadUserByUsername(USERNAME);

        // assert
        assertAll(
                () -> assertEquals(USERNAME, userDetails.getUsername()),
                () -> assertEquals(PASSWORD, userDetails.getPassword()),
                () -> assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals(PERMISSION_ACTION))));
    }

    /**
     * dado: un username que no existe.
     * cuando: se llama a loadUserByUsername.
     * entonces: se lanza UsernameNotFoundException.
     */
    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() throws NotFoundException {
        // arrange
        when(forUsersPort.findUserByUsername(USERNAME)).thenThrow(new NotFoundException(anyString()));

        // act y assert
        assertThrows(UsernameNotFoundException.class,
                () -> userLoaderUtil.loadUserByUsername(USERNAME));
    }

    /**
     * dado: un usuario null.
     * cuando: se llama a loadUserPermissions.
     * entonces: se devuelve un set vacío.
     */
    @Test
    void shouldReturnEmptyPermissionsWhenUserIsNull() {
        // arrange
        user = null;
        // act
        // al ser el usuario nulo entonces se debe devolver una lista vacia
        Set<GrantedAuthority> result = userLoaderUtil.loadUserPermissions(user);
        // assert
        assertTrue(result.isEmpty());
    }

    /**
     * dado: un usuario sin empleado.
     * cuando: se llama a loadUserPermissions.
     * entonces: se devuelve un set vacío.
     */
    @Test
    void shouldReturnEmptyPermissionsWhenEmployeeIsNull() {
        // arrange
        user.setEmployee(null);// sin un empleado
        // act
        Set<GrantedAuthority> result = userLoaderUtil.loadUserPermissions(user);
        // asset
        assertTrue(result.isEmpty());
    }

    /**
     * dado: un usuario con empleado sin tipo.
     * cuando: se llama a loadUserPermissions.
     * entonces: se devuelve un set vacío.
     */
    @Test
    void shouldReturnEmptyPermissionsWhenEmployeeTypeIsNull() {
        // arrange
        user.setEmployee(new Employee()); // sin tipo de empleado
        // act
        Set<GrantedAuthority> result = userLoaderUtil.loadUserPermissions(user);
        // asset
        assertTrue(result.isEmpty());
    }

    /**
     * dado: un usuario con tipo de empleado sin permisos.
     * cuando: se llama a loadUserPermissions.
     * entonces: se devuelve un set vacío.
     */
    @Test
    void shouldReturnEmptyPermissionsWhenEmployeeTypeHasNoPermissions() {
        // arrange
        employeeType.setPermissions(List.of());// no tiene permisos
        // act
        // al no tener permisos entonces deberia devolvernos un vacio
        Set<GrantedAuthority> result = userLoaderUtil.loadUserPermissions(user);
        // assertF
        assertTrue(result.isEmpty());
    }
}
