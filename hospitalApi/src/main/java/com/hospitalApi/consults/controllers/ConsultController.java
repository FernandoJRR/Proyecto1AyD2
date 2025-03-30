package com.hospitalApi.consults.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalApi.consults.dtos.AddDeleteEmployeeConsultRequestDTO;
import com.hospitalApi.consults.dtos.ConsultResponseDTO;
import com.hospitalApi.consults.dtos.ConsutlFilterDTO;
import com.hospitalApi.consults.dtos.CreateConsultRequestDTO;
import com.hospitalApi.consults.dtos.EmployeeConsultResponseDTO;
import com.hospitalApi.consults.dtos.TotalConsultResponseDTO;
import com.hospitalApi.consults.dtos.UpdateConsultRequestDTO;
import com.hospitalApi.consults.mappers.ConsultMapper;
import com.hospitalApi.consults.mappers.EmployeeConsultMapper;
import com.hospitalApi.consults.models.Consult;
import com.hospitalApi.consults.models.EmployeeConsult;
import com.hospitalApi.consults.port.ForConsultPort;
import com.hospitalApi.consults.port.ForEmployeeConsultPort;
import com.hospitalApi.shared.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/consults")
@RequiredArgsConstructor
public class ConsultController {

        private final ForConsultPort consultPort;
        private final ConsultMapper consultMapper;
        private final EmployeeConsultMapper employeeConsultMapper;
        private final ForEmployeeConsultPort employeeConsultPort;
        // private final ConsultMapper consultMapper;

