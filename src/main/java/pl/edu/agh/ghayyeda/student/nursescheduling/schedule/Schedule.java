package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Schedule {

    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    private final List<DateEmployeeShiftAssignments> schedule;
    private final Year year;
    private final Month month;
    private final int numberOfChildren;

    public Schedule(List<DateEmployeeShiftAssignments> schedule, Year year, Month month, int numberOfChildren) {
        this.schedule = schedule;
        this.year = year;
        this.month = month;
        this.numberOfChildren = numberOfChildren;
    }

    public static Schedule ofDateEmployeeShiftAssignment(List<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments, Year year, Month month, int numberOfChildren) {
        return dateEmployeeShiftAssignments.stream()
                .collect(groupingBy(DateEmployeeShiftAssignment::getStartDate, mapping(DateEmployeeShiftAssignment::getEmployeeShiftAssignment, toList())))
                .entrySet()
                .stream()
                .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                .collect(collectingAndThen(toList(), schedule1 -> new Schedule(schedule1, year, month, numberOfChildren)));
    }

    public List<DateEmployeeShiftAssignments> getDateEmployeeShiftAssignmentsByDate() {
        return schedule;
    }

    public Map<Employee, Long> getWorkHoursPerEmployee() {
        return getDateShiftAssignments()
                .collect(groupingBy(DateEmployeeShiftAssignment::getEmployee, mapping(DateEmployeeShiftAssignment::getShiftDuration, summingLong(Duration::toHours))));
    }

    public Stream<EmployeeShiftAssignment> getEmployeeShiftAssignmentsFor(LocalDateTime localDateTime) {
        return schedule.stream()
                .filter(dateEmployeeShiftAssignments -> !dateEmployeeShiftAssignments.getStartDate().isAfter(localDateTime.toLocalDate()))
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

    public YearMonth getYearMonth() {
        return YearMonth.of(getYear().getValue(), getMonth());
    }

    public Month getMonth() {
        return month;
    }

    public Stream<DateEmployeeShiftAssignment> getDateShiftAssignmentMatching(Predicate<DateEmployeeShiftAssignment> predicate) {
        return getDateShiftAssignments().filter(predicate);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule1 = (Schedule) o;
        return numberOfChildren == schedule1.numberOfChildren &&
                Objects.equals(schedule, schedule1.schedule) &&
                Objects.equals(year, schedule1.year) &&
                month == schedule1.month;
    }

    @Override
    public int hashCode() {

        return Objects.hash(schedule, year, month, numberOfChildren);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "schedule=" + ScheduleAsciiTablePresenter.buildAsciiTableRepresentationOf(this) +
                ", year=" + year +
                ", month=" + month +
                ", numberOfChildren=" + numberOfChildren +
                '}';
    }
}
