package com.hospitalApi.employees.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.employees.repositories.EmployeeRepository;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.ports.ForUsersPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class EmployeeService implements ForEmployeesPort {

    private final EmployeeRepository employeeRepository;
    private final ForEmployeeTypePort forEmployeeTypePort;
    private final ForUsersPort userService;

    @Override
    public Employee createEmployee(Employee newEmployee, EmployeeType employeeType, User newUser)
            throws DuplicatedEntryException, NotFoundException {
        // veficar que el tipo de empleado si exista
        EmployeeType existingEmployeeType = forEmployeeTypePort.findEmployeeTypeById(employeeType.getId());
        // mandar a guardar el usuario
        User user = userService.createUser(newUser);

        // guardar el empledo
        newEmployee.setUser(user);
        newEmployee.setEmployeeType(existingEmployeeType);
        user.setEmployee(newEmployee);

        // guardar el historial del empleado inicial
        return employeeRepository.save(newEmployee);
    }

    @Override
    public Employee updateEmployee(String currentId, Employee newData, EmployeeType employeeType)
            throws NotFoundException {
        // traer el empleado por id
        Employee currentEmployee = findEmployeeById(currentId);

        // traer el tipo de empleado que se desea asignar
        EmployeeType existingEmployeeType = forEmployeeTypePort.findEmployeeTypeById(employeeType.getId());

        // editar el empleado existente con la información de newData
        currentEmployee.setFirstName(newData.getFirstName());
        currentEmployee.setLastName(newData.getLastName());
        currentEmployee.setSalary(newData.getSalary());
        currentEmployee.setIgssPercentage(newData.getIgssPercentage());
        currentEmployee.setIrtraPercentage(newData.getIrtraPercentage());
        currentEmployee.setEmployeeType(existingEmployeeType);

        return employeeRepository.save(currentEmployee);
    }

    @Override
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

    @Override
    public Employee reassignEmployeeType(String employeeId, String employeeTypeId) throws NotFoundException {
        Employee exisitingEmployee = findEmployeeById(employeeId);
        EmployeeType existinEmployeeType = forEmployeeTypePort.findEmployeeTypeById(employeeTypeId);
        exisitingEmployee.setEmployeeType(existinEmployeeType);
        return exisitingEmployee;
    }

    @Override
    public List<Employee> reassignEmployeeType(List<Employee> employeeIds, String employeeTypeId)
            throws NotFoundException {
        List<Employee> updatedEmployees = new ArrayList<>();
        for (Employee employeeId : employeeIds) {
            Employee reassignEmployeeType = reassignEmployeeType(employeeId.getId(), employeeTypeId);
            updatedEmployees.add(reassignEmployeeType);
        }
        return updatedEmployees;
    }

    @Override
    public Employee findEmployeeById(String employeeId) throws NotFoundException {
        String errorMessage = String.format("El id %s no pertenece a ningun empleado.", employeeId);
        // manda a traer el employee si el optional esta vacio entonces retorna un
        // notfound exception
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new NotFoundException(errorMessage));

        return employee;
    }

    @Override
    public List<Employee> findEmployees() {
        // manda a traer el employee si el optional esta vacio entonces retorna un
        // notfound exception
        List<Employee> employees = employeeRepository.findAll();

        return employees;
    }
}
