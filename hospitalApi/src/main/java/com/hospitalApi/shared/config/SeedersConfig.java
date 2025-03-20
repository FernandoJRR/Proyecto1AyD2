package com.hospitalApi.shared.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.permissions.ports.ForPermissionsPort;
import com.hospitalApi.shared.enums.EmployeeTypeEnum;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Profile("dev") // Solo se ejecutará si el perfil es "dev"
@RequiredArgsConstructor
@Component
public class SeedersConfig implements CommandLineRunner {

        private final ForEmployeeTypePort forEmployeeTypePort;
        private final ForPermissionsPort forPermissionsPort;
        private final ForEmployeesPort forEmployeesPort;

        private final UserRepository userRepository;

        @Override
        @Transactional(rollbackOn = Exception.class)
        public void run(String... args) throws Exception {

                if (userRepository.count() > 0) {
                        return;
                }

                System.out.println("Ejecutando seeders.");
                // cremos los permisos
                Permission createEmployeesPermission = forPermissionsPort
                                .createPermission(new Permission("Crear Empleados", "CREAR_EMPLEADOS"));
                Permission editEmployeesPermission = forPermissionsPort
                                .createPermission(new Permission("Editar Empleados", "EDITAR_EMPLEADOS"));

                // mandamos a crear el nuevo tipo de empleado

                EmployeeType adminEmployeeType = forEmployeeTypePort.createEmployeeType(
                                EmployeeTypeEnum.ADMIN.getEmployeeType(),
                                List.of(createEmployeesPermission, editEmployeesPermission));
                EmployeeType defauEmployeeType = forEmployeeTypePort.createEmployeeType(
                                EmployeeTypeEnum.DEFAULT.getEmployeeType(),
                                List.of(createEmployeesPermission, editEmployeesPermission));

                // creamos el nuevo empleado adminstrador
                // creamos un nuevo empleado
                Employee newEmployee = new Employee(null, null, null, null, null, null,
                                adminEmployeeType, null);
                // creamos el usuario admin
                User userAdmin = new User("admin", "admin");

                forEmployeesPort.createEmployee(newEmployee, adminEmployeeType, userAdmin);
        }

}
