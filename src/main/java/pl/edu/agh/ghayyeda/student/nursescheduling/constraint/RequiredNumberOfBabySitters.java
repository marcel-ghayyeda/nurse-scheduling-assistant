package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

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
import java.util.stream.IntStream;

import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.HardConstraintValidationResult.feasibleConstraintValidationResult;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.HardConstraintValidationResult.notFeasibleConstraintValidationResult;

public class RequiredNumberOfBabySitters implements ScheduleConstraint {

    private static final int MAX_CHILDREN_PER_BABY_SITTER_DURING_DAY = 3;
    private static final int MAX_CHILDREN_PER_BABY_SITTER_DURING_NIGHT = 5;

    private final LocalDateTime validationStartTime;
    private final LocalDateTime validationEndTime;
    private final YearMonth validationMonth;
    private final int numberOfChildren;

    private RequiredNumberOfBabySitters(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        assert validationStartTime.getMonth() == validationEndTime.getMonth();
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
        this.validationMonth = YearMonth.from(validationStartTime);
        this.numberOfChildren = numberOfChildren;
    }

    public static RequiredNumberOfBabySitters between(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        return new RequiredNumberOfBabySitters(validationStartTime, validationEndTime, numberOfChildren);
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var isFeasible = YearMonthUtil.allDaysOf(validationMonth)
                .flatMap(dayOfMonth -> allHoursADay().mapToObj(toLocalDateTimeOn(dayOfMonth)))
                .filter(notBeforeValidationStartTime())
                .filter(beforeValidationEndtime())
                .allMatch(hasMinimumRequiredNumberOfBabySitters(schedule));

        return isFeasible ? feasibleConstraintValidationResult() : notFeasibleConstraintValidationResult();
    }

    private Predicate<? super LocalDateTime> hasMinimumRequiredNumberOfBabySitters(Schedule schedule) {
        return timeOfDuty -> {
            var numberOfBabySitters = schedule.getEmployeeShiftAssignmentsFor(timeOfDuty).filter(isBabySitter()).count();
            final boolean feasible = isDay(timeOfDuty) ?
                    numberOfBabySitters >= Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_BABY_SITTER_DURING_DAY) :
                    numberOfBabySitters >= Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_BABY_SITTER_DURING_NIGHT);
            return feasible;
        };
    }

    private boolean isDay(LocalDateTime timeOfDuty) {
        return timeOfDuty.getHour() >= 6 && timeOfDuty.getHour() < 22;
    }

    private Predicate<? super EmployeeShiftAssignment> isBabySitter() {
        return employeeShiftAssignment -> Employee.Type.BABY_SITTER == employeeShiftAssignment.getEmployeeType();
    }

    private IntStream allHoursADay() {
        return IntStream.rangeClosed(0, 23);
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
