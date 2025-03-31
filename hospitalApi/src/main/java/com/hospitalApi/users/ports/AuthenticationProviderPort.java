package com.hospitalApi.users.ports;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface AuthenticationProviderPort {

    /**
     * Obtiene el empleado autenticado actualmente en el sistema.
     *
     * @return el objeto Employee correspondiente al usuario autenticado.
     * @throws IllegalStateException si no hay ningún usuario autenticado en el
     *                               contexto.
     * @throws NotFoundException     si no se encuentra un empleado asociado al
     *                               nombre de usuario autenticado.
     */
    public Employee getAutenticatedEmployee() throws IllegalStateException, NotFoundException;
}
