package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.details.Details;
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

import java.time.Month;
import java.time.Year;
import java.util.UUID;

import static com.vaadin.flow.component.icon.VaadinIcon.CHECK_SQUARE;
import static com.vaadin.flow.component.icon.VaadinIcon.COPY;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.setValue;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.withCssClass;

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
        FormLayout scheduleDetailsForm = new FormLayout();
        TextField scheduleNameField = setValue(new TextField("Schedule name"), schedule.getName());
        scheduleDetailsForm.add(scheduleNameField);
        NumberField numberOfChildrenField = setValue(new NumberField("Number of children"), (double) schedule.getNumberOfChildren());
        numberOfChildrenField.setMin(1);
        numberOfChildrenField.setMax(10);
        numberOfChildrenField.setHasControls(true);
        scheduleDetailsForm.add(numberOfChildrenField);
        ComboBox<Month> monthComboBox = new ComboBox<>("Month");
        monthComboBox.setItems(Month.values());
        monthComboBox.setValue(schedule.getMonth());
        scheduleDetailsForm.add(monthComboBox);
        NumberField yearField = new NumberField("Year");
        yearField.setValue((double) schedule.getYear().getValue());
        yearField.setMin(1900);
        yearField.setMax(2100);
        yearField.setHasControls(true);
        scheduleDetailsForm.add(yearField);
        var scheduleTableComponent = new EditableScheduleTableComponent(schedule.getSchedule());
        FormLayout actionsForm = new FormLayout();
        var saveAsNewButton = new Button("Save as new");
        saveAsNewButton.setEnabled(true);
        saveAsNewButton.setIcon(COPY.create());
        saveAsNewButton.addClassNames("base-active-button", "button-with-icon");
        saveAsNewButton.addClickListener(saveAsNewButtonClickHandler(scheduleTableComponent, scheduleNameField, numberOfChildrenField, monthComboBox, yearField));
        saveAsNewButton.setWidthFull();
        var saveButton = new Button("Save");
        saveButton.setEnabled(true);
        saveButton.setIcon(CHECK_SQUARE.create());
        saveButton.addClassNames("base-active-button", "button-with-icon");
        saveButton.addClickListener(saveButtonClickHandler(schedule.getId(), scheduleTableComponent, scheduleNameField, numberOfChildrenField, monthComboBox, yearField));
        saveButton.setWidthFull();
        actionsForm.add(saveButton, saveAsNewButton);
        HorizontalLayout actionsHorizontalLayout = new HorizontalLayout();
        actionsHorizontalLayout.add(saveAsNewButton);
        actionsHorizontalLayout.add(saveButton);
        actionsHorizontalLayout.setWidthFull();
        add(withCssClass("details", opened(new Details("Details", scheduleDetailsForm))));
        add(withCssClass("details", opened(new Details("Schedule", scheduleTableComponent))));
        add(withCssClass("details", opened(new Details("Actions", actionsHorizontalLayout))));
    }

    private Details opened(Details details) {
        details.setOpened(true);
        return details;
    }

    private ComponentEventListener<ClickEvent<Button>> saveAsNewButtonClickHandler(EditableScheduleTableComponent scheduleTableComponent, TextField scheduleName, NumberField numberOfChildren, ComboBox<Month> month, NumberField year) {
        return e -> {
            Schedule editedSchedule = scheduleTableComponent.buildSchedule(Year.of(year.getValue().intValue()), month.getValue(), numberOfChildren.getValue().intValue());
            UUID newScheduleId = scheduleFacade.save(editedSchedule, scheduleName.getValue());
            UI.getCurrent().navigate("schedule/" + newScheduleId);
        };
    }

    private ComponentEventListener<ClickEvent<Button>> saveButtonClickHandler(UUID scheduleId, EditableScheduleTableComponent scheduleTableComponent, TextField scheduleName, NumberField numberOfChildren, ComboBox<Month> month, NumberField year) {
        return e -> {
            Schedule editedSchedule = scheduleTableComponent.buildSchedule(Year.of(year.getValue().intValue()), month.getValue(), numberOfChildren.getValue().intValue());
            UUID newScheduleId = scheduleFacade.save(scheduleId, editedSchedule, scheduleName.getValue());
            UI.getCurrent().navigate("schedule/" + newScheduleId);
        };
    }

    private void scheduleNotFound() {
        add(new Label("Ooops! Something went wrong. We couldn't find requested schedule. Please try again."));
    }
}
