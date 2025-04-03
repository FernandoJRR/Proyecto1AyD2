package com.hospitalApi.vacations.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VacationPeriodRequestDTO {
    private LocalDate beginDate;
    private LocalDate endDate;
}
