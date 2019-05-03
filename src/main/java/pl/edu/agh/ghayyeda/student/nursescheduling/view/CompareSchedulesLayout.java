package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule.SchedulesDiffTableComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Route(value = "compare-schedules", layout = MainLayout.class)
@StyleSheet("frontend://css/schedule.css")
public class CompareSchedulesLayout extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver {

    private final ScheduleFacade scheduleFacade;
    private UUID firstScheduleId;
    private UUID secondScheduleId;

    public CompareSchedulesLayout(ScheduleFacade scheduleFacade) {
        this.scheduleFacade = scheduleFacade;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        firstScheduleId = UUID.fromString(parametersMap.get("firstSchedule").get(0));
        secondScheduleId = UUID.fromString(parametersMap.get("secondSchedule").get(0));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        init();
    }

    private void init() {
        removeAll();

        var firstSchedule = scheduleFacade.getById(firstScheduleId).orElseThrow();
        var secondSchedule = scheduleFacade.getById(secondScheduleId).orElseThrow();

        add(new SchedulesDiffTableComponent(firstSchedule, secondSchedule));
        setHeightFull();
    }

}
