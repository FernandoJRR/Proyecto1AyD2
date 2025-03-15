package com.hospitalApi.employees.services;

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
public class EmployeeService implements ForEmployeesPort {

    private final EmployeeRepository employeeRepository;
    private final ForEmployeeTypePort forEmployeeTypePort;
    private final ForUsersPort userService;

    @Transactional(rollbackOn = Exception.class)
    public Employee createEmployee(Employee newEmployee, EmployeeType employeeType, User newUser)
            throws DuplicatedEntryException, NotFoundException {
        // veficar que el tipo de empleado si exista
        if (!forEmployeeTypePort.existsEmployeeTypeById(employeeType)) {
            throw new NotFoundException("El tipo del empleado especificado no existe.");
        }
        // mandar a guardar el usuario
        User user = userService.createUser(newUser);

        // guardar el empledo
        newEmployee.setUser(user);
        newEmployee.setEmployeeType(employeeType);
        user.setEmployee(newEmployee);

        // guardar el historial del empleado inicial
        return employeeRepository.save(newEmployee);
    }

}
