package com.hospitalApi.employees.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Value;

@Value
public class EmployeeResponseDTO {

    String id;

    String firstName;

    String lastName;

    BigDecimal salary;

    BigDecimal igssPercentage;

    BigDecimal irtraPercentage;

    //LocalDateTime resignDate;

    EmployeeTypeResponseDTO employeeType;

    EmployeeHistoriesResponseDTO employeeHistoryResponseDTO;
}
