package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Locale.US;
import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleContraintUtils.significantHoursOfDay;

class PenaltyAwareRequiredNumberOfEmployees implements ScheduleConstraint {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d HH:mm").localizedBy(US);
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
        var summaryValidationResult = YearMonthUtil.allDaysOf(validationMonth)
                .flatMap(dayOfMonth -> significantHoursOfDay().mapToObj(toLocalDateTimeOn(dayOfMonth)))
                .filter(notBeforeValidationStartTime())
                .filter(beforeValidationEndtime())
                .map(hasMinimumRequiredNumberOfBabySitters(schedule))
                .reduce(new ValidationResultForDate(0d), ValidationResultForDate::sum);

        return ScheduleConstraintValidationResult.ofPenalty(summaryValidationResult.penalty, summaryValidationResult.descriptions);
    }


    private Function<LocalDateTime, ValidationResultForDate> hasMinimumRequiredNumberOfBabySitters(Schedule schedule) {
        return timeOfDuty -> {
            var employees = schedule.getEmployeeShiftAssignmentsFor(timeOfDuty).collect(toList());

            var numberOfEmployees = employees.size();
            var requiredNumberOfEmployees = calculateRequiredNumberOfEmployees(timeOfDuty);

            if (numberOfEmployees < requiredNumberOfEmployees) {
                log.debug("Not enough employees on {}", timeOfDuty);
                double missingEmployees = requiredNumberOfEmployees - numberOfEmployees;
                String description = String.format("Not enough employees on %s. Expected %d but found %d", formatter.format(timeOfDuty), requiredNumberOfEmployees, numberOfEmployees);
                return new ValidationResultForDate(Math.sqrt(missingEmployees / requiredNumberOfEmployees), description);
            } else {
                if (employees.stream().noneMatch(isNurse())) {
                    log.debug("No nurse on {}", timeOfDuty);
                    String description = String.format("No nurse on %s", formatter.format(timeOfDuty));
                    return new ValidationResultForDate(NO_NURSE_PENALTY, description);
                }
                return new ValidationResultForDate(0d);
            }
        };
    }

    private static class ValidationResultForDate {
        double penalty;
        List<String> descriptions;

        public ValidationResultForDate(double penalty, String description) {
            this.penalty = penalty;
            this.descriptions = List.of(description);
        }

        public ValidationResultForDate(double penalty) {
            this.penalty = penalty;
            descriptions = List.of();
        }

        public ValidationResultForDate(double penalty, List<String> descriptions) {
            this.penalty = penalty;
            this.descriptions = descriptions;
        }

        private static ValidationResultForDate sum(ValidationResultForDate result1, ValidationResultForDate result2) {
            double penaltySum = result1.penalty + result2.penalty;
            List<String> constraintViolationDescriptions = Stream.concat(result1.descriptions.stream(), result2.descriptions.stream()).collect(toList());

            return new ValidationResultForDate(penaltySum, constraintViolationDescriptions);
        }
    }

    private int calculateRequiredNumberOfEmployees(LocalDateTime timeOfDuty) {
        return isDay(timeOfDuty) ?
                (int) Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_EMPLOYEE_DURING_DAY) :
                (int) Math.ceil(numberOfChildren / (double) MAX_CHILDREN_PER_EMPLOYEE_DURING_NIGHT);
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