        @Operation(summary = "Obtener todas las consultas", description = "Este endpoint devuelve una lista con todas las consultas registradas en el sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Consultas obtenidas exitosamente"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/all")
        @ResponseStatus(HttpStatus.OK)
        public List<ConsultResponseDTO> getAllConsults(
                        @ModelAttribute ConsutlFilterDTO consultFilterDTO) {
                List<Consult> consults = consultPort.getConsults(consultFilterDTO);
                List<ConsultResponseDTO> consultsResponse = consultMapper.fromConsultsToResponse(consults);
                return consultsResponse;
        }

        @Operation(summary = "Obtener una consulta por ID", description = "Este endpoint permite obtener una consulta por su identificador único.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Consulta obtenida exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Consulta no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public ConsultResponseDTO getConsult(
                        @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
                        throws NotFoundException {
                Consult consult = consultPort.findById(id);
                ConsultResponseDTO consultResponse = consultMapper.fromConsultToResponse(consult);
                return consultResponse;
        }

        @Operation(summary = "Crear una nueva consulta", description = "Este endpoint permite registrar una nueva consulta para un paciente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Consulta creada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, error en los datos de entrada"),
                        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping("/create")
        @ResponseStatus(HttpStatus.CREATED)
        public ConsultResponseDTO createConsult(
                        @RequestBody @Valid CreateConsultRequestDTO createConsultRequestDTO)
                        throws NotFoundException {

                Consult consult = consultPort.createConsult(createConsultRequestDTO.getPatientId(),
                                createConsultRequestDTO.getEmployeeId(),
                                createConsultRequestDTO.getCostoConsulta());
                ConsultResponseDTO consultResponse = consultMapper.fromConsultToResponse(consult);
                return consultResponse;
        }

        @Operation(summary = "Actualizar una consulta", description = "Este endpoint permite actualizar la información de una consulta existente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Consulta actualizada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida, error en los datos de entrada"),
                        @ApiResponse(responseCode = "404", description = "Consulta no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PatchMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public ConsultResponseDTO updateConsult(
                        @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id,
                        @RequestBody @Valid UpdateConsultRequestDTO updateConsultRequestDTO)
                        throws NotFoundException {
                Consult consult = consultPort.updateConsult(id, updateConsultRequestDTO);
                return consultMapper.fromConsultToResponse(consult);
        }

        @Operation(summary = "Pagar una consulta", description = "Este endpoint permite pagar una consulta, cambiando su estado a pagada.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Consulta pagada exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Consulta no encontrada"),
                        @ApiResponse(responseCode = "409", description = "La consulta ya se encuentra pagada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping("/pay/{id}")
        @ResponseStatus(HttpStatus.OK)
        public ConsultResponseDTO payConsult(
                        @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
                        throws NotFoundException, IllegalStateException {
                Consult consult = consultPort.pagarConsulta(id);
                return consultMapper.fromConsultToResponse(consult);
        }

        @Operation(summary = "Obtener el total de una consulta", description = "Este endpoint devuelve el costo total de una consulta incluyendo posibles cirugías.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Total obtenido exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Consulta no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/total/{id}")
        @ResponseStatus(HttpStatus.OK)
        public TotalConsultResponseDTO getTotalConsult(
                        @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
                        throws NotFoundException {
                Double total = consultPort.obtenerTotalConsulta(id);
                return new TotalConsultResponseDTO(id, total);
        }

        @Operation(summary = "Obtener empleados asignados a una consulta", description = "Este endpoint devuelve una lista de empleados asignados a una consulta específica.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empleados obtenidos exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Consulta no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}/employees")
        @ResponseStatus(HttpStatus.OK)
        public List<EmployeeConsultResponseDTO> getEmployeeConsultsByConsultId(
                        @PathVariable @NotNull(message = "El id de la consulta no puede ser nulo") String id)
                        throws NotFoundException {
                List<EmployeeConsult> employeeConsults = employeeConsultPort.getEmployeeConsultsByConsultId(id);
                List<EmployeeConsultResponseDTO> employeeConsultsResponse = employeeConsultMapper
                                .fromEmployeeConsultsToResponse(employeeConsults);
                return employeeConsultsResponse;
        }

        @Operation(summary = "Agregar empleado a una consulta", description = "Este endpoint permite agregar un empleado a una consulta existente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empleado agregado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Consulta o Empleado no encontrado"),
                        @ApiResponse(responseCode = "409", description = "El empleado ya esta asignado a la consulta"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping("add-employee")
        @ResponseStatus(HttpStatus.OK)
        public List<EmployeeConsultResponseDTO> addEmployeeToConsult(
                        @RequestBody @Valid AddDeleteEmployeeConsultRequestDTO addEmployeeConsultRequestDTO)
                        throws NotFoundException, IllegalStateException {
                Consult consult = consultPort.findById(addEmployeeConsultRequestDTO.getConsultId());
                List<EmployeeConsult> employeeConsults = employeeConsultPort
                                .addEmployeeConsultsByConsultIdAndEmployeeId(
                                                consult,
                                                addEmployeeConsultRequestDTO.getEmployeeId());
                List<EmployeeConsultResponseDTO> employeeConsultsResponse = employeeConsultMapper
                                .fromEmployeeConsultsToResponse(employeeConsults);
                return employeeConsultsResponse;
        }

        @Operation(summary = "Eliminar empleado de una consulta", description = "Este endpoint permite eliminar un empleado de una consulta existente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empleado eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Consulta o Empleado no encontrado"),
                        @ApiResponse(responseCode = "409", description = "El empleado no está asignado a la consulta o es el único empleado asignado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @DeleteMapping("delete-employee")
        @ResponseStatus(HttpStatus.OK)
        public List<EmployeeConsultResponseDTO> deleteEmployeeFromConsult(
                        @RequestBody @Valid AddDeleteEmployeeConsultRequestDTO addEmployeeConsultRequestDTO)
                        throws NotFoundException, IllegalStateException {
                List<EmployeeConsult> employeeConsults = employeeConsultPort
                                .deleteEmployeeConsultsByConsultIdAndEmployeeId(
                                                addEmployeeConsultRequestDTO.getConsultId(),
                                                addEmployeeConsultRequestDTO.getEmployeeId());
                List<EmployeeConsultResponseDTO> employeeConsultsResponse = employeeConsultMapper
                                .fromEmployeeConsultsToResponse(employeeConsults);
                return employeeConsultsResponse;
        }
}
