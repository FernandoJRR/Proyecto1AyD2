package com.hospitalApi.vacations.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.parameters.enums.ParameterEnum;
import com.hospitalApi.parameters.models.Parameter;
import com.hospitalApi.parameters.ports.ForParameterPort;
import com.hospitalApi.shared.exceptions.InvalidPeriodException;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.vacations.dtos.CreateVacationsRequestDTO;
import com.hospitalApi.vacations.dtos.VacationDaysResponseDTO;
import com.hospitalApi.vacations.dtos.VacationsResponseDTO;
import com.hospitalApi.vacations.mappers.VacationsMapper;
import com.hospitalApi.vacations.models.Vacations;
import com.hospitalApi.vacations.ports.ForVacationsPort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/vacations")
@RequiredArgsConstructor
public class VacationsController {

    private final ForVacationsPort vacationsPort;
    private final ForParameterPort parameterPort;
    private final VacationsMapper vacationsMapper;

    @Operation(summary = "Obtener los dias de vacaciones que el sistema tiene configurados",
        description = "Devuelve el valor en dias que el sistema tiene configurado de vacaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dias de vacaciones obtenidas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/vacation-days")
    @ResponseStatus(HttpStatus.OK)
    public VacationDaysResponseDTO getVacationDays()
            throws NotFoundException {

        Parameter vacationDays = parameterPort.findParameterByKey(ParameterEnum.DIAS_VACACIONES.getKey());
        VacationDaysResponseDTO response = new VacationDaysResponseDTO(Integer.parseInt(vacationDays.getValue()));
        return response;
    }

    @Operation(summary = "Obtener todos las vacaciones de un empleado en un periodo",
        description = "Devuelve la lista de las vacaciones de un empleado en un periodo especifico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vacaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{employeeId}/{periodYear}")
    @ResponseStatus(HttpStatus.OK)
    public List<VacationsResponseDTO> getAllVacationsForEmployeeOnPeriod(
            @PathVariable("employeeId") @NotBlank(message = "El id del empleado es obligatorio") String employeeId,
            @PathVariable("periodYear") @NotBlank(message = "El periodo de las vacaciones es obligatorio") @Positive(message = "El periodo de las vacaciones debe ser un numero entero") Integer periodYear)
            throws NotFoundException {

        List<Vacations> vacations = vacationsPort.getAllVacationsForEmployeeOnPeriod(employeeId, periodYear);
        List<VacationsResponseDTO> response = vacationsMapper
                .fromVacationsListToVacationsResponseDTOs(vacations);
        return response;
    }

    @Operation(summary = "Crea las vacaciones de un empleado",
        description = "Crea las vacaciones de un empleado a partir de un periodo y varias fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacaciones creadas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{employeeId}/{periodYear}")
    @ResponseStatus(HttpStatus.OK)
    public List<VacationsResponseDTO> createVacationsForEmployeeOnPeriod(
            @PathVariable("employeeId") @NotBlank(message = "El id del empleado es obligatorio") String employeeId,
            @PathVariable("periodYear") @NotBlank(message = "El periodo de las vacaciones es obligatorio") @Positive(message = "El periodo de las vacaciones debe ser un numero entero") Integer periodYear,
            @RequestBody @Valid List<CreateVacationsRequestDTO> createVacationsRequestDTO
            )
            throws NotFoundException, InvalidPeriodException {

        List<Vacations> vacations = vacationsMapper.fromVacationsRequestToVacationsList(createVacationsRequestDTO);
        List<Vacations> createdVacations = vacationsPort.createVacationsForEmployeeOnPeriod(employeeId, periodYear, vacations);
        List<VacationsResponseDTO> response = vacationsMapper
                .fromVacationsListToVacationsResponseDTOs(createdVacations);
        return response;
    }

    @Operation(summary = "Obtener todos las vacaciones de un empleado en un periodo",
        description = "Devuelve la lista de las vacaciones de un empleado en un periodo especifico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vacaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<Integer, List<VacationsResponseDTO>> getAllVacationsForEmployee(
            @PathVariable("employeeId") @NotBlank(message = "El id del empleado es obligatorio") String employeeId)
            throws NotFoundException {

        Map<Integer, List<Vacations>> vacations = vacationsPort.getAllVacationsForEmployee(employeeId);

        Map<Integer, List<VacationsResponseDTO>> response = vacationsMapper
                .fromVacationMapToVacationMapResponse(vacations);

        return response;
    }

}
