package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.DAY_OFF;

class AbstractNeighbourhoodStrategy {

    Function<DateEmployeeShiftAssignment, Stream<? extends Schedule>> createNeighboursWithAllWorkingShifts(Schedule schedule) {
        return dateEmployeeShiftAssignment -> Shift.allWorkingShifts().map(shift -> createNeighbour(schedule, dateEmployeeShiftAssignment, shift));
    }

    Function<DateEmployeeShiftAssignment, Schedule> createNeighbourWithDayOffShift(Schedule schedule) {
        return dateEmployeeShiftAssignment -> createNeighbour(schedule, dateEmployeeShiftAssignment, DAY_OFF);

    }

     Schedule createNeighbour(Schedule schedule, DateEmployeeShiftAssignment dateEmployeeShiftAssignment, Shift shift) {
        DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = dateEmployeeShiftAssignment.setShift(shift);
        List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = schedule.getDateShiftAssignments().collect(toList());
        List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
        newSchedule.set(dateEmployeeShiftAssignments.indexOf(dateEmployeeShiftAssignment), dateEmployeeShiftAssignmentWithAddedShift);
        return Schedule.ofDateEmployeeShiftAssignment(newSchedule, schedule.getYear(), schedule.getMonth(), schedule.getNumberOfChildren());
    }

}
