package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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


    private Schedule createNeighbour(Schedule schedule, DateEmployeeShiftAssignment dateEmployeeShiftAssignment, Shift shift) {
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

    Stream<Schedule> swapShiftsInTheSameDaysBetweenEmployees(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate, Adaptation adaptation) {
        return schedule.getDateShiftAssignmentMatching(this::isWorkDayOrDayOff)
                .filter(isEligibleForChanging(employeeDateViolationsByDate, adaptation))
                .flatMap(shiftAssignment1 ->
                        schedule.getDateShiftAssignmentMatching(isEligibleToSwap(shiftAssignment1))
                                .filter(shiftAssignment2 -> schedule.isAllowedShift(shiftAssignment2.getEmployee(), shiftAssignment1.getShift()))
                                .map(shiftAssignment2 -> createNeighbourWithSwappedShifts(schedule, shiftAssignment1, shiftAssignment2)));
    }

    private Predicate<DateEmployeeShiftAssignment> isEligibleForChanging(Map<LocalDate, List<EmployeeDateViolation>> violationsByDate, Adaptation adaptation) {
        return dateEmployeeShiftAssignment -> getViolations(violationsByDate, dateEmployeeShiftAssignment, adaptation)
                .anyMatch(refersTo(dateEmployeeShiftAssignment.getEmployee()));
    }

    private Predicate<EmployeeDateViolation> refersTo(Employee employee) {
        return employeeDateViolation -> employeeDateViolation.getEmployee().map(employee::equals).orElse(true);
    }

    private Stream<EmployeeDateViolation> getViolations(Map<LocalDate, List<EmployeeDateViolation>> violationsByDate, DateEmployeeShiftAssignment dateEmployeeShiftAssignment, Adaptation adaptation) {
        switch (adaptation) {
            case NARROW:
                return violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate(), List.of()).stream();
            case WIDE:
                return Stream.of(
                        violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate(), List.of()),
                        violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate().minusDays(1), List.of()),
                        violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate().plusDays(1), List.of())

                ).flatMap(Collection::stream);
            default:
                throw new IllegalStateException();
        }
    }

    Stream<Schedule> addWorkingShifts(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate, Adaptation adaptation) {
        return schedule.getDateShiftAssignmentMatching(this::isWorkDayOrDayOff)
                .filter(isEligibleForChanging(employeeDateViolationsByDate, adaptation))
                .flatMap(createNeighboursWithAllWorkingShifts(schedule));
    }

    Stream<Schedule> removeShifts(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate, Adaptation adaptation) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .filter(isEligibleForChanging(employeeDateViolationsByDate, adaptation))
                .map(createNeighbourWithDayOffShift(schedule));
    }

}
