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
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Locale.US;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.concat;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.withCssClass;

public class SchedulesDiffTableComponent extends Grid<SchedulesDiffTableComponent.ScheduleLayoutRow> {

    //TODO use common formatter with ascii presented
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d").localizedBy(US);
    protected Collection<ScheduleLayoutRow> items;

    public SchedulesDiffTableComponent(ScheduleWrapper firstScheduleWrapper, ScheduleWrapper secondScheduleWrapper) {
        var firstSchedule = firstScheduleWrapper.getSchedule();
        var secondSchedule = secondScheduleWrapper.getSchedule();
        List<ScheduleLayoutRow> firstScheduleLayoutRows = getScheduleLayoutRows(firstSchedule);
        List<ScheduleLayoutRow> secondScheduleLayoutRows = getScheduleLayoutRows(secondSchedule);

        List<Integer> dayOsMonths = concat(firstSchedule.getDateEmployeeShiftAssignmentsByDate().stream(), secondSchedule.getDateEmployeeShiftAssignmentsByDate().stream())
                .map(DateEmployeeShiftAssignments::getStartDate)
                .map(LocalDate::getDayOfMonth)
                .distinct()
                .sorted()
                .collect(toList());

        addEmployeeColumn();
        dayOsMonths.forEach(addShiftColumn(getShiftAssignmentsByDate(firstSchedule), getShiftAssignmentsByDate(secondSchedule)));


        items = Stream.of(Stream.of(ScheduleLayoutRow.dummy(firstScheduleWrapper.getName())), firstScheduleLayoutRows.stream(), Stream.of(ScheduleLayoutRow.dummy(secondScheduleWrapper.getName())), secondScheduleLayoutRows.stream())
                .flatMap(Function.identity())
                .collect(toList());
        setItems(items);
    }

    private List<ScheduleLayoutRow> getScheduleLayoutRows(Schedule secondSchedule) {
        return secondSchedule.getDateShiftAssignments().collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .entrySet()
                .stream()
                .map(entry -> new ScheduleLayoutRow(entry.getKey(), entry.getValue().stream().collect(toMap(x -> x.getStartDate().getDayOfMonth(), DateEmployeeShiftAssignment::getShift))))
                .sorted(comparing(row -> row.employee.getName()))
                .collect(toList());
    }

    private Map<Integer, List<EmployeeShiftAssignment>> getShiftAssignmentsByDate(Schedule firstSchedule) {
        return firstSchedule.getDateEmployeeShiftAssignmentsByDate()
                .stream()
                .collect(groupingBy(employeeShiftAssignments -> employeeShiftAssignments.getStartDate().getDayOfMonth(), flatMapping(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.getShiftAssignments().stream(), toList())));
    }


    private Consumer<Integer> addShiftColumn(Map<Integer, List<EmployeeShiftAssignment>> firstScheduleAssignmentsByDate, Map<Integer, List<EmployeeShiftAssignment>> secondScheduleAssignmentsByDate) {
        return date -> addComponentColumn(createShiftComponentColumn(date, firstScheduleAssignmentsByDate, secondScheduleAssignmentsByDate))
                .setHeader(dateHeader(date))
                .setWidth("55px")
                .setFlexGrow(0);
    }

    protected ValueProvider<ScheduleLayoutRow, Div> createShiftComponentColumn(Integer date, Map<Integer, List<EmployeeShiftAssignment>> firstScheduleAssignmentsByDate, Map<Integer, List<EmployeeShiftAssignment>> secondScheduleAssignmentsByDate) {
        return scheduleLayoutRow -> {
            if (scheduleLayoutRow.isDummy()) {
                return withCssClass("schedule-name-row", new Div(new Span("-")));
            }
            Optional<Shift> shift = Optional.ofNullable(scheduleLayoutRow.shifts.get(date));
            var shiftDescription = shift.map(Shift::getLocalizedShiftSymbol).orElse("-");
            var div = new Div(new Span(shiftDescription));

            Optional<Shift> firstScheduleShift = firstScheduleAssignmentsByDate.getOrDefault(date, List.of())
                    .stream()
                    .filter(employeeShiftAssignment -> Objects.equals(employeeShiftAssignment.getEmployee(), scheduleLayoutRow.getEmployee()))
                    .map(EmployeeShiftAssignment::getShift)
                    .findFirst();
            Optional<Shift> secondScheduleShift = secondScheduleAssignmentsByDate.getOrDefault(date, List.of())
                    .stream()
                    .filter(employeeShiftAssignment -> Objects.equals(employeeShiftAssignment.getEmployee(), scheduleLayoutRow.getEmployee()))
                    .map(EmployeeShiftAssignment::getShift)
                    .findFirst();

            if (!firstScheduleShift.isPresent() || !secondScheduleShift.isPresent()) {
                div.addClassName("shift-missing");

            } else if (!Objects.equals(firstScheduleShift, secondScheduleShift)) {
                div.addClassName("shift-difference");
            }


            return div;
        };
    }

    private void addEmployeeColumn() {
        addComponentColumn(createEmployeeColumn())
                .setHeader(createEmployeeHeader())
                .setWidth("180px")
                .setFlexGrow(0);
    }

    protected ValueProvider<ScheduleLayoutRow, Component> createEmployeeColumn() {
        return scheduleLayoutRow -> withCssClass("employee-row", employeeName(scheduleLayoutRow));
    }

    private Component employeeName(ScheduleLayoutRow scheduleLayoutRow) {
        if (scheduleLayoutRow.isDummy()) {
            Span span = new Span(scheduleLayoutRow.getEmployee().getName());
            Div div = withCssClass("schedule-name-row", new Div());
            div.add(span);
            div.setWidthFull();
            return div;
        }
        Span span = new Span(format("%s (%s)", scheduleLayoutRow.employee.getName(), scheduleLayoutRow.employee.getType().getName()));
        Div div = new Div();
        div.add(span);
        div.setWidthFull();
        return div;
    }

    protected Component createEmployeeHeader() {
        var span = new Span("Employee");
        Div div = new Div();
        div.add(span);
        div.setWidth("180px");
        return withCssClass("employee-header", div);
    }

    private Component dateHeader(Integer date) {
        var div = new Div(new Span(String.valueOf(date)));
        div.addClassName("date-header");
        return div;
    }

    protected static class ScheduleLayoutRow {

        Employee employee;
        Map<Integer, Shift> shifts;
        boolean dummy = false;

        public static ScheduleLayoutRow dummy(String text) {
            return new ScheduleLayoutRow(text, Map.of());
        }

        private ScheduleLayoutRow(String text, Map<Integer, Shift> shifts) {
            this.employee = Employee.employee(text, null);
            this.shifts = shifts;
            this.dummy = true;
        }

        ScheduleLayoutRow(Employee employee, Map<Integer, Shift> shifts) {
            this.employee = employee;
            this.shifts = shifts;
        }

        public Employee getEmployee() {
            return employee;
        }

        public Map<Integer, Shift> getDateShiftMap() {
            return shifts;
        }

        public boolean isDummy() {
            return dummy;
        }
    }
}
