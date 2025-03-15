package com.hospitalApi.employees.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import com.hospitalApi.users.ports.ForUsersPort;
import com.hospitalApi.users.repositories.UserRepository;

public class EmployeesServicesTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ForEmployeeTypePort forEmployeeTypePort;

    @Mock
    private ForUsersPort forUsersPort;

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
        employee.setId("adsfgdh-arsgdfhg-adfgh");

        user = new User("Luis", "123");
        user.setId("wqer-qwerw-qweq");

        employeeType = new EmployeeType();
        employeeType.setId("dasdd-asdasd-asdasd");
    }

    @Test
    public void insertEmployee() throws DuplicatedEntryException, NotFoundException {

        // ARRANGE
        // configuramos el mock para que lance el user cuando este sea creado
        when(forEmployeeTypePort.verifyExistsEmployeeTypeById(any(EmployeeType.class))).thenReturn(true);
        when(forUsersPort.createUser(any(User.class))).thenReturn(user);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        // ACT
        Employee result = employeeService.createEmployee(employee, employeeType, user);

        // ASSERT
        // captor para capturar el objeto pasado a save()
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        // verificar que se llamó a save() y capturar el argumento
        verify(employeeRepository).save(employeeCaptor.capture());
        // Obtener el objeto capturado
        Employee capturedEmployee = employeeCaptor.getValue();

        assertAll(
                () -> assertNotNull(result, "El empleado no debería ser nulo"),
                () -> assertEquals(result.getFirstName(), capturedEmployee.getFirstName(),
                        "El nombre debe coincidir"),
                () -> assertEquals(result.getLastName(), capturedEmployee.getLastName(),
                        "El apellido debe coincidir"),
                () -> assertEquals(result.getSalary(), capturedEmployee.getSalary(), "El salario debe coincidir"),
                () -> assertEquals(user, capturedEmployee.getUser(), "El usuario debe coincidir"),
                () -> assertEquals(employeeType, capturedEmployee.getEmployeeType(),
                        "El tipo de empleado debe coincidir")

        );

        // se verifican las llamadas a los métodos dependientes
        verify(forUsersPort, times(1)).createUser(any(User.class));
        verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(employeeType);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void insertEmployeeWithExistantUsername() {
        try {

            // ARRANGE
            when(forEmployeeTypePort.verifyExistsEmployeeTypeById(any(EmployeeType.class))).thenReturn(true);
            when(forUsersPort.createUser(user)).thenThrow(DuplicatedEntryException.class);

            // ACT and Asserts
            assertThrows(DuplicatedEntryException.class, () -> {
                // se verifica que se haya lanzado la excepcion
                employeeService.createEmployee(employee, employeeType, user);
            });

            verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(any(EmployeeType.class));
            verify(forUsersPort, times(1)).createUser(any(User.class));
            verify(employeeRepository, times(0)).save(employee);
        } catch (DuplicatedEntryException | NotFoundException e) {

        }
    }

    @Test
    public void insertEmployeeWithInexistantEmployeeType() {
        try {
            // ARRANGE
            when(forEmployeeTypePort.verifyExistsEmployeeTypeById(any(EmployeeType.class))).thenThrow(
                    NotFoundException.class);

            // ACT
            assertThrows(NotFoundException.class, () -> {
                // se verifica que se haya lanzado la excepcion
                employeeService.createEmployee(employee, employeeType, user);
            });

            // Asserts
            verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(any(EmployeeType.class));
            verify(forUsersPort, times(0)).createUser(any(User.class));
            verify(employeeRepository, times(0)).save(employee);

        } catch (DuplicatedEntryException | NotFoundException e) {

        }
    }

}
