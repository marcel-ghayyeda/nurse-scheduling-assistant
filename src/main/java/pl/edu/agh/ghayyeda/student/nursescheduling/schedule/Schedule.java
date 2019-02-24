package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

public class Schedule {

    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    private final List<DateEmployeeShiftAssignments> schedule;

    Schedule(List<DateEmployeeShiftAssignments> schedule) {
        this.schedule = schedule;
    }

    public static Schedule ofDateEmployeeShiftAssignment(List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments) {
        return dateEmployeeShiftAssignments.stream()
                .collect(groupingBy(DateEmployeeShiftAssignment::getStartDate, mapping(DateEmployeeShiftAssignment::getEmployeeShiftAssignment, toList())))
                .entrySet()
                .stream()
                .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                .collect(Collectors.collectingAndThen(toList(), Schedule::new));
    }

    public List<DateEmployeeShiftAssignments> getDateEmployeeShiftAssignmentsByDate() {
        return schedule;
    }

    public Stream<EmployeeShiftAssignment> getEmployeeShiftAssignmentsFor(LocalDateTime localDateTime) {
        return schedule.stream()
                .flatMap(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.getFor(localDateTime));
    }

    public Stream<DateEmployeeShiftAssignment> getDateShiftAssignments() {
        return schedule.stream()
                .flatMap(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.getShiftAssignments().stream().map(employeeShiftAssignment -> new DateEmployeeShiftAssignment(dateEmployeeShiftAssignments.getStartDate(), employeeShiftAssignment)));
    }

    public List<Schedule> getNeighbourhood() {
        var neighbourhood = Stream.concat(addRandomShifts(), removeRandomShifts()).collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return neighbourhood;
    }

    Stream<Schedule> addRandomShifts() {
        return getDateShiftAssignmentMatching(not(DateEmployeeShiftAssignment::isWorkDay))
                .flatMap(dateEmployeeShiftAssignment -> Shift.allWorkingShifts().map(shift -> {
                    DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = dateEmployeeShiftAssignment.setShift(shift);
                    List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = getDateShiftAssignments().collect(toList());
                    List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
                    newSchedule.set(dateEmployeeShiftAssignments.indexOf(dateEmployeeShiftAssignment), dateEmployeeShiftAssignmentWithAddedShift);
                    return Schedule.ofDateEmployeeShiftAssignment(newSchedule);
                }));
    }

    Stream<Schedule> removeRandomShifts() {
        return getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .map(randomDateEmployeeShiftAssignments -> {
                    DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = randomDateEmployeeShiftAssignments.removeShift();
                    List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = getDateShiftAssignments().collect(toList());
                    List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
                    newSchedule.set(dateEmployeeShiftAssignments.indexOf(randomDateEmployeeShiftAssignments), dateEmployeeShiftAssignmentWithAddedShift);
                    return Schedule.ofDateEmployeeShiftAssignment(newSchedule);
                });
    }

    private Stream<DateEmployeeShiftAssignment> getDateShiftAssignmentMatching(Predicate<DateEmployeeShiftAssignment> predicate) {
        return getDateShiftAssignments().filter(predicate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule1 = (Schedule) o;
        return Objects.equals(schedule, schedule1.schedule);
    }

    @Override
    public int hashCode() {

        return Objects.hash(schedule);
    }

    @Override
    public String toString() {
        return ScheduleAsciiTablePresenter.buildAsciiTableRepresentationOf(this);
    }
}
