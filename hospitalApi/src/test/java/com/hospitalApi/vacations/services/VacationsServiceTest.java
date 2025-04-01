package com.hospitalApi.vacations.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospitalApi.employees.models.Employee;
import com.hospitalApi.parameters.enums.ParameterEnum;
import com.hospitalApi.parameters.models.Parameter;
import com.hospitalApi.parameters.repositories.ParameterRepository;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.vacations.models.Vacations;
import com.hospitalApi.vacations.repositories.VacationsRepository;

@ExtendWith(MockitoExtension.class)
public class VacationsServiceTest {

    Employee employee;
    Parameter parameter;

    @Mock
    private VacationsRepository vacationsRepository;
    @Mock
    private ParameterRepository parameterRepository;
    @Mock
    private com.hospitalApi.employees.repositories.EmployeeRepository employeeRepository;

    @InjectMocks
    private VacationsService vacationsService;

    private static final String EMPLOYEE_ID = "fdfd-oier-dkmf";
    private static final String PARAM_KEY_DIAS_VACACIONES = ParameterEnum.DIAS_VACACIONES.getKey();
    private static final String PARAM_VALUE_DIAS_VACACIONES = ParameterEnum.DIAS_VACACIONES.getDefaultValue();
    private static final int DAYS_VACATIONS = Integer.parseInt(PARAM_VALUE_DIAS_VACACIONES);
    private static final int CURRENT_YEAR = LocalDate.now().getYear();
    private static final int NEXT_YEAR = LocalDate.now().getYear() + 1;
    private static final int PERIOD_VACATIONS = 2023;


    @BeforeEach
    private void setUp() {
        employee = new Employee();
        employee.setId(EMPLOYEE_ID);

        parameter = new Parameter();
        parameter.setParameterKey(PARAM_KEY_DIAS_VACACIONES);
        parameter.setValue(PARAM_VALUE_DIAS_VACACIONES);
    }

