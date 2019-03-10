package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.DateEmployeeShiftAssignments;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Locale.US;
import static java.util.stream.Collectors.*;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.SICK_LEAVE;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.VACATION;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.withCssClass;

class ScheduleTableComponent extends Grid<ScheduleTableComponent.ScheduleLayoutRow> {

    //TODO use common formatter with ascii presented
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d").localizedBy(US);

    ScheduleTableComponent(Schedule schedule) {
        List<LocalDate> dates = schedule.getDateEmployeeShiftAssignmentsByDate().stream().map(DateEmployeeShiftAssignments::getStartDate).sorted().collect(toList());
        addEmployeeColumn();
        dates.forEach(this::addShiftColumn);
        var scheduleLayoutRows = schedule.getDateShiftAssignments().collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .entrySet()
                .stream()
                .map(entry -> new ScheduleLayoutRow(entry.getKey(), entry.getValue().stream().collect(toMap(DateEmployeeShiftAssignment::getStartDate, DateEmployeeShiftAssignment::getShift))))
                .sorted(Comparator.comparing(x -> x.employee.getName()))
                .collect(Collectors.toList());


        setItems(scheduleLayoutRows);
    }

    private void addEmployeeColumn() {
        addComponentColumn(scheduleLayoutRow -> withCssClass("employee-row", new Span(scheduleLayoutRow.employee.getName())))
                .setHeader(employeeHeader())
                .setFlexGrow(0);
    }

    private Component employeeHeader() {
        var span = new Span("Employee");
        span.addClassName("employee-header");
        return span;
    }

    private void addShiftColumn(LocalDate date) {
        addComponentColumn(scheduleLayoutRow -> {
            var shift = scheduleLayoutRow.shifts.get(date);
            var div = new Div(new Span(shift.getLocalizedShiftSymbol()));
            if (SICK_LEAVE == shift) {
                div.addClassName("sick-leave");
            } else if (VACATION == shift) {
                div.addClassName("vacation");
            }
            return div;
        })
                .setHeader(dateHeader(date))
                .setWidth("55px")
                .setFlexGrow(0);
    }

    private Component dateHeader(LocalDate date) {
        var div = new Div(new Span(formatter.format(date)));
        div.addClassName("date-header");
        var dayOfWeek = date.getDayOfWeek();
        if (SUNDAY == dayOfWeek) {
            div.addClassName("date-header-sunday");
        } else if (SATURDAY == dayOfWeek) {
            div.addClassName("date-header-saturday");
        }
        return div;
    }

    static class ScheduleLayoutRow {

        Employee employee;
        Map<LocalDate, Shift> shifts;

        ScheduleLayoutRow(Employee employee, Map<LocalDate, Shift> shifts) {
            this.employee = employee;
            this.shifts = shifts;
        }
    }
}
