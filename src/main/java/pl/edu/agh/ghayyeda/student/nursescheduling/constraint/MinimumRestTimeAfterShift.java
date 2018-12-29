package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.HardConstraintValidationResult.feasibleConstraintValidationResult;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.HardConstraintValidationResult.notFeasibleConstraintValidationResult;

public class MinimumRestTimeAfterShift implements ScheduleConstraint {

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var isFeasible = schedule.getDateShiftAssignments()
                .collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .values().stream()
                .allMatch(this::eachEmployeeHasMinimumRestTimeBetweenShifts);

        return isFeasible ? feasibleConstraintValidationResult() : notFeasibleConstraintValidationResult();
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

            var firstShiftEndTime = LocalDateTime.of(firstAssignment.getStartDate(), firstShift.getEndTime());
            var secondShiftStartTime = LocalDateTime.of(secondAssignment.getStartDate(), secondShift.getStartTime());

            var requiredRestTime = firstShift.getRestTime();
            if (Duration.between(firstShiftEndTime, secondShiftStartTime).compareTo(requiredRestTime) < 0)
                return false;
        }

        return true;
    }
}
