package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment;
import static pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee.nurse;

public class ScheduleBuilder {

    private final Collection<Tuple2<LocalDate, EmployeeShiftAssignment>> shiftAssignments = new LinkedList<>();
    private Year year = Year.now();
    private Month month = LocalDate.now().getMonth();
    private int numberOfChildren = 1;

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
        return shiftAssignments.stream()
                .collect(groupingBy(Tuple2::_1, mapping(Tuple2::_2, toList())))
                .entrySet()
                .stream()
                .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                .collect(collectingAndThen(toList(), schedule -> new Schedule(schedule, year, month, numberOfChildren)));
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
}
