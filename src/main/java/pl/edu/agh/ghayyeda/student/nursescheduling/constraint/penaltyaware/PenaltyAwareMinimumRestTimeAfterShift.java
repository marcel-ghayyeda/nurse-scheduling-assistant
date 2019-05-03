package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Locale.US;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class PenaltyAwareMinimumRestTimeAfterShift implements ScheduleConstraint {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d HH:mm").localizedBy(US);
    private static final Logger log = LoggerFactory.getLogger(PenaltyAwareMinimumRestTimeAfterShift.class);

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var result = schedule.getDateShiftAssignments()
                .collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .values().stream()
                .map(this::eachEmployeeHasMinimumRestTimeBetweenShifts)
                .reduce(new ValidationResultForEmployee(0, 0, List.of()), ValidationResultForEmployee::sum);

        return ScheduleConstraintValidationResult.ofPenalty(calculatePenalty(result), result.constraintViolationDescriptions);
    }

    private ValidationResultForEmployee eachEmployeeHasMinimumRestTimeBetweenShifts(List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments) {
        var workingShiftsAssignmentsSortedByStartDate = dateEmployeeShiftAssignments.stream()
                .filter(DateEmployeeShiftAssignment::isWorkDay)
                .sorted(comparing(DateEmployeeShiftAssignment::getStartDate))
                .collect(toList());

        int notFeasibleShiftsCount = 0;
        int totalCount = 0;
        List<String> constraintViolationDescriptions = new LinkedList<>();

        for (int i = 0; i < workingShiftsAssignmentsSortedByStartDate.size() - 1; i++) {
            var firstAssignment = workingShiftsAssignmentsSortedByStartDate.get(i);
            var secondAssignment = workingShiftsAssignmentsSortedByStartDate.get(i + 1);

            var firstShift = firstAssignment.getShift();
            var secondShift = secondAssignment.getShift();

            var firstShiftEndTime = LocalDateTime.of(firstAssignment.getEndDate(), firstShift.getEndTime());
            var secondShiftStartTime = LocalDateTime.of(secondAssignment.getStartDate(), secondShift.getStartTime());

            var requiredRestTime = firstShift.getRestTime();
            if (Duration.between(firstShiftEndTime, secondShiftStartTime).compareTo(requiredRestTime) < 0) {
                constraintViolationDescriptions.add(format("No minimum required rest time %dh between %s and %s for %s", requiredRestTime.toHours(), formatter.format(firstShiftEndTime), formatter.format(secondShiftStartTime), firstAssignment.getEmployee().getName()));
                log.debug("No minimum required rest time {} between {} and {}, for {}", requiredRestTime, firstShiftEndTime, secondShiftStartTime, firstAssignment.getEmployee());
                notFeasibleShiftsCount++;
            }

            totalCount++;
        }

        return new ValidationResultForEmployee(notFeasibleShiftsCount, totalCount, constraintViolationDescriptions);
    }

    private static class ValidationResultForEmployee {
        private final int notFeasibleShiftsCount;
        private final int totalShiftsCount;
        private final List<String> constraintViolationDescriptions;

        private ValidationResultForEmployee(int notFeasibleShiftsCount, int totalShiftsCount, List<String> constraintViolationDescriptions) {
            this.notFeasibleShiftsCount = notFeasibleShiftsCount;
            this.totalShiftsCount = totalShiftsCount;
            this.constraintViolationDescriptions = constraintViolationDescriptions;
        }

        private static ValidationResultForEmployee sum(ValidationResultForEmployee result1, ValidationResultForEmployee result2) {
            int notFeasibleShiftsCount = result1.notFeasibleShiftsCount + result2.notFeasibleShiftsCount;
            int totalShiftsCount = result1.totalShiftsCount + result2.totalShiftsCount;
            List<String> constraintViolationDescriptions = Stream.concat(result1.constraintViolationDescriptions.stream(), result2.constraintViolationDescriptions.stream()).collect(toList());
            return new ValidationResultForEmployee(notFeasibleShiftsCount, totalShiftsCount, constraintViolationDescriptions);
        }
    }

    private double calculatePenalty(ValidationResultForEmployee result) {
        return Math.sqrt(result.notFeasibleShiftsCount / (double) result.totalShiftsCount);
    }
}
