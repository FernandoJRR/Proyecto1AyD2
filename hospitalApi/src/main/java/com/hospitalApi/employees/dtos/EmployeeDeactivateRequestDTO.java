package com.hospitalApi.employees.dtos;

import java.time.LocalDate;

import com.hospitalApi.shared.dtos.IdRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EmployeeDeactivateRequestDTO {
    private LocalDate deactivationDate;

    @Valid
    @NotNull(message = "El motivo de desactivacion no puede ser nulo")
    private IdRequestDTO historyTypeId;
}
