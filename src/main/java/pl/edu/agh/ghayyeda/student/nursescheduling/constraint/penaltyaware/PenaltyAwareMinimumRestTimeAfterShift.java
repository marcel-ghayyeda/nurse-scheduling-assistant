package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class PenaltyAwareMinimumRestTimeAfterShift implements ScheduleConstraint {

    private static final Logger log = LoggerFactory.getLogger(PenaltyAwareMinimumRestTimeAfterShift.class);

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var result = schedule.getDateShiftAssignments()
                .collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .values().stream()
                .map(this::eachEmployeeHasMinimumRestTimeBetweenShifts)
                .reduce(new ValidationResultForEmployee(0, 0), ValidationResultForEmployee::sum);

        return ScheduleConstraintValidationResult.ofPenalty(calculatePenalty(result));
    }

    private ValidationResultForEmployee eachEmployeeHasMinimumRestTimeBetweenShifts(List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments) {
        var workingShiftsAssignmentsSortedByStartDate = dateEmployeeShiftAssignments.stream()
                .filter(DateEmployeeShiftAssignment::isWorkDay)
                .sorted(comparing(DateEmployeeShiftAssignment::getStartDate))
                .collect(toList());

        int notFeasibleShiftsCount = 0;
        int totalCount = 0;

        for (int i = 0; i < workingShiftsAssignmentsSortedByStartDate.size() - 1; i++) {
            var firstAssignment = workingShiftsAssignmentsSortedByStartDate.get(i);
            var secondAssignment = workingShiftsAssignmentsSortedByStartDate.get(i + 1);

            var firstShift = firstAssignment.getShift();
            var secondShift = secondAssignment.getShift();

            var firstShiftEndTime = LocalDateTime.of(firstAssignment.getEndDate(), firstShift.getEndTime());
            var secondShiftStartTime = LocalDateTime.of(secondAssignment.getStartDate(), secondShift.getStartTime());

            var requiredRestTime = firstShift.getRestTime();
            if (Duration.between(firstShiftEndTime, secondShiftStartTime).compareTo(requiredRestTime) < 0) {
                log.debug("No minimum required rest time {} between {} and {}, for {}", requiredRestTime, firstShiftEndTime, secondShiftStartTime, firstAssignment.getEmployee());
                notFeasibleShiftsCount++;
            }

            totalCount++;
        }

        return new ValidationResultForEmployee(notFeasibleShiftsCount, totalCount);
    }

    private static class ValidationResultForEmployee {
        private final int notFeasibleShiftsCount;
        private final int totalShiftsCount;

        private ValidationResultForEmployee(int notFeasibleShiftsCount, int totalShiftsCount) {
            this.notFeasibleShiftsCount = notFeasibleShiftsCount;
            this.totalShiftsCount = totalShiftsCount;
        }

        private static ValidationResultForEmployee sum(ValidationResultForEmployee result1, ValidationResultForEmployee result2) {
            int notFeasibleShiftsCount = result1.notFeasibleShiftsCount + result2.notFeasibleShiftsCount;
            int totalShiftsCount = result1.totalShiftsCount + result2.totalShiftsCount;
            return new ValidationResultForEmployee(notFeasibleShiftsCount, totalShiftsCount);
        }
    }

    private double calculatePenalty(ValidationResultForEmployee result) {
        return result.notFeasibleShiftsCount / (double) result.totalShiftsCount;
    }
}
