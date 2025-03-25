package com.hospitalApi.employees.dtos;

import com.hospitalApi.users.dtos.CreateUserRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateEmployeeRequestDTO extends EmployeeRequestDTO {

    @Valid
    @NotNull(message = "El createUserRequestDTO no puede ser nulo")
    private CreateUserRequestDTO createUserRequestDTO;

    @Valid
    @NotNull(message = "El employeeHistoryDateRequestDTO no puede ser nulo")
    private EmployeeHistoryDateRequestDTO employeeHistoryDateRequestDTO;
}
