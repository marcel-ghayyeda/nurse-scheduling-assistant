package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;

import static com.vaadin.flow.component.icon.VaadinIcon.MINUS_CIRCLE;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS_CIRCLE;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*;
import static pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee.Type.NURSE;
import static pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee.employee;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.YearMonthUtil.allDaysOf;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.*;

class EditableScheduleTableComponent extends ScheduleTableComponent {

    EditableScheduleTableComponent(Schedule initialSchedule) {
        super(initialSchedule);
    }

    @Override
    protected Component createEmployeeHeader() {
        Component employeeHeader = setCssClass("editable-employee-header", super.createEmployeeHeader());
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button button = new Button();
        button.setIcon(PLUS_CIRCLE.create());
        button.addClassNames("add-employee-button", "button-with-icon");
        button.addClickListener(clickEvent -> {
            Dialog dialog = new Dialog();

            var dialogMessage = new Label("Add new employee");

            ComboBox<Employee.Type> employeeTypeComboBox = new ComboBox<>("Employee type");
            employeeTypeComboBox.setItemLabelGenerator(Employee.Type::getName);
            employeeTypeComboBox.setItems(Employee.Type.values());
            employeeTypeComboBox.setValue(NURSE);
            employeeTypeComboBox.setAllowCustomValue(false);


            TextField employeeName = setValue(new TextField("Employee name"), "New Employee");
            var addButton = new Button("Add", addEmployeeEvent -> {
                if (!employeeName.isEmpty() && !employeeTypeComboBox.isEmpty()) {
                    var shifts = allDaysOf(getYearMonth()).collect(toMap(identity(), __ -> Shift.DAY_OFF));
                    items.add(new ScheduleLayoutRow(employee(employeeName.getValue(), employeeTypeComboBox.getValue()), shifts));
                    dialog.close();
                    getDataProvider().refreshAll();
                }
            });


            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(centered(dialogMessage));
            HorizontalLayout horizontalLayout1 = new HorizontalLayout();
            horizontalLayout1.add(employeeTypeComboBox);
            horizontalLayout1.add(employeeName);
            verticalLayout.add(centered(horizontalLayout1));
            verticalLayout.add(centered(addButton));
            dialog.add(verticalLayout);
            dialog.setWidth("500px");
            dialog.open();

        });
        horizontalLayout.add(button);
        horizontalLayout.add(employeeHeader);
        return horizontalLayout;
    }

    @Override
    protected ValueProvider<ScheduleLayoutRow, Component> createEmployeeColumn() {
        return scheduleLayoutRow -> {
            var employeeName = setCssClass("editable-employee-row", super.createEmployeeColumn().apply(scheduleLayoutRow));
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Button button = new Button();
            button.setIcon(MINUS_CIRCLE.create());
            button.addClassNames("delete-employee-button", "button-with-icon");
            button.addClickListener(clickEvent -> {
                items.remove(scheduleLayoutRow);
                getDataProvider().refreshAll();

            });
            horizontalLayout.add(button);
            horizontalLayout.add(employeeName);
            return horizontalLayout;
        };
    }

    @Override
    protected ValueProvider<ScheduleLayoutRow, Div> createShiftComponentColumn(LocalDate date) {
        return scheduleLayoutRow -> {
            var column = super.createShiftComponentColumn(date).apply(scheduleLayoutRow);
            column.addClassName("shift");
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(column);
            Shift shift = scheduleLayoutRow.getDateShiftMap().get(date);
            if (shift != VACATION) {
                contextMenu.addItem("Set vacation", e -> {
                    scheduleLayoutRow.shifts.put(date, VACATION);
                    getDataProvider().refreshItem(scheduleLayoutRow);
                });
            }
            if (shift != SICK_LEAVE) {
                contextMenu.addItem("Set sick leave", e -> {
                    scheduleLayoutRow.shifts.put(date, SICK_LEAVE);
                    getDataProvider().refreshItem(scheduleLayoutRow);
                });
            }

            if (shift == VACATION || shift == SICK_LEAVE) {
                contextMenu.addItem("Remove assignment", e -> {
                    scheduleLayoutRow.shifts.put(date, DAY_OFF);
                    getDataProvider().refreshItem(scheduleLayoutRow);
                });
            }
            return column;
        };
    }
}
