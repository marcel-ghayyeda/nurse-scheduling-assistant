package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.DAY_OFF;
import static pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee.nurse;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.YearMonthUtil.allDaysOf;

public class ScheduleBuilder {

    private final Collection<Tuple2<LocalDate, EmployeeShiftAssignment>> shiftAssignments = new HashSet<>();
    private Year year = Year.now();
    private Month month = LocalDate.now().getMonth();
    private int numberOfChildren = 1;
    private boolean adjustForMonthLength = false;
    private AllowedWorkingShiftsPerEmployee allowedWorkingShiftPerEmployee;
    private AvailabilityPerEmployee availabilityPerEmployee;

    public static ScheduleBuilder schedule() {
        return new ScheduleBuilder();
    }

    public ScheduleBuilder forYear(int year) {
        this.year = Year.of(year);
        return this;
    }

    public ScheduleBuilder forMonth(Month month) {
        this.month = month;
        return this;
    }

    public ScheduleBuilder removeDay(int monthDay, EmployeeShiftAssignmentBuilder employeeShiftAssignmentBuilder) {
        shiftAssignments.remove(Tuple.of(LocalDate.of(year.getValue(), month, monthDay), employeeShiftAssignmentBuilder.build()));
        return this;
    }

    public ScheduleBuilder onDay(int monthDay, EmployeeShiftAssignmentBuilder employeeShiftAssignmentBuilder) {
        shiftAssignments.add(Tuple.of(LocalDate.of(year.getValue(), month, monthDay), employeeShiftAssignmentBuilder.build()));
        return this;
    }

    public ScheduleBuilder nursesShifts(List<List<Shift>> nursesShifts) {
        var nurseCounter = new AtomicInteger();
        nursesShifts.forEach(nurse -> {
            var dayCounter = new AtomicInteger();
            int nurseNumber = nurseCounter.incrementAndGet();
            nurse.forEach(dayShift ->
                    onDay(dayCounter.incrementAndGet(), employeeShiftAssignment()
                            .employee(nurse("Nurse " + nurseNumber))
                            .shift(dayShift)));
        });
        return this;
    }

    public ScheduleBuilder babySittersShifts(List<List<Shift>> babySittersShifts) {
        var babySitterCounter = new AtomicInteger();
        babySittersShifts.forEach(babySitter -> {
            var dayCounter = new AtomicInteger();
            int babySitterNumber = babySitterCounter.incrementAndGet();
            babySitter.forEach(dayShift ->
                    onDay(dayCounter.incrementAndGet(), employeeShiftAssignment()
                            .employee(nurse("BabySitter " + babySitterNumber))
                            .shift(dayShift)));
        });
        return this;
    }

    public ScheduleBuilder numberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        return this;
    }

    public Schedule build() {
        if (adjustForMonthLength) {
            Map<Integer, Set<EmployeeShiftAssignment>> employeeShiftAssignmentsByMonthDay = shiftAssignments.stream().collect(groupingBy(x -> x._1.getDayOfMonth(), mapping(Tuple2::_2, toSet())));
            var distinctEmployees = shiftAssignments.stream().map(Tuple2::_2).map(EmployeeShiftAssignment::getEmployee).distinct().collect(toSet());
            return allDaysOf(YearMonth.of(year.getValue(), month))
                    .map(buildDateEmployeeShiftAssignments(employeeShiftAssignmentsByMonthDay, distinctEmployees))
                    .collect(collectingAndThen(toSet(), schedule -> new Schedule(schedule, year, month, numberOfChildren, allowedWorkingShiftPerEmployee, availabilityPerEmployee)));

        } else {
            return shiftAssignments.stream()
                    .collect(groupingBy(Tuple2::_1, mapping(Tuple2::_2, toSet())))
                    .entrySet()
                    .stream()
                    .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                    .collect(collectingAndThen(toSet(), schedule -> new Schedule(schedule, year, month, numberOfChildren, allowedWorkingShiftPerEmployee, availabilityPerEmployee)));
        }
    }

    private Function<LocalDate, DateEmployeeShiftAssignments> buildDateEmployeeShiftAssignments(Map<Integer, Set<EmployeeShiftAssignment>> employeeShiftAssignmentsByDate, Collection<Employee> distinctEmployees) {
        return dayOfMonth -> {
            var employeeShiftAssignments = ofNullable(employeeShiftAssignmentsByDate.get(dayOfMonth.getDayOfMonth())).orElseGet(dayOffShiftsForEachEmployee(distinctEmployees));
            return new DateEmployeeShiftAssignments(dayOfMonth, employeeShiftAssignments);
        };
    }

    private Supplier<Set<EmployeeShiftAssignment>> dayOffShiftsForEachEmployee(Collection<Employee> distinctEmployees) {
        return () -> distinctEmployees.stream()
                .map(employee -> new EmployeeShiftAssignment(employee, DAY_OFF)).collect(Collectors.toSet());
    }


    public ScheduleBuilder fromEmployeeShiftMap(Collection<? extends EmployeeShiftMap> employeeShiftMaps) {
        employeeShiftMaps
                .forEach(employeeShiftMap -> employeeShiftMap.getDateShiftMap().forEach((localDate, shift) ->
                        shiftAssignments.add(Tuple.of(localDate, employeeShiftAssignment()
                                .employee(employeeShiftMap.getEmployee())
                                .shift(shift)
                                .build()))
                ));
        return this;
    }

    public ScheduleBuilder adjustForMonthLength() {
        this.adjustForMonthLength = true;
        return this;
    }

    public ScheduleBuilder withAllowedWorkingShiftsPerEmployee(AllowedWorkingShiftsPerEmployee allowedWorkingShiftPerEmployee) {
        this.allowedWorkingShiftPerEmployee = allowedWorkingShiftPerEmployee;
        return this;
    }

    public ScheduleBuilder withAvailabilityPerEmployee(AvailabilityPerEmployee availabilityPerEmployee) {
        this.availabilityPerEmployee = availabilityPerEmployee;
        return this;
    }
}
