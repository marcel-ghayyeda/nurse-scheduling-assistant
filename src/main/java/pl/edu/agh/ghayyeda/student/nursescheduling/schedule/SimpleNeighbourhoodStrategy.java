package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SimpleNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private static final Logger log = LoggerFactory.getLogger(SimpleNeighbourhoodStrategy.class);

    @Override
    public Neighbourhood createNeighbourhood(Schedule schedule, ConstraintValidationResult ignored) {
        var neighbourhood = Stream.concat(addWorkingShifts(schedule), removeShifts(schedule)).collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return new Neighbourhood(neighbourhood);
    }

    @VisibleForTesting
    Stream<Schedule> addWorkingShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isDayOff)
                .flatMap(createNeighboursWithAllWorkingShifts(schedule));
    }

    @VisibleForTesting
    Stream<Schedule> removeShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .map(createNeighbourWithDayOffShift(schedule));
    }

}
