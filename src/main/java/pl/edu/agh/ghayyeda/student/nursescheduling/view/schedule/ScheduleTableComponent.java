package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.function.ValueProvider;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.*;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
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
    private final Schedule initialSchedule;
    private Collection<ScheduleLayoutRow> items;

    ScheduleTableComponent(Schedule initialSchedule) {
        this.initialSchedule = initialSchedule;
        List<LocalDate> dates = initialSchedule.getDateEmployeeShiftAssignmentsByDate().stream().map(DateEmployeeShiftAssignments::getStartDate).sorted().collect(toList());
        addEmployeeColumn();
        dates.forEach(this::addShiftColumn);
        var scheduleLayoutRows = initialSchedule.getDateShiftAssignments().collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .entrySet()
                .stream()
                .map(entry -> new ScheduleLayoutRow(entry.getKey(), entry.getValue().stream().collect(toMap(DateEmployeeShiftAssignment::getStartDate, DateEmployeeShiftAssignment::getShift))))
                .sorted(Comparator.comparing(x -> x.employee.getName()))
                .collect(Collectors.toList());

        items = scheduleLayoutRows;
        setItems(items);
    }

    public Schedule getSchedule() {
        return ScheduleBuilder.schedule()
                .numberOfChildren(initialSchedule.getNumberOfChildren())
                .forMonth(initialSchedule.getMonth())
                .fromEmployeeShiftMap(items)
                .forYear(initialSchedule.getYear().getValue())
                .build();
    }

    private Column<ScheduleLayoutRow> addShiftColumn(LocalDate date) {
        return addComponentColumn(createShiftComponentColumn(date))
                .setHeader(dateHeader(date))
                .setWidth("55px")
                .setFlexGrow(0);
    }

    protected ValueProvider<ScheduleLayoutRow, Div> createShiftComponentColumn(LocalDate date) {
        return scheduleLayoutRow -> {
            var shift = scheduleLayoutRow.shifts.get(date);
            var div = new Div(new Span(shift.getLocalizedShiftSymbol()));
            if (SICK_LEAVE == shift) {
                div.addClassName("sick-leave");
            } else if (VACATION == shift) {
                div.addClassName("vacation");
            }
            return div;
        };
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

    protected static class ScheduleLayoutRow implements EmployeeShiftMap {

        Employee employee;
        Map<LocalDate, Shift> shifts;

        ScheduleLayoutRow(Employee employee, Map<LocalDate, Shift> shifts) {
            this.employee = employee;
            this.shifts = shifts;
        }

        @Override
        public Employee getEmployee() {
            return employee;
        }

        @Override
        public Map<LocalDate, Shift> getDateShiftMap() {
            return shifts;
        }
    }
}
