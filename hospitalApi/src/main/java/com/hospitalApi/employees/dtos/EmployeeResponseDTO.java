package com.hospitalApi.employees.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Value;

@Value
public class EmployeeResponseDTO {

    String id;

    String firstName;

    String lastName;

    BigDecimal salary;

    BigDecimal igssPercentage;

    BigDecimal irtraPercentage;

    LocalDate desactivatedAt;

    EmployeeTypeResponseDTO employeeType;

}
