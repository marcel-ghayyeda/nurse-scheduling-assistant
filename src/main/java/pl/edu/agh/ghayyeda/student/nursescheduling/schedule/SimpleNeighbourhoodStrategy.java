package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;

import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

public class SimpleNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private static final Logger log = LoggerFactory.getLogger(SimpleNeighbourhoodStrategy.class);

    @Override
    public Neighbourhood createNeighbourhood(Schedule schedule, ConstraintValidationResult ignored) {
        var neighbourhood = Stream.of(swapShiftsInTheSameDaysBetweenEmployees(schedule), addWorkingShifts(schedule), removeShifts(schedule)).flatMap(identity()).collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return new Neighbourhood(neighbourhood);
    }


    private Stream<Schedule> swapShiftsInTheSameDaysBetweenEmployees(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(this::isWorkDayOrDayOff)
                .flatMap(shiftAssignment1 ->
                        schedule.getDateShiftAssignmentMatching(isEligibleToSwap(shiftAssignment1))
                                .filter(shiftAssignment2 -> schedule.isAllowedShift(shiftAssignment2.getEmployee(), shiftAssignment1.getShift()))
                                .map(shiftAssignment2 -> createNeighbourWithSwappedShifts(schedule, shiftAssignment1, shiftAssignment2)));
    }


    @VisibleForTesting
    Stream<Schedule> addWorkingShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(shouldAddWorkingShift(schedule))
                .flatMap(createNeighboursWithAllWorkingShifts(schedule));
    }

    @VisibleForTesting
    Stream<Schedule> removeShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .map(createNeighbourWithDayOffShift(schedule));
    }

}
