package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FullNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private static final Logger log = LoggerFactory.getLogger(FullNeighbourhoodStrategy.class);

    @Override
    public Neighbourhood createNeighbourhood(Schedule schedule) {
        var neighbourhood = Stream.concat(addRandomShifts(schedule), removeRandomShifts(schedule)).collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return new Neighbourhood(neighbourhood);
    }

    @VisibleForTesting
    Stream<Schedule> addRandomShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isDayOff)
                .flatMap(dateEmployeeShiftAssignment -> Shift.allWorkingShifts().map(shift -> {
                    DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = dateEmployeeShiftAssignment.setShift(shift);
                    List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = schedule.getDateShiftAssignments().collect(toList());
                    List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
                    newSchedule.set(dateEmployeeShiftAssignments.indexOf(dateEmployeeShiftAssignment), dateEmployeeShiftAssignmentWithAddedShift);
                    return Schedule.ofDateEmployeeShiftAssignment(newSchedule, schedule.getYear(), schedule.getMonth(), schedule.getNumberOfChildren());
                }));
    }

    @VisibleForTesting
    Stream<Schedule> removeRandomShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .map(randomDateEmployeeShiftAssignments -> {
                    DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = randomDateEmployeeShiftAssignments.removeShift();
                    List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = schedule.getDateShiftAssignments().collect(toList());
                    List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
                    newSchedule.set(dateEmployeeShiftAssignments.indexOf(randomDateEmployeeShiftAssignments), dateEmployeeShiftAssignmentWithAddedShift);
                    return Schedule.ofDateEmployeeShiftAssignment(newSchedule, schedule.getYear(), schedule.getMonth(), schedule.getNumberOfChildren());
                });
    }


}
