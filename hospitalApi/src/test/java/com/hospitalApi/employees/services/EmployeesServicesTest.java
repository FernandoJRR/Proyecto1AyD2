package com.hospitalApi.employees.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private Employee updatedEmployee;
    private EmployeeType employeeType;

    /** Para el nuevo empleado */
    private static final String EMPLOYEE_ID = "adsfgdh-arsgdfhg-adfgh";
    private static final String EMPLOYEE_FIRST_NAME = "Luis";
    private static final String EMPLOYEE_LAST_NAME = "Monterroso";
    private static final BigDecimal EMPLOYEE_SALARY = new BigDecimal(1200);
    private static final BigDecimal EMPLOYEE_IGSS = new BigDecimal(10.2);
    private static final BigDecimal EMPLOYEE_IRTRA = new BigDecimal(10.2);

    /** Para el objeto usuario */
    private static final String USER_ID = "wqer-qwerw-qweq";
    private static final String USER_NAME = "Luis";
    private static final String USER_PASSWORD = "123";

    /** Para el objeto tipo de empleado */
    private static final String EMPLOYEE_TYPE_ID = "dasdd-asdasd-asdasd";

    /** Para actualizaciones */
    private static final String UPDATED_EMPLOYEE_FIRST_NAME = "Carlos";
    private static final String UPDATED_EMPLOYEE_LAST_NAME = "Ramírez";
    private static final BigDecimal UPDATED_EMPLOYEE_SALARY = new BigDecimal(7000);
    private static final BigDecimal UPDATED_EMPLOYEE_IGSS = new BigDecimal(5.25);
    private static final BigDecimal UPDATED_EMPLOYEE_IRTRA = new BigDecimal(10.2);

    /**
     * este metodo se ejecuta antes de cualquier prueba individual, se hace para
     * inicializar los moks ademas del driver
     */
    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(
                EMPLOYEE_FIRST_NAME,
                EMPLOYEE_LAST_NAME,
                EMPLOYEE_SALARY,
                EMPLOYEE_IGSS,
                EMPLOYEE_IRTRA,
                null);
        employee.setId(EMPLOYEE_ID);

        updatedEmployee = new Employee(
                UPDATED_EMPLOYEE_FIRST_NAME,
                UPDATED_EMPLOYEE_LAST_NAME,
                UPDATED_EMPLOYEE_SALARY,
                UPDATED_EMPLOYEE_IGSS,
                UPDATED_EMPLOYEE_IRTRA,
                null);

        user = new User(USER_ID, USER_NAME, USER_PASSWORD);

        employeeType = new EmployeeType();
        employeeType.setId(EMPLOYEE_TYPE_ID);

        // inicializamos los empleados que usaremos para la reasignacion del tipo de
        // empleado
        employeeToReasignEmployeeType1 = new Employee(EMPLOYEE_ID_1);
        employeeToReasignEmployeeType2 = new Employee(EMPLOYEE_ID_2);
    }

    @Test
    public void shouldInsertEmployee() throws DuplicatedEntryException, NotFoundException {

        // ARRANGE
        // configuramos el mock para que lance el user cuando este sea creado
        when(forEmployeeTypePort.findEmployeeTypeById(anyString())).thenReturn(employeeType);
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
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), capturedEmployee.getFirstName()),
                () -> assertEquals(result.getLastName(), capturedEmployee.getLastName()),
                () -> assertEquals(result.getSalary(), capturedEmployee.getSalary()),
                () -> assertEquals(user, capturedEmployee.getUser()),
                () -> assertEquals(employeeType, capturedEmployee.getEmployeeType())

        );

        // se verifican las llamadas a los métodos dependientes
        verify(forUsersPort, times(1)).createUser(any(User.class));
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void shouldNotInsertEmployeeWithExistantUsername() throws DuplicatedEntryException, NotFoundException {

        // ARRANGE
        when(forEmployeeTypePort.findEmployeeTypeById(anyString())).thenReturn(employeeType);
        when(forUsersPort.createUser(user)).thenThrow(DuplicatedEntryException.class);

        // ACT and Asserts
        assertThrows(DuplicatedEntryException.class, () -> {
            // se verifica que se haya lanzado la excepcion
            employeeService.createEmployee(employee, employeeType, user);
        });

        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(anyString());
        verify(forUsersPort, times(1)).createUser(any(User.class));
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void shouldNotInsertEmployeeWithInexistantEmployeeType() throws NotFoundException, DuplicatedEntryException {
        // ARRANGE
        when(forEmployeeTypePort.findEmployeeTypeById(anyString())).thenThrow(
                NotFoundException.class);

        // ACT
        assertThrows(NotFoundException.class, () -> {
            // se verifica que se haya lanzado la excepcion
            employeeService.createEmployee(employee, employeeType, user);
        });

        // Asserts
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(anyString());
        verify(forUsersPort, times(0)).createUser(any(User.class));
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void updateEmployeeShouldUpdateFieldsAndSave() throws NotFoundException {

        // ARRANGE
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(forEmployeeTypePort.findEmployeeTypeById(anyString())).thenReturn(employeeType);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // ACT
        Employee updateEmployee = employeeService.updateEmployee(employee.getId(), updatedEmployee, employeeType);

        // validar que los setters fueron llamados con los valores correctos
        assertAll(
                () -> assertEquals(UPDATED_EMPLOYEE_FIRST_NAME, updateEmployee.getFirstName()),
                () -> assertEquals(UPDATED_EMPLOYEE_FIRST_NAME, updateEmployee.getFirstName()),
                () -> assertEquals(UPDATED_EMPLOYEE_LAST_NAME, updateEmployee.getLastName()),
                () -> assertEquals(UPDATED_EMPLOYEE_SALARY, updateEmployee.getSalary()),
                () -> assertEquals(UPDATED_EMPLOYEE_IGSS, updateEmployee.getIgssPercentage()),
                () -> assertEquals(UPDATED_EMPLOYEE_IRTRA, updateEmployee.getIrtraPercentage()),
                () -> assertEquals(employeeType, updateEmployee.getEmployeeType()));

        // Asegurar que se guardó en la base de datos
        verify(employeeRepository, times(1)).findById(anyString());
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));

    }

    @Test
    public void updateEmployeeShouldNotUpdateEmployeeWithInexistantEmployee() throws NotFoundException {
        // ARRANGE
        // cuando se busque el empleado por id entonces volvr vacio para que lance la
        // excepcion
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        // ACT
        assertThrows(NotFoundException.class, () -> {
            employeeService.updateEmployee(anyString(), employee, employeeType);
        });

        // Asserts
        verify(employeeRepository, times(1)).findById(anyString());
        verify(forEmployeeTypePort, times(0)).existsEmployeeTypeById(anyString());
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void updateEmployeeShouldNotUpdateEmployeeWithInexistantEmployeeType() throws NotFoundException {
        // ARRANGE
        // cuando se busque por id entonces devolver el employee
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));

        when(forEmployeeTypePort.findEmployeeTypeById(anyString())).thenThrow(
                NotFoundException.class);
        // ACT
        assertThrows(NotFoundException.class, () -> {
            employeeService.updateEmployee(anyString(), employee, employeeType);
        });

        // ASSERTS
        verify(employeeRepository, times(1)).findById(anyString());
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(anyString());
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void testDesactivateEmployee() throws NotFoundException {
        // ARRANGE
        employee.setUser(user); // aseguramos que el empleado tenga un usuario
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // ACT
        employeeService.desactivateEmployee(EMPLOYEE_ID);
        // capturamos luego del set
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee capturedEmployee = employeeCaptor.getValue();

        // ASSERTS
        LocalDate date = LocalDate.now();
        assertAll(
                () -> assertNotNull(capturedEmployee),
                () -> assertEquals(date, capturedEmployee.getDesactivatedAt()),
                () -> assertEquals(date, capturedEmployee.getUser().getDesactivatedAt()));
    }

    /**
     * dado: que el empleado ya está desactivado en el sistema.
     * cuando: se intenta desactivar nuevamente.
     * entonces: se lanza una excepción IllegalStateException y no se realizan
     * cambios.
     */
    @Test
    public void desactivateEmployeeShouldThrowIllegalStateExceptionWhenEmployeeIsAlreadyDeactivated()
            throws NotFoundException {
        // ARRANGE
        employee.setUser(user); // aseguramos que el empleado tenga un usuario
        // hacemos que el usuario ya este desactivado
        employee.setDesactivatedAt(LocalDate.now());
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));

        // ACT y ASSERT
        assertThrows(IllegalStateException.class,
                () -> {
                    employeeService.desactivateEmployee(EMPLOYEE_ID);
                });
    }

    @Test
    public void desactivateEmployeeShouldNotDesactivateEmployeeWithInexistantEmployee() throws NotFoundException {
        // ARRANGE
        // cuando se busque por id mandamos vacio para que se lance la excepcion
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        // ACT
        // Asserts
        assertThrows(NotFoundException.class, () -> {
            employeeService.desactivateEmployee(anyString());
        });
    }

    /**
     * dado: que el empleado y el tipo de empleado existen en la base de datos.
     * cuando: se reasigna el tipo de empleado a un nuevo tipo válido.
     * entonces: el empleado debe tener actualizado el nuevo tipo de empleado.
     */
    @Test
    public void reassignEmployeeTypeShouldReassignEmployeeTypeSuccessfully() throws NotFoundException {

        // ARRANGE
        // cuando se busque por el id entonoces devolver nuestro mock de empleado
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        // cuando se buswque e tipo de empleado a asignar entonces devolver nuestro mock
        when(forEmployeeTypePort.findEmployeeTypeById(anyString())).thenReturn(employeeType);

        // ACT
        Employee result = employeeService.reassignEmployeeType(EMPLOYEE_ID, EMPLOYEE_TYPE_ID);

        // ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(employeeType, result.getEmployeeType()));

        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(EMPLOYEE_TYPE_ID);
    }

    /**
     * dado: que el empleado no existe en la base de datos.
     * cuando: se intenta reasignar su tipo de empleado.
     * entonces: se lanza una excepción `NotFoundException` y no se realizan
     * cambios.
     * 
     * @throws NotFoundException
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenEmployeeDoesNotExist() throws NotFoundException {
        // ARRANGE
        // al devolver el empty en el findBi entonces el metodo debe lanzar un not found
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        // ACT &y ASSERT
        assertThrows(NotFoundException.class,
                () -> employeeService.reassignEmployeeType(EMPLOYEE_ID, EMPLOYEE_TYPE_ID));

        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
        verify(forEmployeeTypePort, never()).findEmployeeTypeById(anyString());
    }

    /**
     * dado: que el tipo de empleado no existe en la base de datos.
     * cuando: se intenta reasignar el empleado a ese tipo de empleado inexistente.
     * entonces: se lanza una excepción `NotFoundException` y no se realizan
     * cambios.
     * 
     * @throws NotFoundException
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenEmployeeTypeDoesNotExist() throws NotFoundException {
        // ARRANGE
        // si deolvemos el optional lleno
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        // cuando mandemos a busqcar el tpio de empleado a asignar simulamos que este no
        // se encontro
        when(forEmployeeTypePort.findEmployeeTypeById(anyString()))
                .thenThrow(new NotFoundException(anyString()));

        // ACT y ASSERT
        assertThrows(NotFoundException.class,
                () -> employeeService.reassignEmployeeType(EMPLOYEE_ID, EMPLOYEE_TYPE_ID));

        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(EMPLOYEE_TYPE_ID);

    }

    private static final String EMPLOYEE_ID_1 = "sdaf-asdf-sad";
    private static final String EMPLOYEE_ID_2 = "";

    Employee employeeToReasignEmployeeType1;
    Employee employeeToReasignEmployeeType2;

    /**
     * dado: que una lista de empleados y un tipo de empleado existen en la base de
     * datos.
     * cuando: se reasignan todos los empleados a un nuevo tipo de empleado válido.
     * entonces: todos los empleados deben tener actualizado el nuevo tipo de
     * empleado.
     */
    @Test
    public void shouldReassignEmployeeTypeForMultipleEmployeesSuccessfully() throws NotFoundException {
        // ARRANGE
        List<Employee> employees = List.of(employeeToReasignEmployeeType1, employeeToReasignEmployeeType2);

        when(employeeRepository.findById(EMPLOYEE_ID_1)).thenReturn(Optional.of(employeeToReasignEmployeeType1));
        when(employeeRepository.findById(EMPLOYEE_ID_2)).thenReturn(Optional.of(employeeToReasignEmployeeType2));
        when(forEmployeeTypePort.findEmployeeTypeById(EMPLOYEE_TYPE_ID)).thenReturn(employeeType);

        // ACT
        List<Employee> result = employeeService.reassignEmployeeType(employees, EMPLOYEE_TYPE_ID);

        // ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(employeeType, result.get(0).getEmployeeType()),
                () -> assertEquals(employeeType, result.get(1).getEmployeeType()));

        verify(employeeRepository, times(2)).findById(anyString());
        verify(forEmployeeTypePort, times(2)).findEmployeeTypeById(EMPLOYEE_TYPE_ID);
    }

    /**
     * dado: que al menos un empleado de la lista no existe en la base de datos.
     * cuando: se intenta reasignar su tipo de empleado.
     * entonces: se lanza una excepción `NotFoundException` y no se realizan cambios
     * en ningún empleado.
     * 
     * @throws NotFoundException
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenAnyEmployeeDoesNotExist() throws NotFoundException {
        // ARRANGE
        List<Employee> employees = List.of(employeeToReasignEmployeeType1, employeeToReasignEmployeeType2);

        when(employeeRepository.findById(EMPLOYEE_ID_1)).thenReturn(Optional.of(employeeToReasignEmployeeType1));
        // cuando busquemos el segundo id entonces devolvemos un Optional vacio para
        // forzar el NotFound
        when(employeeRepository.findById(EMPLOYEE_ID_2)).thenReturn(Optional.empty());

        // ACT yASSERT
        assertThrows(NotFoundException.class, () -> employeeService.reassignEmployeeType(employees, EMPLOYEE_TYPE_ID));

        verify(employeeRepository, times(2)).findById(anyString());
        // solo se debra hacer realizado una busqueda del tipo de empledo porque a la
        // segunda vez ya habra fallado antes de llegar alli
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(anyString());
    }

    /**
     * dado: que el tipo de empleado no existe en la base de datos.
     * cuando: se intenta reasignar una lista de empleados a ese tipo de empleado
     * inexistente.
     * entonces: se lanza una excepción `NotFoundException` y no se realizan cambios
     * en ningún empleado.
     * 
     * @throws NotFoundException
     */
    @Test
    public void shouldThrowNotFoundExceptionWhenEmployeeTypeDoesNotExistForMultipleEmployees()
            throws NotFoundException {
        // ARRANGE
        List<Employee> employees = List.of(employeeToReasignEmployeeType1, employeeToReasignEmployeeType2);

        when(employeeRepository.findById(EMPLOYEE_ID_1)).thenReturn(Optional.of(employeeToReasignEmployeeType1));
        when(employeeRepository.findById(EMPLOYEE_ID_2)).thenReturn(Optional.of(employeeToReasignEmployeeType2));

        when(forEmployeeTypePort.findEmployeeTypeById(EMPLOYEE_TYPE_ID)).thenThrow(new NotFoundException(anyString()));

        // ACT y ASSERT
        assertThrows(NotFoundException.class, () -> employeeService.reassignEmployeeType(employees, EMPLOYEE_TYPE_ID));

        // solo se debera realzar una vez porque fallara el buscar el tipo de empleado
        verify(employeeRepository, times(1)).findById(anyString());
        verify(forEmployeeTypePort, times(1)).findEmployeeTypeById(EMPLOYEE_TYPE_ID);
    }

    /**
     * dado: que existen empleados en la base de datos.
     * cuando: se consulta la lista de empleados.
     * entonces: el método devuelve una lista con los empleados existentes.
     */
    @Test
    public void shouldReturnListOfEmployeesWhenEmployeesExist() {
        // ARRANGE
        List<Employee> employees = List.of(employee, updatedEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);

        // ACT
        List<Employee> result = employeeService.findEmployees();

        // ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()));

        verify(employeeRepository, times(1)).findAll();
    }

}
