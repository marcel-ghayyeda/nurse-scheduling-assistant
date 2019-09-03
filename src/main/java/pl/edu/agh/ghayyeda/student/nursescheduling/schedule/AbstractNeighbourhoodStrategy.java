package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.DAY_OFF;

class AbstractNeighbourhoodStrategy {

    Function<DateEmployeeShiftAssignment, Stream<? extends Schedule>> createNeighboursWithAllWorkingShifts(Schedule schedule) {
        return dateEmployeeShiftAssignment -> schedule.getAllowedWorkingShiftsFor(dateEmployeeShiftAssignment.getEmployee()).stream().map(shift -> createNeighbour(schedule, dateEmployeeShiftAssignment, shift));
    }

    Function<DateEmployeeShiftAssignment, Schedule> createNeighbourWithDayOffShift(Schedule schedule) {
        return dateEmployeeShiftAssignment -> createNeighbour(schedule, dateEmployeeShiftAssignment, DAY_OFF);

    }


    Schedule createNeighbour(Schedule schedule, DateEmployeeShiftAssignment dateEmployeeShiftAssignment, Shift shift) {
        DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = dateEmployeeShiftAssignment.setShift(shift);
        List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = schedule.getDateShiftAssignments().collect(toList());
        List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
        newSchedule.set(dateEmployeeShiftAssignments.indexOf(dateEmployeeShiftAssignment), dateEmployeeShiftAssignmentWithAddedShift);
        return Schedule.ofDateEmployeeShiftAssignment(newSchedule, schedule.getYear(), schedule.getMonth(), schedule.getNumberOfChildren(), schedule.getAllowedWorkingShiftPerEmployee(), schedule.getAvailabilityPerEmployee());
    }

    Predicate<DateEmployeeShiftAssignment> isEligibleToSwap(DateEmployeeShiftAssignment shiftAssignment1) {
        return assignment2 -> assignment2.getStartDate().equals(shiftAssignment1.getStartDate()) && !assignment2.getEmployee().equals(shiftAssignment1.getEmployee()) && assignment2.getShift() != shiftAssignment1.getShift() && isWorkDayOrDayOff(assignment2);
    }

    boolean isWorkDayOrDayOff(DateEmployeeShiftAssignment assignment) {
        return assignment.isWorkDay() || assignment.isDayOff();
    }

    Schedule createNeighbourWithSwappedShifts(Schedule schedule, DateEmployeeShiftAssignment shiftAssignment1, DateEmployeeShiftAssignment shiftAssignment2) {
        return createNeighbour(createNeighbour(schedule, shiftAssignment2, shiftAssignment1.getShift()), shiftAssignment1, shiftAssignment2.getShift());
    }

}
