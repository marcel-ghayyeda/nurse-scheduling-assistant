package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ScheduleBuilder {

    private final TreeMap<LocalDate, List<EmployeeShiftAssignment>> schedule = new TreeMap<>();
    private int year;
    private Month month;

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
        schedule.compute(LocalDate.of(year, month, monthDay), (k, v) -> {
            final EmployeeShiftAssignment employeeShiftAssignment = employeeShiftAssignmentBuilder.build();
            return v != null ? concat(v, employeeShiftAssignment) : List.of(employeeShiftAssignment);
        });
        return this;
    }

    public Schedule build() {
        return new Schedule(schedule);
    }

    private List<EmployeeShiftAssignment> concat(List<EmployeeShiftAssignment> v, EmployeeShiftAssignment employeeShiftAssignment) {
        return Stream.concat(v.stream(), Stream.of(employeeShiftAssignment)).collect(toList());
    }
}
