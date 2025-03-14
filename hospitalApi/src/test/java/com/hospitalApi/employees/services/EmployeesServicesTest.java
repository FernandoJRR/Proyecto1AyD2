package com.hospitalApi.employees.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.repositories.EmployeeRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.repositories.UserRepository;
import com.hospitalApi.users.services.UserService;

public class EmployeesServicesTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ForEmployeeTypePort forEmployeeTypePort;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmployeeService employeeService;

    private User user;
    private Employee employee;
    private EmployeeType employeeType;

    /**
     * este metodo se ejecuta antes de cualquier prueba individual, se hace para
     * inicializar los moks ademas del driver
     */
    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee("Luis", "Monterroso",
                new BigDecimal(1200),
                new BigDecimal(10.2),
                new BigDecimal(10.2),
                null);
        user = new User("Luis", "123");
        employeeType = new EmployeeType("asdfg-sdfgh-awsdfgh");
    }

    @Test
    public void insertEmployeeWithExistantUsername() throws DuplicatedEntryException {
        // configuramos el mock para que lance una excepción al intentar crearlo
        when(userService.createUser(user)).thenThrow(
                new DuplicatedEntryException("El usuario ya existe con el nombre: " + user.getUsername()));
        // cuando se busque si existe o no el tipo de usuario entonces se devolvera true

        // verificamos que la excepción se lanza correctamente
        assertThrows(DuplicatedEntryException.class, () -> {
            userService.createUser(user);
        });

        verify(employeeRepository, times(0)).save(employee);
    }

    @Test
    public void insertEmployee() throws DuplicatedEntryException, NotFoundException {
        // configuramos el mock para que lance el user cuando este sea creado
        when(userService.createUser(user)).thenReturn(user);
        // configurar que el mock devuelva el empleado creado
        when(employeeService.createEmployee(employee, employeeType, user)).thenReturn(employee);
        // verifcar que el save se ejecuto una vez
        verify(employeeRepository, times(1)).save(employee);
    }
}
