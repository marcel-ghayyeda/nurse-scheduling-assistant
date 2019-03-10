package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Schedule {

    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    private final List<DateEmployeeShiftAssignments> schedule;
    private final Year year;
    private final Month month;
    private final int numberOfChildren;

    Schedule(List<DateEmployeeShiftAssignments> schedule, Year year, Month month, int numberOfChildren) {
        this.schedule = schedule;
        this.year = year;
        this.month = month;
        this.numberOfChildren = numberOfChildren;
    }

    private static Schedule ofDateEmployeeShiftAssignment(List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments, Year year, Month month, int numberOfChildren) {
        return dateEmployeeShiftAssignments.stream()
                .collect(groupingBy(DateEmployeeShiftAssignment::getStartDate, mapping(DateEmployeeShiftAssignment::getEmployeeShiftAssignment, toList())))
                .entrySet()
                .stream()
                .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                .collect(Collectors.collectingAndThen(toList(), schedule1 -> new Schedule(schedule1, year, month, numberOfChildren)));
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

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public Year getYear() {
        return year;
    }

    public Month getMonth() {
        return month;
    }

    public List<Schedule> getNeighbourhood() {
        var neighbourhood = Stream.concat(addRandomShifts(), removeRandomShifts()).collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return neighbourhood;
    }

    Stream<Schedule> addRandomShifts() {
        return getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isDayOff)
                .flatMap(dateEmployeeShiftAssignment -> Shift.allWorkingShifts().map(shift -> {
                    DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = dateEmployeeShiftAssignment.setShift(shift);
                    List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = getDateShiftAssignments().collect(toList());
                    List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
                    newSchedule.set(dateEmployeeShiftAssignments.indexOf(dateEmployeeShiftAssignment), dateEmployeeShiftAssignmentWithAddedShift);
                    return Schedule.ofDateEmployeeShiftAssignment(newSchedule, year, month, numberOfChildren);
                }));
    }

    Stream<Schedule> removeRandomShifts() {
        return getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .map(randomDateEmployeeShiftAssignments -> {
                    DateEmployeeShiftAssignment dateEmployeeShiftAssignmentWithAddedShift = randomDateEmployeeShiftAssignments.removeShift();
                    List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments = getDateShiftAssignments().collect(toList());
                    List<DateEmployeeShiftAssignment> newSchedule = new ArrayList<>(dateEmployeeShiftAssignments);
                    newSchedule.set(dateEmployeeShiftAssignments.indexOf(randomDateEmployeeShiftAssignments), dateEmployeeShiftAssignmentWithAddedShift);
                    return Schedule.ofDateEmployeeShiftAssignment(newSchedule, year, month, numberOfChildren);
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
        return numberOfChildren == schedule1.numberOfChildren &&
                Objects.equals(schedule, schedule1.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedule);
    }

    @Override
    public String toString() {
        return "Number of children: " + numberOfChildren + "\n" + ScheduleAsciiTablePresenter.buildAsciiTableRepresentationOf(this);
    }
}
