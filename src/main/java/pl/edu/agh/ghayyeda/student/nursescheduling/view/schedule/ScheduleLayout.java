package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.MainLayout;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.NavigationComponents;

import java.util.UUID;

@Route(value = "schedule", layout = MainLayout.class)
@StyleSheet("frontend://css/schedule.css")
public class ScheduleLayout extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver {

    private final NavigationComponents navigationComponents;
    private final ScheduleFacade scheduleFacade;
    private UUID scheduleId;

    public ScheduleLayout(NavigationComponents navigationComponents, ScheduleFacade scheduleFacade) {
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
        add(new ScheduleTableComponent(schedule.getSchedule()));
        add(new ScheduleActionsComponent(scheduleFacade, schedule));
    }


    private void scheduleNotFound() {
        add(new Label("Ooops! Something went wrong. We couldn't find requested schedule. Please try again."));
    }
}
