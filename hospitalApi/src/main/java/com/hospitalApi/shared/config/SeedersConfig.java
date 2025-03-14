package com.hospitalApi.shared.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.repositories.EmployeeRepository;
import com.hospitalApi.employees.repositories.EmployeeTypeRepository;
import com.hospitalApi.shared.utils.PasswordEncoderUtil;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Profile("dev") // Solo se ejecutará si el perfil es "dev"
@RequiredArgsConstructor
@Component
public class SeedersConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeTypeRepository employeeTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoderUtil passwordEncoderUtil;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void run(String... args) throws Exception {

        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("Ejecutando seeders.");
        // creamos el tipo de usuario
        EmployeeType newEmployeeType = new EmployeeType("USER");
        employeeTypeRepository.save(newEmployeeType);

        // creamos el usuario admin
        User newUser = new User("admin", passwordEncoderUtil.encode("admin"));

        // creamos un nuevo empleado
        Employee newEmployee = new Employee(null, null, null, null, null, null,
                newEmployeeType, newUser);

        newUser.setEmployee(newEmployee);

        employeeRepository.save(newEmployee);
        userRepository.save(newUser);

    }

}
