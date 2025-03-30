package com.hospitalApi.permissions.enums;

import com.hospitalApi.permissions.models.Permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemPermissionEnum {

        // PERMISOS DE EMPLEADOS
        CREATE_EMPLOYEE(new Permission("Crear empleado", "CREATE_EMPLOYEE")),
        EDIT_EMPLOYEE(new Permission("Editar empleado", "EDIT_EMPLOYEE")),
        DESACTIVATE_EMPLOYEE(new Permission("Desactivar empleado", "DESACTIVATE_EMPLOYEE")),
        RESACTIVATE_EMPLOYEE(new Permission("Reactivar empleado", "RESACTIVATE_EMPLOYEE")),

        // PERMISOS DE PACIENTE
        CREATE_PATIENT(new Permission("Crear paciente", "CREATE_PATIENT")),

        // PERMISOS PARA MEDICOS
        CREATE_MEDICINE(new Permission("Crear medicamento", "CREATE_MEDICINE")),
        EDIT_MEDICINE(new Permission("Editar medicamento", "EDIT_MEDICINE")),
        DELETE_MEDICINE(new Permission("Eliminar medicamento", "DELETE_MEDICINE")),

        // PERMISOS PARA SALAS
        CREATE_SALE_MEDICINE_FARMACIA(
                        new Permission("Registrar venta de medicamento en farmacia", 
                        "CREATE_SALE_MEDICINE_FARMACIA")),
        CREATE_SALE_MEDICINE_CONSULT(
                        new Permission("Registrar venta de medicamento en consultorio",
                                        "CREATE_SALE_MEDICINE_CONSULT")),

        // PARA TIPOS DE EMPLEADO

        CREATE_EMPLOYEE_TYPE(new Permission("Crear tipo de empleado", "CREATE_EMPLOYEE_TYPE")),
        EDIT_EMPLOYEE_TYPE(new Permission("Editar tipo de empleado", "EDIT_EMPLOYEE_TYPE")),
        DELETE_EMPLOYEE_TYPE(new Permission("Eliminar tipo de empleado", "DELETE_EMPLOYEE_TYPE")),

        // para las habitaciones
        CREATE_ROOM(new Permission("Crear habitacion", "CREATE_ROOM")),
        EDIT_ROOM(new Permission("Editar habitacion", "EDIT_ROOM")),
        TOGGLE_ROOM_AVAILABILITY(
                        new Permission("Alternar disponibilidad de la habitación", "TOGGLE_ROOM_AVAILABILITY"));

        private final Permission permission;
}
