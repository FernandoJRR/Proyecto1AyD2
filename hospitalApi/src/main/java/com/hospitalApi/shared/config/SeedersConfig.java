package com.hospitalApi.shared.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.employees.models.EmployeeType;
import com.hospitalApi.employees.ports.ForEmployeeTypePort;
import com.hospitalApi.employees.ports.ForEmployeesPort;
import com.hospitalApi.permissions.enums.SystemPermissionEnum;
import com.hospitalApi.permissions.models.Permission;
import com.hospitalApi.permissions.ports.ForPermissionsPort;
import com.hospitalApi.permissions.repositories.PermissionRepository;
import com.hospitalApi.shared.enums.EmployeeTypeEnum;
import com.hospitalApi.users.models.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Profile("dev") // Solo se ejecutará si el perfil es "dev"
@RequiredArgsConstructor
@Component
public class SeedersConfig implements CommandLineRunner {

        private final ForEmployeeTypePort forEmployeeTypePort;
        private final ForPermissionsPort forPermissionsPort;
        private final ForEmployeesPort forEmployeesPort;

        private final PermissionRepository permissionRepository;

        @Override
        @Transactional(rollbackOn = Exception.class)
        public void run(String... args) throws Exception {

                if (permissionRepository.count() > 0) {
                        return;
                }

                System.out.println("Ejecutando seeders.");
                // en este array guardaremos los pemrisos creados
                List<Permission> createdPermissions = new ArrayList<>();

                // cremos los permisos
                for (SystemPermissionEnum permissionEnum : SystemPermissionEnum.values()) {
                        Permission createdPermission = forPermissionsPort
                                        .createPermission(permissionEnum.getPermission());
                        createdPermissions.add(createdPermission);
                }

                // mandamos a crear el tipo de empleado admin
                EmployeeType adminEmployeeType = forEmployeeTypePort.createEmployeeType(
                                EmployeeTypeEnum.ADMIN.getEmployeeType(),
                                createdPermissions);

                //creamos el sin asignar sin permisos
                forEmployeeTypePort.createEmployeeType(
                                EmployeeTypeEnum.DEFAULT.getEmployeeType(),
                                List.of());

                // creamos el nuevo empleado adminstrador
                // creamos un nuevo empleado
                Employee newEmployee = new Employee("Luis", "Monterroso", new BigDecimal(2000), 
                new BigDecimal(10), new BigDecimal(10), null,
                                adminEmployeeType, null);
                // creamos el usuario admin
                User userAdmin = new User("admin", "admin");

                forEmployeesPort.createEmployee(newEmployee, adminEmployeeType, userAdmin);
        }

}
