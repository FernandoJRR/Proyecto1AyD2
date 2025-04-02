package com.hospitalApi.reports.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * Filtro para generar el reporte de asignación de doctores.
 * Permite especificar si se quiere incluir únicamente doctores asignados,
 * no asignados, o ambos.
 *
 * @param onlyAssigneds    si es true, solo se incluirán los médicos asignados a
 *                         pacientes.
 * @param onlyNotAssigneds si es true, solo se incluirán los médicos que no
 *                         están asignados a pacientes.
 */
@Value
public class DoctorAssignmentFilter {

    @NotNull(message = "Debes indicar si se desea filtrar solo médicos asignados.")
    Boolean onlyAssigneds;

    @NotNull(message = "Debes indicar si se desea filtrar solo médicos no asignados.")
    Boolean onlyNotAssigneds;
}
