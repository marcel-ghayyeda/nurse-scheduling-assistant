package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.MainLayout;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.NavigationComponents;

import java.util.UUID;

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
        add(new ScheduleDetailsComponent(schedule));
        var scheduleTableComponent = new EditableScheduleTableComponent(schedule.getSchedule());
        add(scheduleTableComponent);
        var saveButon = new Button("Save");
        saveButon.addClickListener(saveButtonClickHandler(scheduleTableComponent));
        add(saveButon);

    }

    private ComponentEventListener<ClickEvent<Button>> saveButtonClickHandler(ScheduleTableComponent scheduleTableComponent) {
        return e -> {
            UUID scheduleId = scheduleFacade.save(scheduleTableComponent.getSchedule());
            UI.getCurrent().navigate("schedule/" + scheduleId);
        };
    }

    private void scheduleNotFound() {
        add(new Label("Ooops! Something went wrong. We couldn't find requested schedule. Please try again."));
    }
}
