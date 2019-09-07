package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

public class Schedule {

    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    private final Collection<DateEmployeeShiftAssignments> schedule;
    private final Year year;
    private final Month month;
    private final int numberOfChildren;
    private final AllowedWorkingShiftsPerEmployee allowedWorkingShiftPerEmployee;
    private final AvailabilityPerEmployee availabilityPerEmployee;

    public Schedule(Collection<DateEmployeeShiftAssignments> schedule, Year year, Month month, int numberOfChildren, AllowedWorkingShiftsPerEmployee allowedWorkingShiftPerEmployee, AvailabilityPerEmployee availabilityPerEmployee) {
        this.schedule = schedule;
        this.year = year;
        this.month = month;
        this.numberOfChildren = numberOfChildren;
        this.allowedWorkingShiftPerEmployee = ofNullable(allowedWorkingShiftPerEmployee).orElseGet(AllowedWorkingShiftsPerEmployee::empty);
        this.availabilityPerEmployee = ofNullable(availabilityPerEmployee).orElseGet(AvailabilityPerEmployee::empty);
    }

    public static Schedule ofDateEmployeeShiftAssignment(Collection<DateEmployeeShiftAssignment> dateEmployeeShiftAssignments, Year year, Month month, int numberOfChildren, AllowedWorkingShiftsPerEmployee allowedWorkingShiftPerEmployee, AvailabilityPerEmployee availabilityPerEmployee) {
        return dateEmployeeShiftAssignments.stream()
                .collect(groupingBy(DateEmployeeShiftAssignment::getStartDate, mapping(DateEmployeeShiftAssignment::getEmployeeShiftAssignment, toSet())))
                .entrySet()
                .stream()
                .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                .collect(collectingAndThen(toSet(), schedule1 -> new Schedule(schedule1, year, month, numberOfChildren, allowedWorkingShiftPerEmployee, availabilityPerEmployee)));
    }

    public Collection<Shift> getAllowedWorkingShiftsFor(Employee employee) {
        return allowedWorkingShiftPerEmployee.getAllowedWorkingShiftsFor(employee);
    }

    public AvailabilityPerEmployee getAvailabilityPerEmployee() {
        return availabilityPerEmployee;
    }

    public boolean isAllowedShift(Employee employee, Shift shift) {
        return !shift.isWorkDay() || allowedWorkingShiftPerEmployee.getAllowedWorkingShiftsFor(employee).contains(shift);
    }

    public EmployeeAvailability getAvailabilityFor(Employee employee) {
        return availabilityPerEmployee.getAvailabilityFor(employee);
    }

    public AllowedWorkingShiftsPerEmployee getAllowedWorkingShiftPerEmployee() {
        return allowedWorkingShiftPerEmployee;
    }

    public Collection<DateEmployeeShiftAssignments> getDateEmployeeShiftAssignmentsByDate() {
        return schedule;
    }

    public long getNumberOfEmployees() {
        return schedule.stream()
                .map(DateEmployeeShiftAssignments::getShiftAssignments)
                .flatMap(Collection::stream)
                .map(EmployeeShiftAssignment::getEmployee)
                .distinct()
                .count();
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
