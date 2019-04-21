package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.MainLayout;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.NavigationComponents;

import java.util.UUID;

import static com.vaadin.flow.component.icon.VaadinIcon.CHECK_SQUARE;
import static com.vaadin.flow.component.icon.VaadinIcon.COPY;

@Route(value = "schedule-edit", layout = MainLayout.class)
@StyleSheet("frontend://css/schedule.css")
public class ScheduleEditLayout extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver {

    private final NavigationComponents navigationComponents;
    private final ScheduleFacade scheduleFacade;
    private UUID scheduleId;

    public ScheduleEditLayout(NavigationComponents navigationComponents, ScheduleFacade scheduleFacade) {
        this.navigationComponents = navigationComponents;
        this.scheduleFacade = scheduleFacade;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.scheduleId = UUID.fromString(parameter);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        navigationComponents.setActiveSchedules();
        init();
    }

    private void init() {
        removeAll();
        scheduleFacade.getById(scheduleId)
                .ifPresentOrElse(this::presentSchedule, this::scheduleNotFound);
    }

    private void presentSchedule(ScheduleWrapper schedule) {
        VerticalLayout verticalLayout = new VerticalLayout();
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        FormLayout scheduleDetailsForm = new FormLayout();
        TextField scheduleNameField = setValue(new TextField("Schedule name"), schedule.getName());
        scheduleDetailsForm.add(scheduleNameField);
        NumberField numberOfChildrenField = setValue(new NumberField("Number of children"), (double) schedule.getNumberOfChildren());
        numberOfChildrenField.setMin(1);
        numberOfChildrenField.setMax(10);
        numberOfChildrenField.setHasControls(true);
        scheduleDetailsForm.add(numberOfChildrenField);
        accordion.add("Schedule details", scheduleDetailsForm);
        var scheduleTableComponent = new EditableScheduleTableComponent(schedule.getSchedule());
        accordion.add("Schedule", scheduleTableComponent);
        FormLayout actionsForm = new FormLayout();
        var saveAsNewButton = new Button("Save as new");
        saveAsNewButton.setEnabled(true);
        saveAsNewButton.setIcon(COPY.create());
        saveAsNewButton.addClassName("base-active-button");
        saveAsNewButton.addClickListener(saveAsNewButtonClickHandler(scheduleTableComponent, scheduleNameField, numberOfChildrenField));
        saveAsNewButton.setWidthFull();
        var saveButton = new Button("Save");
        saveButton.setEnabled(true);
        saveButton.setIcon(CHECK_SQUARE.create());
        saveButton.addClassName("base-active-button");
        saveButton.addClickListener(saveButtonClickHandler(schedule.getId(), scheduleTableComponent, scheduleNameField, numberOfChildrenField));
        saveButton.setWidthFull();
        actionsForm.add(saveButton, saveAsNewButton);
        HorizontalLayout actionsHorizontalLayout = new HorizontalLayout();
        actionsHorizontalLayout.add(saveAsNewButton);
        actionsHorizontalLayout.add(saveButton);
        actionsHorizontalLayout.setWidthFull();
        verticalLayout.add(accordion);
        verticalLayout.add(actionsHorizontalLayout);
        add(accordion);
        add(verticalLayout);
    }

    private <X extends AbstractField<X, T>, T> X setValue(AbstractField<X, T> field, T value) {
        field.setValue(value);
        return (X) field;
    }

    private ComponentEventListener<ClickEvent<Button>> saveAsNewButtonClickHandler(EditableScheduleTableComponent scheduleTableComponent, TextField scheduleName, NumberField numberOfChildren) {
        return e -> {
            Schedule editedSchedule = scheduleTableComponent.getSchedule();
            Schedule newSchedule = new Schedule(editedSchedule.getDateEmployeeShiftAssignmentsByDate(), editedSchedule.getYear(), editedSchedule.getMonth(), numberOfChildren.getValue().intValue());
            UUID newScheduleId = scheduleFacade.save(newSchedule, scheduleName.getValue());
            UI.getCurrent().navigate("schedule/" + newScheduleId);
        };
    }

    private ComponentEventListener<ClickEvent<Button>> saveButtonClickHandler(UUID scheduleId, EditableScheduleTableComponent scheduleTableComponent, TextField scheduleName, NumberField numberOfChildren) {
        return e -> {
            Schedule editedSchedule = scheduleTableComponent.getSchedule();
            Schedule newSchedule = new Schedule(editedSchedule.getDateEmployeeShiftAssignmentsByDate(), editedSchedule.getYear(), editedSchedule.getMonth(), numberOfChildren.getValue().intValue());
            UUID newScheduleId = scheduleFacade.save(scheduleId, newSchedule, scheduleName.getValue());
            UI.getCurrent().navigate("schedule/" + newScheduleId);
        };
    }

    private void scheduleNotFound() {
        add(new Label("Ooops! Something went wrong. We couldn't find requested schedule. Please try again."));
    }
}
