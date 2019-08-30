package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.MainLayout;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.UUID;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.*;

@Route(value = "new-schedule", layout = MainLayout.class)
@StyleSheet("frontend://css/schedule.css")
public class ScheduleNewLayout extends VerticalLayout implements AfterNavigationObserver {

    private final ScheduleFacade scheduleFacade;

    public ScheduleNewLayout(ScheduleFacade scheduleFacade) {
        this.scheduleFacade = scheduleFacade;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        removeAll();
        init();
    }

    private void init() {
        FormLayout scheduleDetailsForm = new FormLayout();
        TextField scheduleNameField = setValue(new TextField("Schedule name"), "New Schedule");
        scheduleDetailsForm.add(scheduleNameField);
        NumberField numberOfChildrenField = setValue(new NumberField("Number of children"), (double) 6);
        numberOfChildrenField.setMin(1);
        numberOfChildrenField.setHasControls(true);
        scheduleDetailsForm.add(numberOfChildrenField);

        ComboBox<Month> monthComboBox = new ComboBox<>("Month");
        monthComboBox.setItems(Month.values());
        monthComboBox.setValue(LocalDate.now().getMonth());
        scheduleDetailsForm.add(monthComboBox);

        NumberField yearField = new NumberField("Year");
        yearField.setValue((double) LocalDate.now().getYear());
        yearField.setMin(1900);
        yearField.setMax(2100);
        yearField.setHasControls(true);
        scheduleDetailsForm.add(yearField);

        NumberField numberOfNursesField = setValue(new NumberField("Number of nurses"), (double) 3);
        numberOfNursesField.setMin(1);
        numberOfNursesField.setHasControls(true);
        scheduleDetailsForm.add(numberOfNursesField);

        FormLayout actionsForm = new FormLayout();
        var saveAsNewButton = new Button("Generate new schedule with random shifts");
        saveAsNewButton.setEnabled(true);
        saveAsNewButton.setIcon(PLUS.create());
        saveAsNewButton.addClassNames("base-active-button", "button-with-icon");
        saveAsNewButton.addClickListener(saveAsNewButtonClickHandler(scheduleNameField, numberOfChildrenField, numberOfNursesField, monthComboBox, yearField));
        saveAsNewButton.setWidthFull();
        actionsForm.add(saveAsNewButton);
        HorizontalLayout actionsHorizontalLayout = new HorizontalLayout();
        actionsHorizontalLayout.add(saveAsNewButton);
        actionsHorizontalLayout.setWidthFull();

        add(withCssClass("details", opened(new Details("Details", scheduleDetailsForm))));
        add(withCssClass("details", opened(new Details("Actions", actionsHorizontalLayout))));

    }


    private ComponentEventListener<ClickEvent<Button>> saveAsNewButtonClickHandler(TextField scheduleName, NumberField numberOfChildren, NumberField numberOfNurses, ComboBox<Month> month, NumberField year) {
        return e -> {
            Schedule schedule = scheduleFacade.generateRandomSchedule(Year.of(year.getValue().intValue()), month.getValue(), numberOfChildren.getValue().intValue(), numberOfNurses.getValue().intValue());
            UUID scheduleId = scheduleFacade.save(schedule, scheduleName.getValue());
            UI.getCurrent().navigate("schedule/" + scheduleId);
        };
    }
}
