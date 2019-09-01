package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeAvailability;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.vaadin.flow.component.icon.VaadinIcon.*;
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
            Button deleteButton = new Button();
            deleteButton.setIcon(MINUS_CIRCLE.create());
            deleteButton.addClassNames("delete-employee-button", "button-with-icon");
            deleteButton.addClickListener(clickEvent -> {
                items.remove(scheduleLayoutRow);
                getDataProvider().refreshAll();

            });

            Button settingsButton = new Button();
            settingsButton.setIcon(COG.create());
            settingsButton.addClassNames("configure-employee-button", "button-with-icon");
            settingsButton.addClickListener(event -> {
                Set<Shift> allowedShifts = new HashSet<>(allowedWorkingShiftPerEmployee.getAllowedWorkingShiftsFor(scheduleLayoutRow.getEmployee()));
                EmployeeAvailability employeeAvailability = availabilityPerEmployee.getAvailabilityFor(scheduleLayoutRow.getEmployee());
                Dialog dialog = new Dialog();

                var dialogMessage = new Label("Configure employee");

                FormLayout formLayout = new FormLayout();

                Checkbox morningShift = new Checkbox();
                morningShift.setLabel("Morning");
                morningShift.setValue(allowedShifts.contains(Shift.MORNING));
                morningShift.addValueChangeListener(handleValueChange(allowedShifts, Shift.MORNING));

                Checkbox afternoonShift = new Checkbox();
                afternoonShift.setLabel("Afternoon");
                afternoonShift.setValue(allowedShifts.contains(Shift.AFTERNOON));
                morningShift.addValueChangeListener(handleValueChange(allowedShifts, Shift.AFTERNOON));

                Checkbox dayShift = new Checkbox();
                dayShift.setLabel("Day");
                dayShift.setValue(allowedShifts.contains(Shift.DAY));
                dayShift.addValueChangeListener(handleValueChange(allowedShifts, Shift.DAY));

                Checkbox nightShift = new Checkbox();
                nightShift.setLabel("Night");
                nightShift.setValue(allowedShifts.contains(Shift.NIGHT));
                nightShift.addValueChangeListener(handleValueChange(allowedShifts, Shift.NIGHT));

                Checkbox dayNightShift = new Checkbox();
                dayNightShift.setLabel("Day-night");
                dayNightShift.setValue(allowedShifts.contains(Shift.DAY_NIGHT));
                dayNightShift.addValueChangeListener(handleValueChange(allowedShifts, Shift.DAY_NIGHT));

                Select<EmployeeAvailability> availabilitySelect = new Select<>();
                availabilitySelect.setLabel("Availability");
                availabilitySelect.setItems(EmployeeAvailability.values());
                availabilitySelect.setValue(employeeAvailability);
                availabilitySelect.setEmptySelectionAllowed(false);
                availabilitySelect.setItemLabelGenerator(EmployeeAvailability::getLabel);


                formLayout.add(morningShift);
                formLayout.add(afternoonShift);
                formLayout.add(dayShift);
                formLayout.add(nightShift);
                formLayout.add(dayNightShift);

                var okButton = new Button("OK", addEmployeeEvent -> {
                    allowedWorkingShiftPerEmployee.set(scheduleLayoutRow.employee, allowedShifts);
                    availabilityPerEmployee.set(scheduleLayoutRow.employee, availabilitySelect.getValue());
                    dialog.close();
                });

                VerticalLayout verticalLayout = new VerticalLayout();
                verticalLayout.add(centered(dialogMessage));
                verticalLayout.add(new Label("Select allowed working shifts"));
                verticalLayout.add(formLayout);
                verticalLayout.add(new Label("Select availability"));
                verticalLayout.add(availabilitySelect);
                verticalLayout.add(centered(okButton));
                dialog.add(verticalLayout);
                dialog.setWidth("500px");
                dialog.open();
            });

            horizontalLayout.add(deleteButton);
            horizontalLayout.add(settingsButton);
            horizontalLayout.add(employeeName);
            return horizontalLayout;
        };
    }

    private HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>> handleValueChange(Set<Shift> allowedShifts, Shift morning) {
        return valueChangeEvent -> {
            if (valueChangeEvent.getValue()) {
                allowedShifts.add(morning);
            } else {
                allowedShifts.remove(morning);
            }
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
