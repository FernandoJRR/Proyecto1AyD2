package com.hospitalApi.vacations.dtos;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class CreateVacationsRequestDTO {
    List<VacationPeriodRequestDTO> periodsVacations;
}
