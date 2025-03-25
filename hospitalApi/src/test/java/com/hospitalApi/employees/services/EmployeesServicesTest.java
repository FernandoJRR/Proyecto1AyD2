package com.hospitalApi.employees.services;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForEmployeeHistoryPort;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.EmployeeRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
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

    @Mock
    private ForEmployeeHistoryPort forEmployeeHistoryPort;

    @Mock
    private ForHistoryTypePort forHistoryTypePort;

    @InjectMocks
    private EmployeeService employeeService;

    private User user;
    private HistoryType historyType;
    private HistoryType historyTypeIncrease;
    private HistoryType historyTypeDecrease;
    private EmployeeHistory employeeHistory;
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

    /** Para el historial del empleado **/
    private static final String HISTORY_TYPE_ID = "fdsf-rtrer-bbvk";
    private static final String HISTORY_TYPE = "Contratacion";
    private static final String HISTORY_TYPE_ID_INCREASE = "rewp-fkds-bbvk";
    private static final String HISTORY_TYPE_INCREASE = "Aumento Salarial";
    private static final String HISTORY_TYPE_ID_DECREASE = "dflm-fodp-bbvk";
    private static final String HISTORY_TYPE_DECREASE = "Disminucion Salarial";

    private static final String EMPLOYEE_HISTORY_ID = "rewf-fdsa-fdsd";
    private static final String EMPLOYEE_HISTORY_COMMENTARY = "Se realizo la contratacion con un salario de Q.7000";
    private static final LocalDate EMPLOYEE_HISTORY_LOCAL_DATE = LocalDate.of(2022, 11, 23);

    private static final BigDecimal EMPLOYEE_STARTING_SALARY = new BigDecimal(1200);
    private static final BigDecimal EMPLOYEE_NEW_SALARY = new BigDecimal(1500);
    private static final String EMPLOYEE_NEW_SALARY_COMMENTARY = "1500";
    private static final String EMPLOYEE_HISTORY_INCREASE_COMMENTARY = "Se realizo la contratacion con un salario de Q.1200";


    private static final BigDecimal EMPLOYEE_STARTING_DECREASE_SALARY = new BigDecimal(1500);
    private static final BigDecimal EMPLOYEE_NEW_SALARY_DECREASE = new BigDecimal(1200);
    private static final String EMPLOYEE_NEW_SALARY_DECREASE_COMMENTARY = "1200";
    private static final String EMPLOYEE_HISTORY_DECREASE_COMMENTARY = "Se realizo la contratacion con un salario de Q.1500";
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
                EMPLOYEE_IRTRA
                );
        employee.setId(EMPLOYEE_ID);

        updatedEmployee = new Employee(
                UPDATED_EMPLOYEE_FIRST_NAME,
                UPDATED_EMPLOYEE_LAST_NAME,
                UPDATED_EMPLOYEE_SALARY,
                UPDATED_EMPLOYEE_IGSS,
                UPDATED_EMPLOYEE_IRTRA
                );

        user = new User(USER_NAME, USER_PASSWORD);
        user.setId(USER_ID);

        historyType = new HistoryType(HISTORY_TYPE);
        historyType.setId(HISTORY_TYPE_ID);

        historyTypeIncrease = new HistoryType(HISTORY_TYPE_INCREASE);
        historyTypeIncrease.setId(HISTORY_TYPE_ID_INCREASE);

        historyTypeDecrease = new HistoryType(HISTORY_TYPE_DECREASE);
        historyTypeDecrease.setId(HISTORY_TYPE_ID_DECREASE);

        employeeHistory = new EmployeeHistory(EMPLOYEE_HISTORY_COMMENTARY);
        employeeHistory.setHistoryDate(EMPLOYEE_HISTORY_LOCAL_DATE);
        employeeHistory.setId(EMPLOYEE_HISTORY_ID);

        employeeType = new EmployeeType();
        employeeType.setId(EMPLOYEE_TYPE_ID);
    }

    @Test
    public void shouldInsertEmployee() throws DuplicatedEntryException, NotFoundException {

        // ARRANGE
        // configuramos el mock para que lance el user cuando este sea creado
        when(forEmployeeTypePort.verifyExistsEmployeeTypeById(anyString())).thenReturn(true);
        when(forUsersPort.createUser(any(User.class))).thenReturn(user);
        when(forHistoryTypePort.findHistoryTypeByName(anyString())).thenReturn(historyType);
        when(forEmployeeHistoryPort.createEmployeeHistoryHiring(any(Employee.class), any(LocalDate.class)))
            .thenReturn(employeeHistory);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        // ACT
        Employee result = employeeService.createEmployee(employee, employeeType, user, employeeHistory);

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
                () -> assertEquals(employeeHistory, capturedEmployee.getEmployeeHistories().get(0)),
                () -> assertEquals(employeeType, capturedEmployee.getEmployeeType())

        );

        // se verifican las llamadas a los métodos dependientes
        verify(forUsersPort, times(1)).createUser(any(User.class));
        verify(forEmployeeHistoryPort, times(1)).createEmployeeHistoryHiring(any(Employee.class), any(LocalDate.class));
        verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void shouldNotInsertEmployeeWithExistantUsername() throws DuplicatedEntryException, NotFoundException {

        // ARRANGE
        when(forEmployeeTypePort.verifyExistsEmployeeTypeById(anyString())).thenReturn(true);
        when(forUsersPort.createUser(user)).thenThrow(DuplicatedEntryException.class);

        // ACT and Asserts
        assertThrows(DuplicatedEntryException.class, () -> {
            // se verifica que se haya lanzado la excepcion
            employeeService.createEmployee(employee, employeeType, user, employeeHistory);
        });

        verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(anyString());
        verify(forUsersPort, times(1)).createUser(any(User.class));
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void shouldNotInsertEmployeeWithInexistantEmployeeType() throws NotFoundException, DuplicatedEntryException {
        // ARRANGE
        when(forEmployeeTypePort.verifyExistsEmployeeTypeById(anyString())).thenThrow(
                NotFoundException.class);

        // ACT
        assertThrows(NotFoundException.class, () -> {
            // se verifica que se haya lanzado la excepcion
            employeeService.createEmployee(employee, employeeType, user, employeeHistory);
        });

        // Asserts
        verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(anyString());
        verify(forUsersPort, times(0)).createUser(any(User.class));
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void updateEmployeeShouldUpdateFieldsAndSave() throws NotFoundException {

        // ARRANGE
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(forEmployeeTypePort.verifyExistsEmployeeTypeById(anyString())).thenReturn(true);
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
        verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));

    }

    @Test
    public void shouldNotUpdateEmployeeWithInexistantEmployee() throws NotFoundException {
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
        verify(forEmployeeTypePort, times(0)).verifyExistsEmployeeTypeById(anyString());
        verify(employeeRepository, times(0)).save(employee);

    }

    @Test
    public void shouldNotUpdateEmployeeWithInexistantEmployeeType() throws NotFoundException {
        // ARRANGE
        // cuando se busque por id entonces devolver el employee
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));

        when(forEmployeeTypePort.verifyExistsEmployeeTypeById(anyString())).thenThrow(
                NotFoundException.class);
        // ACT
        assertThrows(NotFoundException.class, () -> {
            employeeService.updateEmployee(anyString(), employee, employeeType);
        });

        // ASSERTS
        verify(employeeRepository, times(1)).findById(anyString());
        verify(forEmployeeTypePort, times(1)).verifyExistsEmployeeTypeById(anyString());
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

    @Test
    public void shouldNotDesactivateEmployeeWithInexistantEmployee() throws NotFoundException {
        // ARRANGE
        // cuando se busque por id mandamos vacio para que se lance la excepcion
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        // ACT
        // Asserts
        assertThrows(NotFoundException.class, () -> {
            employeeService.desactivateEmployee(anyString());
        });
    }

    @Test
    public void shouldUpdateEmployeeSalaryForIncrease() throws NotFoundException, InvalidPeriodException {

        // ARRANGE
        List<EmployeeHistory> histories = new ArrayList<>();

        EmployeeHistory increaseHistory = new EmployeeHistory(EMPLOYEE_HISTORY_INCREASE_COMMENTARY);
        increaseHistory.setHistoryDate(EMPLOYEE_HISTORY_LOCAL_DATE);
        increaseHistory.setHistoryType(historyTypeIncrease);

        histories.add(increaseHistory);

        employee.setEmployeeHistories(histories);
        employee.setSalary(EMPLOYEE_STARTING_SALARY);

        LocalDate salaryDate = LocalDate.now();
        BigDecimal newSalary = EMPLOYEE_NEW_SALARY;

        // se crea el registro de un aumento salarial en el historial del empleado
        EmployeeHistory salaryIncreaseHistory = new EmployeeHistory(EMPLOYEE_NEW_SALARY_COMMENTARY);
        salaryIncreaseHistory.setHistoryDate(salaryDate);

        // se hace que el ultimo salario del empleado este vacio
        when(forEmployeeHistoryPort.getLastEmployeeSalaryUntilDate(employee, salaryDate))
            .thenReturn(Optional.empty());

        when(forEmployeeHistoryPort.createEmployeeHistorySalaryIncrease(employee, newSalary, salaryDate))
            .thenReturn(salaryIncreaseHistory);

        // se retorna el ultimo cambio de salario
        when(forEmployeeHistoryPort.getMostRecentEmployeeSalary(employee))
            .thenReturn(Optional.of(salaryIncreaseHistory));

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        // ACT
        Employee updatedEmployee = employeeService.updateEmployeeSalary(employee.getId(), newSalary, salaryDate);


        // ASSERT
        assertAll(
            () -> assertEquals(newSalary, updatedEmployee.getSalary()),
            () -> assertEquals(2, updatedEmployee.getEmployeeHistories().size()),
            () -> assertEquals(salaryIncreaseHistory, updatedEmployee.getEmployeeHistories().get(1))
        );

        verify(forEmployeeHistoryPort, times(1))
            .createEmployeeHistorySalaryIncrease(employee, newSalary, salaryDate);
        verify(forEmployeeHistoryPort, times(1)).getMostRecentEmployeeSalary(employee);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void shouldUpdateEmployeeSalaryForDecrease() throws NotFoundException, InvalidPeriodException {

        // ARRANGE
        List<EmployeeHistory> histories = new ArrayList<>();

        EmployeeHistory decreaseHistory = new EmployeeHistory(EMPLOYEE_HISTORY_DECREASE_COMMENTARY);
        decreaseHistory.setHistoryDate(EMPLOYEE_HISTORY_LOCAL_DATE);
        decreaseHistory.setHistoryType(historyTypeDecrease);

        histories.add(decreaseHistory);

        employee.setEmployeeHistories(histories);
        employee.setSalary(EMPLOYEE_STARTING_DECREASE_SALARY);

        LocalDate salaryDate = LocalDate.now();
        BigDecimal newSalary = EMPLOYEE_NEW_SALARY_DECREASE;

        // se crea el registro de un aumento salarial en el historial del empleado
        EmployeeHistory salaryDecreaseHistory = new EmployeeHistory(EMPLOYEE_NEW_SALARY_DECREASE_COMMENTARY);
        salaryDecreaseHistory.setHistoryDate(salaryDate);

        // se hace que el ultimo salario del empleado este vacio
        when(forEmployeeHistoryPort.getLastEmployeeSalaryUntilDate(employee, salaryDate))
            .thenReturn(Optional.empty());

        when(forEmployeeHistoryPort.createEmployeeHistorySalaryDecrease(employee, newSalary, salaryDate))
            .thenReturn(salaryDecreaseHistory);

        // se retorna el ultimo cambio de salario
        when(forEmployeeHistoryPort.getMostRecentEmployeeSalary(employee))
            .thenReturn(Optional.of(salaryDecreaseHistory));

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        // ACT
        Employee updatedEmployee = employeeService.updateEmployeeSalary(employee.getId(), newSalary, salaryDate);


        // ASSERT
        assertAll(
            () -> assertEquals(newSalary, updatedEmployee.getSalary()),
            () -> assertEquals(2, updatedEmployee.getEmployeeHistories().size()),
            () -> assertEquals(salaryDecreaseHistory, updatedEmployee.getEmployeeHistories().get(1))
        );

        verify(forEmployeeHistoryPort, times(1))
            .createEmployeeHistorySalaryDecrease(employee, newSalary, salaryDate);
        verify(forEmployeeHistoryPort, times(1)).getMostRecentEmployeeSalary(employee);
        verify(employeeRepository, times(1)).save(employee);
    }
}
