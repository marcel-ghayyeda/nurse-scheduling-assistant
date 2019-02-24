package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class ScheduleBuilder {

    private final Collection<Tuple2<LocalDate, EmployeeShiftAssignment>> shiftAssignments = new LinkedList<>();
    private int year = Year.now().getValue();
    private Month month = LocalDate.now().getMonth();

    public static ScheduleBuilder schedule() {
        return new ScheduleBuilder();
    }

    public ScheduleBuilder forYear(int year) {
        this.year = year;
        return this;
    }

    public ScheduleBuilder forMonth(Month month) {
        this.month = month;
        return this;
    }

    public ScheduleBuilder onDay(int monthDay, EmployeeShiftAssignmentBuilder employeeShiftAssignmentBuilder) {
        shiftAssignments.add(Tuple.of(LocalDate.of(year, month, monthDay), employeeShiftAssignmentBuilder.build()));
        return this;
    }

    public Schedule build() {
        return shiftAssignments.stream()
                .collect(groupingBy(Tuple2::_1, mapping(Tuple2::_2, toList())))
                .entrySet()
                .stream()
                .map(entry -> new DateEmployeeShiftAssignments(entry.getKey(), entry.getValue()))
                .collect(Collectors.collectingAndThen(toList(), Schedule::new));
    }

}