    @Test
    public void shouldCreateRandomVacationForEmployeeInDecember() throws NotFoundException {
        // ARRANGE
        when(parameterRepository.findOneByParameterKey(PARAM_KEY_DIAS_VACACIONES))
            .thenReturn(Optional.of(parameter));
        when(employeeRepository.findById(EMPLOYEE_ID))
            .thenReturn(Optional.of(employee));
        when(vacationsRepository.save(any(Vacations.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        List<Vacations> result = vacationsService.createRandomVacationsForEmployee(EMPLOYEE_ID);

        ArgumentCaptor<Vacations> vacationCaptor = ArgumentCaptor.forClass(Vacations.class);
        verify(vacationsRepository, times(1)).save(vacationCaptor.capture());
        Vacations capturedVacation = vacationCaptor.getValue();

        // ASSERT
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertNotNull(capturedVacation),
            () -> assertEquals(CURRENT_YEAR, capturedVacation.getPeriodYear()),
            () -> assertEquals(12, capturedVacation.getBeginDate().getMonthValue()),
            () -> assertEquals(12, capturedVacation.getEndDate().getMonthValue()),
            () -> assertEquals(DAYS_VACATIONS, capturedVacation.getWorkingDays())
        );

        verify(parameterRepository, times(1)).findOneByParameterKey(PARAM_KEY_DIAS_VACACIONES);
        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
        verify(vacationsRepository, times(1)).save(any(Vacations.class));
    }

    @Test
    public void shouldCreateVacationPeriodForNextYearForEmployee() throws NotFoundException {
        // ARRANGE
        when(employeeRepository.findById(EMPLOYEE_ID))
             .thenReturn(Optional.of(employee));
        when(vacationsRepository.save(any(Vacations.class)))
             .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        List<Vacations> result = vacationsService.createVacationPeriodForNextYear(EMPLOYEE_ID, DAYS_VACATIONS);

        ArgumentCaptor<Vacations> vacationCaptor = ArgumentCaptor.forClass(Vacations.class);
        verify(vacationsRepository, times(1)).save(vacationCaptor.capture());
        Vacations capturedVacation = vacationCaptor.getValue();

        // ASSERT
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertNotNull(capturedVacation),
            () -> assertEquals(NEXT_YEAR, capturedVacation.getPeriodYear()),
            () -> assertEquals(1, capturedVacation.getBeginDate().getMonthValue()),
            () -> assertEquals(DAYS_VACATIONS, capturedVacation.getWorkingDays()),
            () -> assertEquals(employee, capturedVacation.getEmployee())
        );

        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
    }

    @Test
    public void shouldCreateVacationsForEmployeeOnPeriodSuccessfully() throws Exception {
        // ARRANGE
        when(employeeRepository.findById(EMPLOYEE_ID))
            .thenReturn(Optional.of(employee));
        when(parameterRepository.findOneByParameterKey(PARAM_KEY_DIAS_VACACIONES))
            .thenReturn(Optional.of(parameter));
        when(vacationsRepository.save(any(Vacations.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Vacations vac1 = new Vacations();
        vac1.setBeginDate(LocalDate.of(CURRENT_YEAR, 1, 6)); // Mismatched year → will cause areValidVacationPeriods to return false
        vac1.setEndDate(LocalDate.of(CURRENT_YEAR, 1, 10));   // 5 working days

        Vacations vac2 = new Vacations();
        vac2.setBeginDate(LocalDate.of(CURRENT_YEAR, 1, 13)); // Valid dates for PERIOD 2025
        vac2.setEndDate(LocalDate.of(CURRENT_YEAR, 1, 17));   // 5 working days

        Vacations vac3 = new Vacations();
        vac3.setBeginDate(LocalDate.of(CURRENT_YEAR, 1, 20)); // Valid dates for PERIOD 2025
        vac3.setEndDate(LocalDate.of(CURRENT_YEAR, 1, 24));  // 5 working days

        List<Vacations> inputVacations = Arrays.asList(vac1, vac2, vac3);

        when(vacationsRepository.findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(EMPLOYEE_ID, CURRENT_YEAR))
            .thenReturn(inputVacations);

        // ACT
        List<Vacations> result = vacationsService.createVacationsForEmployeeOnPeriod(EMPLOYEE_ID, CURRENT_YEAR, inputVacations);

        ArgumentCaptor<Vacations> vacationCaptor = ArgumentCaptor.forClass(Vacations.class);
        verify(vacationsRepository, times(inputVacations.size())).save(vacationCaptor.capture());
        List<Vacations> capturedVacations = vacationCaptor.getAllValues();

        // ASSERT
        capturedVacations.forEach(vac -> {
            assertEquals(employee, vac.getEmployee());
            assertEquals(CURRENT_YEAR, vac.getPeriodYear());
            int expectedWorkingDays = 0;
            for (LocalDate d = vac.getBeginDate(); !d.isAfter(vac.getEndDate()); d = d.plusDays(1)) {
                if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    expectedWorkingDays++;
                }
            }
            assertEquals(expectedWorkingDays, vac.getWorkingDays());
            assertEquals(false, vac.getWasUsed());
        });

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(inputVacations.size(), result.size())
        );

        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
        verify(parameterRepository, times(1)).findOneByParameterKey(PARAM_KEY_DIAS_VACACIONES);
        verify(vacationsRepository, times(inputVacations.size())).save(any(Vacations.class));
        verify(vacationsRepository, times(1))
            .findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(EMPLOYEE_ID, CURRENT_YEAR);
    }

    @Test
    public void shouldUpdateVacationsForEmployeeOnPeriodSuccessfully() throws Exception {
        // ARRANGE
        Vacations vac1 = new Vacations();
        vac1.setBeginDate(LocalDate.of(CURRENT_YEAR, 1, 6)); // Mismatched year → will cause areValidVacationPeriods to return false
        vac1.setEndDate(LocalDate.of(CURRENT_YEAR, 1, 10));   // 5 working days

        Vacations vac2 = new Vacations();
        vac2.setBeginDate(LocalDate.of(CURRENT_YEAR, 1, 13)); // Valid dates for PERIOD 2025
        vac2.setEndDate(LocalDate.of(CURRENT_YEAR, 1, 17));   // 5 working days

        Vacations vac3 = new Vacations();
        vac3.setBeginDate(LocalDate.of(CURRENT_YEAR, 1, 20)); // Valid dates for PERIOD 2025
        vac3.setEndDate(LocalDate.of(CURRENT_YEAR, 1, 24));  // 5 working days

        List<Vacations> inputVacations = Arrays.asList(vac1, vac2, vac3);

        when(employeeRepository.findById(EMPLOYEE_ID))
            .thenReturn(Optional.of(employee));
        when(vacationsRepository.findAllByEmployee_IdAndPeriodYearAndWasUsedTrue(EMPLOYEE_ID, CURRENT_YEAR))
            .thenReturn(Arrays.asList());
        when(parameterRepository.findOneByParameterKey(PARAM_KEY_DIAS_VACACIONES))
            .thenReturn(Optional.of(parameter));
        when(vacationsRepository.save(any(Vacations.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(vacationsRepository.findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(EMPLOYEE_ID, CURRENT_YEAR))
            .thenReturn(inputVacations);

        // ACT
        List<Vacations> result = vacationsService.updateVacationsForEmployeeOnPeriod(EMPLOYEE_ID, CURRENT_YEAR, inputVacations);

        ArgumentCaptor<Vacations> vacationCaptor = ArgumentCaptor.forClass(Vacations.class);
        verify(vacationsRepository, times(inputVacations.size())).save(vacationCaptor.capture());
        List<Vacations> capturedVacations = vacationCaptor.getAllValues();

        // ASSERT
        capturedVacations.forEach(vac -> {
            assertEquals(employee, vac.getEmployee(), "Employee should be set correctly");
            assertEquals(CURRENT_YEAR, vac.getPeriodYear(), "Period year should be set to input period");
            assertEquals(false, vac.getWasUsed(), "Vacation should be marked as not used");
            int expectedWorkingDays = 0;
            for (LocalDate d = vac.getBeginDate(); !d.isAfter(vac.getEndDate()); d = d.plusDays(1)) {
                if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    expectedWorkingDays++;
                }
            }
            assertEquals(expectedWorkingDays, vac.getWorkingDays(), "Working days should be computed correctly");
        });

        assertAll("Returned list assertions",
            () -> assertNotNull(result, "Result should not be null"),
            () -> assertEquals(inputVacations.size(), result.size(), "Result list size should match input list size")
        );

        verify(vacationsRepository, times(1)).deleteByPeriodYear(CURRENT_YEAR);
        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
        verify(parameterRepository, times(1)).findOneByParameterKey(PARAM_KEY_DIAS_VACACIONES);
        verify(vacationsRepository, times(1))
            .findAllByEmployee_IdAndPeriodYearOrderByBeginDateAsc(EMPLOYEE_ID, CURRENT_YEAR);
    }
}
