package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.function.ToDoubleFunction;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleContraintUtils.significantHoursOfDay;

public class PenaltyAwareRequiredNumberOfEmployees implements ScheduleConstraint {

    private static final Logger log = LoggerFactory.getLogger(PenaltyAwareRequiredNumberOfEmployees.class);
    private static final int MAX_CHILDREN_PER_EMPLOYEE_DURING_DAY = 3;
    private static final int MAX_CHILDREN_PER_EMPLOYEE_DURING_NIGHT = 5;
    private static final double NO_NURSE_PENALTY = 0.45d;

    private final LocalDateTime validationStartTime;
    private final LocalDateTime validationEndTime;
    private final YearMonth validationMonth;
    private final int numberOfChildren;

    private PenaltyAwareRequiredNumberOfEmployees(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        assert validationStartTime.getMonth() == validationEndTime.getMonth();
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
        this.validationMonth = YearMonth.from(validationStartTime);
        this.numberOfChildren = numberOfChildren;
    }

    static PenaltyAwareRequiredNumberOfEmployees between(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        return new PenaltyAwareRequiredNumberOfEmployees(validationStartTime, validationEndTime, numberOfChildren);
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var sumOfPenalties = YearMonthUtil.allDaysOf(validationMonth)
                .flatMap(dayOfMonth -> significantHoursOfDay().mapToObj(toLocalDateTimeOn(dayOfMonth)))
                .filter(notBeforeValidationStartTime())
                .filter(beforeValidationEndtime())
                .mapToDouble(hasMinimumRequiredNumberOfBabySitters(schedule))
                .sum();

        return ScheduleConstraintValidationResult.ofPenalty(sumOfPenalties);
    }


    private ToDoubleFunction<LocalDateTime> hasMinimumRequiredNumberOfBabySitters(Schedule schedule) {
        return timeOfDuty -> {
            var employees = schedule.getEmployeeShiftAssignmentsFor(timeOfDuty).collect(toList());

            var numberOfEmployees = employees.size();
            var requiredNumberOfEmployees = calculateRequiredNumberOfEmployees(timeOfDuty);

            if (numberOfEmployees < requiredNumberOfEmployees) {
                log.debug("Not enough employees on {}", timeOfDuty);
                double missingEmployees = requiredNumberOfEmployees - numberOfEmployees;
                return missingEmployees / requiredNumberOfEmployees;
            } else {
                if (employees.stream().noneMatch(isNurse())) {
                    log.debug("No nurse on {}", timeOfDuty);
                    return NO_NURSE_PENALTY;
                }
                return 0d;
            }
        };
    }

    private double calculateRequiredNumberOfEmployees(LocalDateTime timeOfDuty) {
        return isDay(timeOfDuty) ?
                Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_EMPLOYEE_DURING_DAY) :
                Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_EMPLOYEE_DURING_NIGHT);
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
