package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.failfast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult.feasibleConstraintValidationResult;

class FailFastMinimumRestTimeAfterShift implements ScheduleConstraint {

    private static final Logger log = LoggerFactory.getLogger(FailFastMinimumRestTimeAfterShift.class);

    @Override
    public ConstraintValidationResult validate(Schedule schedule) {
        var isFeasible = schedule.getDateShiftAssignments()
                .collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .values().stream()
                .allMatch(this::eachEmployeeHasMinimumRestTimeBetweenShifts);

        return isFeasible ? feasibleConstraintValidationResult() : ConstraintValidationResult.notFeasibleConstraintValidationResult();
    }

    private boolean eachEmployeeHasMinimumRestTimeBetweenShifts(List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments) {
        var workingShiftsAssignmentsSortedByStartDate = dateEmployeeShiftAssignments.stream()
                .filter(DateEmployeeShiftAssignment::isWorkDay)
                .sorted(comparing(DateEmployeeShiftAssignment::getStartDate))
                .collect(toList());

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
                return false;
            }
        }

        return true;
    }
}
