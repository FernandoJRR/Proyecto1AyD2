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
    DESACTIVATE_EMPLOYEE(new Permission("Desactivar empleado", "DEACTIVATE_EMPLOYEE")),
    FIND_EMPLOYEE_BY_ID(new Permission("Buscar empleado por ID", "FIND_EMPLOYEE_BY_ID")),
    FIND_ALL_EMPLOYEES(new Permission("Obtener todos los empleados", "FIND_ALL_EMPLOYEES")),

    // PERMISOS DE PACIENTE
    GET_ALL_PATIENTS(new Permission("Obtener todos los pacientes", "GET_ALL_PATIENTS")),
    GET_PATIENT_BY_ID(new Permission("Obtener paciente por ID", "GET_PATIENT_BY_ID")),
    GET_PATIENT_BY_DPI(new Permission("Obtener paciente por DPI", "GET_PATIENT_BY_DPI")),
    CREATE_PATIENT(new Permission("Crear paciente", "CREATE_PATIENT")),

    // PERMISOS PARA MEDICOS
    GET_ALL_MEDICINES(new Permission("Obtener todos los medicamentos", "GET_ALL_MEDICINES")),
    GET_LOW_STOCK_MEDICINES(new Permission("Obtener medicamentos con stock bajo", "GET_LOW_STOCK_MEDICINES")),
    GET_MEDICINE_BY_ID(new Permission("Obtener medicamento por ID", "GET_MEDICINE_BY_ID")),
    CREATE_MEDICINE(new Permission("Crear medicamento", "CREATE_MEDICINE")),
    EDIT_MEDICINE(new Permission("Editar medicamento", "EDIT_MEDICINE")),
    DELETE_MEDICINE(new Permission("Eliminar medicamento", "DELETE_MEDICINE")),

    // PERMISOS PARA SALAS
    CREATE_SALE_MEDICINE_FARMACIA(
            new Permission("Registrar venta de medicamento en farmacia", "CREATE_SALE_MEDICINE_FARMACIA")),
    CREATE_SALE_MEDICINE_CONSULT(
            new Permission("Registrar venta de medicamento en consultorio", "CREATE_SALE_MEDICINE_CONSULT")),
    GET_SALE_MEDICINE_BY_ID(new Permission("Obtener venta de medicamento por ID", "GET_SALE_MEDICINE_BY_ID")),

    // PARA TIPOS DE EMPLEADO

    CREATE_EMPLOYEE_TYPE(new Permission("Crear tipo de empleado", "CREATE_EMPLOYEE_TYPE")),
    EDIT_EMPLOYEE_TYPE(new Permission("Editar tipo de empleado", "EDIT_EMPLOYEE_TYPE")),
    DELETE_EMPLOYEE_TYPE(new Permission("Eliminar tipo de empleado", "DELETE_EMPLOYEE_TYPE")),
    GET_ALL_EMPLOYEE_TYPES(new Permission("Obtener todos los tipos de empleado", "GET_ALL_EMPLOYEE_TYPES")),
    GET_EMPLOYEE_TYPE_BY_ID(new Permission("Obtener tipo de empleado por ID", "GET_EMPLOYEE_TYPE_BY_ID"));

    private final Permission permission;
}
