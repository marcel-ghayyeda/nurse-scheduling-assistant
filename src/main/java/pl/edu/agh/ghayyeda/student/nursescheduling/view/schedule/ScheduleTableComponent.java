package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.function.ValueProvider;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.*;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Comparator.comparing;
import static java.util.Locale.US;
import static java.util.stream.Collectors.*;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.SICK_LEAVE;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.VACATION;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.withCssClass;

public class ScheduleTableComponent extends Grid<ScheduleTableComponent.ScheduleLayoutRow> {

    //TODO use common formatter with ascii presented
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d").localizedBy(US);
    private final Schedule initialSchedule;
    protected Collection<ScheduleLayoutRow> items;
    protected AllowedWorkingShiftsPerEmployee allowedWorkingShiftPerEmployee;
    protected AvailabilityPerEmployee availabilityPerEmployee;

    public ScheduleTableComponent(Schedule initialSchedule) {
        this.initialSchedule = initialSchedule;
        List<LocalDate> dates = initialSchedule.getDateEmployeeShiftAssignmentsByDate().stream().map(DateEmployeeShiftAssignments::getStartDate).sorted().collect(toList());
        addEmployeeColumn();
        dates.forEach(this::addShiftColumn);
        var scheduleLayoutRows = initialSchedule.getDateShiftAssignments().collect(groupingBy(DateEmployeeShiftAssignment::getEmployee))
                .entrySet()
                .stream()
                .map(entry -> new ScheduleLayoutRow(entry.getKey(), entry.getValue().stream().collect(toMap(DateEmployeeShiftAssignment::getStartDate, DateEmployeeShiftAssignment::getShift))))
                .sorted(comparing(row -> row.employee.getName()))
                .collect(Collectors.toList());

        items = scheduleLayoutRows;
        allowedWorkingShiftPerEmployee = initialSchedule.getAllowedWorkingShiftPerEmployee();
        availabilityPerEmployee = initialSchedule.getAvailabilityPerEmployee();
        setItems(items);
    }

    public YearMonth getYearMonth() {
        return initialSchedule.getYearMonth();
    }

    public Schedule buildSchedule(Year year, Month month, int numberOfChildren) {
        return ScheduleBuilder.schedule()
                .numberOfChildren(numberOfChildren)
                .forMonth(month)
                .fromEmployeeShiftMap(items)
                .forYear(year.getValue())
                .withAllowedWorkingShiftsPerEmployee(allowedWorkingShiftPerEmployee)
                .withAvailabilityPerEmployee(availabilityPerEmployee)
                .adjustForMonthLength()
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
            var shift = scheduleLayoutRow.shifts.getOrDefault(date, Shift.DAY_OFF);
            var div = new Div(new Span(shift.getLocalizedShiftSymbol()));
            if (SICK_LEAVE == shift) {
                div.addClassName("sick-leave");
            } else if (VACATION == shift) {
                div.addClassName("vacation");
            }
            if (scheduleLayoutRow.getHighlightedDates().contains(date)) {
                div.addClassName("highlighted");

            }
            return div;
        };
    }

    private void addEmployeeColumn() {
        addComponentColumn(createEmployeeColumn())
                .setHeader(createEmployeeHeader())
                .setWidth("200px")
                .setFlexGrow(0);
    }

    protected ValueProvider<ScheduleLayoutRow, Component> createEmployeeColumn() {
        return scheduleLayoutRow -> withCssClass("employee-row", employeeName(scheduleLayoutRow));
    }

    private Component employeeName(ScheduleLayoutRow scheduleLayoutRow) {
        Span span = new Span(format("%s (%s)", scheduleLayoutRow.employee.getName(), scheduleLayoutRow.employee.getType().getName()));

        var employeeAvailability = availabilityPerEmployee.getAvailabilityFor(scheduleLayoutRow.getEmployee());
        String allowedShifts = allowedWorkingShiftPerEmployee.getAllowedWorkingShiftsFor(scheduleLayoutRow.getEmployee()).stream().map(Shift::getLocalizedShiftSymbol).collect(joining(","));
        Long workLoad = initialSchedule.getWorkHoursPerEmployee().get(scheduleLayoutRow.employee);
        Div tooltip = new Div(new Span(employeeAvailability.getLabel() +":" + allowedShifts +"; Current workload: " + workLoad + "h"));
        tooltip.setClassName("employee-tooltip");

        Div div = new Div();
        div.setClassName("employee-name");
        div.add(span);
        div.add(tooltip);
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

    void unhighlight() {
        items.forEach(ScheduleLayoutRow::unHighlight);
        getDataProvider().refreshAll();
    }

    void highlight(Collection<EmployeeDateViolation> employeeDateViolations) {
        items.forEach(ScheduleLayoutRow::unHighlight);
        employeeDateViolations.forEach(employeeDateViolation -> {
            items.forEach(scheduleLayoutRow -> {
                if (!employeeDateViolation.getEmployee().isPresent() || employeeDateViolation.getEmployee().get().equals(scheduleLayoutRow.getEmployee())) {
                    scheduleLayoutRow.highlight(employeeDateViolation.getDate());
                }
            });
        });
        getDataProvider().refreshAll();

    }

    protected static class ScheduleLayoutRow implements EmployeeShiftMap {

        Employee employee;
        Map<LocalDate, Shift> shifts;
        Set<LocalDate> highlightedDates = new HashSet<>();

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

        public void highlight(LocalDate date) {
            highlightedDates.add(date);
        }

        public void unHighlight() {
            highlightedDates = new HashSet<>();
        }

        public Set<LocalDate> getHighlightedDates() {
            return highlightedDates;
        }
    }
}
