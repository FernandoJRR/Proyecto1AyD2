package com.hospitalApi.employees.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.Value;

@Value
public class EmployeeResponseDTO {

    String firstName;

    String lastName;

    BigDecimal salary;

    BigDecimal igssPercentage;

    BigDecimal irtraPercentage;

    LocalDateTime resignDate;

}
