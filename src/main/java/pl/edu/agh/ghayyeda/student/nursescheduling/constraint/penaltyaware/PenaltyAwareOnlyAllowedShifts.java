package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Locale.US;
import static java.util.stream.Collectors.toList;

public class PenaltyAwareOnlyAllowedShifts implements ScheduleConstraint {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d").localizedBy(US);

    @Override
    public ConstraintValidationResult validate(Schedule schedule) {
        var result = schedule.getDateShiftAssignments()
                .map(eachEmployeeHasOnlyAllowedShifts(schedule))
                .flatMap(Optional::stream)
                .reduce(new ValidationResultForEmployee(0, List.of()), ValidationResultForEmployee::sum);

        return ConstraintValidationResult.ofPenalty(calculatePenalty(result), result.constraintViolationsDescriptions);
    }

    private Function<DateEmployeeShiftAssignment, Optional<ValidationResultForEmployee>> eachEmployeeHasOnlyAllowedShifts(Schedule schedule) {
        return dateEmployeeShiftAssignment -> {
            if (schedule.isAllowedShift(dateEmployeeShiftAssignment.getEmployee(), dateEmployeeShiftAssignment.getShift())) {
                return Optional.empty();
            } else {
                var violationDescription = format("Not allowed shift on %s for %s", formatter.format(dateEmployeeShiftAssignment.getStartDate()), dateEmployeeShiftAssignment.getEmployee().getName());
                var employeeDateViolations = List.of(new EmployeeDateViolation(dateEmployeeShiftAssignment.getEmployee(), dateEmployeeShiftAssignment.getStartDate()));
                return Optional.of(new ValidationResultForEmployee(1, List.of(new ConstraintViolationsDescription(violationDescription, employeeDateViolations))));
            }
        };
    }


    private static class ValidationResultForEmployee {
        private final int notFeasibleShiftsCount;
        private final List<ConstraintViolationsDescription> constraintViolationsDescriptions;


        private ValidationResultForEmployee(int notFeasibleShiftsCount, List<ConstraintViolationsDescription> constraintViolationsDescriptions) {
            this.notFeasibleShiftsCount = notFeasibleShiftsCount;
            this.constraintViolationsDescriptions = constraintViolationsDescriptions;
        }

        private static ValidationResultForEmployee sum(ValidationResultForEmployee result1, ValidationResultForEmployee result2) {
            int notFeasibleShiftsCount = result1.notFeasibleShiftsCount + result2.notFeasibleShiftsCount;
            var constraintViolationDescriptions = Stream.concat(result1.constraintViolationsDescriptions.stream(), result2.constraintViolationsDescriptions.stream()).collect(toList());
            return new ValidationResultForEmployee(notFeasibleShiftsCount, constraintViolationDescriptions);
        }
    }

    private double calculatePenalty(ValidationResultForEmployee result) {
        return result.notFeasibleShiftsCount;
    }
}
