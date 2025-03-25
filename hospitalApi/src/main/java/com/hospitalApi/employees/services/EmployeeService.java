package com.hospitalApi.employees.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeHistory;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.models.HistoryType;
import com.hospitalApi.employees.ports.ForEmployeeHistoryPort;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.employees.ports.ForHistoryTypePort;
import com.hospitalApi.employees.repositories.EmployeeRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.ports.ForUsersPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService implements ForEmployeesPort {

    private final EmployeeRepository employeeRepository;
    private final ForEmployeeTypePort forEmployeeTypePort;
    private final ForEmployeeHistoryPort forEmployeeHistoryPort;
    private final ForUsersPort userService;

    @Transactional(rollbackOn = Exception.class)
    public Employee createEmployee(Employee newEmployee, EmployeeType employeeType, User newUser, EmployeeHistory employeeHistoryDate)
            throws DuplicatedEntryException, NotFoundException {
        // veficar que el tipo de empleado si exista
        forEmployeeTypePort.verifyExistsEmployeeTypeById(employeeType.getId());
        // mandar a guardar el usuario
        User user = userService.createUser(newUser);

        // crea el primer registro del empleado en el historial (su contratacion)
        EmployeeHistory createdEmployeeHistory = forEmployeeHistoryPort.createEmployeeHistoryHiring(newEmployee, employeeHistoryDate.getHistoryDate());

        // guardar el empledo
        newEmployee.setUser(user);
        newEmployee.setEmployeeType(employeeType);
        ArrayList<EmployeeHistory> employeeHistories = new ArrayList<>();
        employeeHistories.add(createdEmployeeHistory);
        newEmployee.setEmployeeHistories(employeeHistories);
        user.setEmployee(newEmployee);

        // guardar el historial del empleado inicial
        return employeeRepository.save(newEmployee);
    }

    @Transactional(rollbackOn = Exception.class)
    public Employee updateEmployee(String currentId, Employee newData, EmployeeType employeeType)
            throws NotFoundException {
        // traer el empleado por id
        Employee currentEmployee = findEmployeeById(currentId);

        // verificar que el tipo de empleado sí exista; si lanza excepción, entonces no
        // existe
        forEmployeeTypePort.verifyExistsEmployeeTypeById(employeeType.getId());

        // editar el empleado existente con la información de newData
        currentEmployee.setFirstName(newData.getFirstName());
        currentEmployee.setLastName(newData.getLastName());
        currentEmployee.setSalary(newData.getSalary());
        currentEmployee.setIgssPercentage(newData.getIgssPercentage());
        currentEmployee.setIrtraPercentage(newData.getIrtraPercentage());
        currentEmployee.setEmployeeType(employeeType);

        return employeeRepository.save(currentEmployee);
    }

    @Transactional(rollbackOn = Exception.class)
    public Employee updateEmployeeSalary(String currentId, BigDecimal newSalary, LocalDate salaryDate)
            throws NotFoundException, InvalidPeriodException {
        Employee currentEmployee = findEmployeeById(currentId);

        List<EmployeeHistory> employeeHistories = currentEmployee.getEmployeeHistories();

        BigDecimal comparisonSalary = currentEmployee.getSalary();

        // se verifica si el salario aumento o disminuyo (para la fecha indicada) y se registra en el historial del empleado
        Optional<EmployeeHistory> currentSalaryUntilDateHistory = forEmployeeHistoryPort
            .getLastEmployeeSalaryUntilDate(currentEmployee, salaryDate);

        // si ya se modifico el salario anteriormente (a la fecha) se usa ese registro
        if (currentSalaryUntilDateHistory.isPresent()) {
            comparisonSalary = new BigDecimal(currentSalaryUntilDateHistory.get().getCommentary());
        } else {
            // si no hay se utiliza el primer salario que se le dio, el de contratacion
            EmployeeHistory hiring = currentEmployee.getEmployeeHistories().get(0);
            String hiringCommentary = hiring.getCommentary();
            BigDecimal hiringSalary = new BigDecimal(hiringCommentary.substring(hiringCommentary.indexOf("Q.") + 2).trim());
            comparisonSalary = hiringSalary;
        }

        if (comparisonSalary.compareTo(newSalary) == -1) {
            // aumento
            EmployeeHistory createdEmployeeHistory = forEmployeeHistoryPort
                .createEmployeeHistorySalaryIncrease(currentEmployee, newSalary, salaryDate);
            employeeHistories.add(createdEmployeeHistory);
        } else if (comparisonSalary.compareTo(newSalary) == 1) {
            // disminuyo
            EmployeeHistory createdEmployeeHistory = forEmployeeHistoryPort
                .createEmployeeHistorySalaryDecrease(currentEmployee, newSalary, salaryDate);
            employeeHistories.add(createdEmployeeHistory);
        }

        // guardar el historial del empleado
        currentEmployee.setEmployeeHistories(employeeHistories);

        // se usa el salario mas reciente segun el historial para asignarle al empleado
        Optional<EmployeeHistory> mostRecentEmployeeSalaryOptional = this.forEmployeeHistoryPort
            .getMostRecentEmployeeSalary(currentEmployee);
        if (mostRecentEmployeeSalaryOptional.isEmpty()) {
            // si esta vacio se usa el que se acaba de ingresar
            currentEmployee.setSalary(newSalary);
        } else {
            // si no esta vacio se usa el ultimo obtenido
            EmployeeHistory employeeHistory = mostRecentEmployeeSalaryOptional.get();
            currentEmployee.setSalary(new BigDecimal(employeeHistory.getCommentary()));
        }

        return employeeRepository.save(currentEmployee);
    }

    @Transactional(rollbackOn = Exception.class)
    public Employee desactivateEmployee(String currentId)
            throws NotFoundException, IllegalStateException {
        // traer el empleado por id
        Employee currentEmployee = findEmployeeById(currentId);

        // si ya esta desactivado entonces lanzamos error
        if (currentEmployee.getDesactivatedAt() != null) {
            // indicamos que se llamo el metodo en un momento inapropiado
            throw new IllegalStateException("El empleado ya está desactivado.");
        }
        // le cambiamos el estado a su usuario y al empleado como tal
        LocalDate desactivatedDate = LocalDate.now();
        currentEmployee.setDesactivatedAt(desactivatedDate);
        currentEmployee.getUser().setDesactivatedAt(desactivatedDate);
        return employeeRepository.save(currentEmployee);
    }

    public Employee findEmployeeById(String employeeId) throws NotFoundException {
        // manda a traer el employee si el optional esta vacio entonces retorna un
        // notfound exception
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new NotFoundException("El id especificado no pertenece a ningun empleado."));

        return employee;
    }

    public List<Employee> findEmployees() {
        // manda a traer el employee si el optional esta vacio entonces retorna un
        // notfound exception
        List<Employee> employees = employeeRepository.findAll();

        return employees;
    }
}
