package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.failfast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;
import pl.edu.agh.ghayyeda.student.nursescheduling.util.YearMonthUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult.feasibleConstraintValidationResult;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleContraintUtils.significantHoursOfDay;

class FailFastRequiredNumberOfEmployees implements ScheduleConstraint {

    private static final Logger log = LoggerFactory.getLogger(FailFastRequiredNumberOfEmployees.class);
    private static final int MAX_CHILDREN_PER_EMPLOYEE_DURING_DAY = 3;
    private static final int MAX_CHILDREN_PER_EMPLOYEE_DURING_NIGHT = 5;

    private final LocalDateTime validationStartTime;
    private final LocalDateTime validationEndTime;
    private final YearMonth validationMonth;
    private final int numberOfChildren;

    private FailFastRequiredNumberOfEmployees(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        assert validationStartTime.getMonth() == validationEndTime.getMonth();
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
        this.validationMonth = YearMonth.from(validationStartTime);
        this.numberOfChildren = numberOfChildren;
    }

    public static FailFastRequiredNumberOfEmployees between(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        return new FailFastRequiredNumberOfEmployees(validationStartTime, validationEndTime, numberOfChildren);
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var isFeasible = YearMonthUtil.allDaysOf(validationMonth)
                .flatMap(dayOfMonth -> significantHoursOfDay().mapToObj(toLocalDateTimeOn(dayOfMonth)))
                .filter(notBeforeValidationStartTime())
                .filter(beforeValidationEndtime())
                .allMatch(hasMinimumRequiredNumberOfBabySitters(schedule));

        return isFeasible ? feasibleConstraintValidationResult() : ScheduleConstraintValidationResult.notFeasibleConstraintValidationResult();
    }

    private Predicate<? super LocalDateTime> hasMinimumRequiredNumberOfBabySitters(Schedule schedule) {
        return timeOfDuty -> {
            var employees = schedule.getEmployeeShiftAssignmentsFor(timeOfDuty).collect(toList());
            if (employees.stream().noneMatch(isNurse())) {
                log.debug("Not nurse on {}", timeOfDuty);
                return false;
            }
            var numberOfEmployees = employees.size();
            final boolean feasible = isDay(timeOfDuty) ?
                    numberOfEmployees >= Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_EMPLOYEE_DURING_DAY) :
                    numberOfEmployees >= Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_EMPLOYEE_DURING_NIGHT);
            if (!feasible) {
                log.debug("Not enough employees on {}", timeOfDuty);
            }
            return feasible;
        };
    }

    private Predicate<? super EmployeeShiftAssignment> isNurse() {
        return dayShiftAssignment -> Employee.Type.NURSE == dayShiftAssignment.getEmployeeType();
    }

    private boolean isDay(LocalDateTime timeOfDuty) {
        return timeOfDuty.getHour() >= 6 && timeOfDuty.getHour() < 22;
    }

    private IntFunction<LocalDateTime> toLocalDateTimeOn(LocalDate dayOfMonth) {
        return hour -> LocalDateTime.of(dayOfMonth, LocalTime.of(hour, 0));
    }


    private Predicate<LocalDateTime> notBeforeValidationStartTime() {
        return localDateTime -> !localDateTime.isBefore(validationStartTime);
    }

    private Predicate<? super LocalDateTime> beforeValidationEndtime() {
        return localDateTime -> localDateTime.isBefore(validationEndTime);
    }

}
