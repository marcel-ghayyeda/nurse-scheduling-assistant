package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.LeftClickableComponent;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftSubMenuBuilder;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.Push;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;

import java.util.function.Function;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Push
@StyleSheet("frontend://css/main.css")
public class MainLayout extends AppLayoutRouterLayout {

    private final ScheduleFacade scheduleFacade;

    public MainLayout(ScheduleFacade scheduleFacade) {
        this.scheduleFacade = scheduleFacade;
        render();
    }

    private void render() {
        init(AppLayoutBuilder.get(Behaviour.LEFT_HYBRID)
                .withTitle("Nurse Scheduling Assistant")
                .withAppMenu(LeftAppMenuBuilder.get()
                        .add(buildScheduleSubmenu())
                        .add(new LeftNavigationComponent("Settings", COG.create(), SettingsLayout.class))
                        .build())
                .build());
    }

    private Component buildScheduleSubmenu() {
        var leftSubMenuBuilder = LeftSubMenuBuilder.get("Schedules", CALENDAR_USER.create());
        scheduleFacade.getLatestScheduleDescriptions().stream()
                .map(buildLeftComponent())
                .forEach(leftSubMenuBuilder::add);
        return leftSubMenuBuilder.build();
    }

    private Function<ScheduleDescription, Component> buildLeftComponent() {
        return scheduleDescription -> {
            var icon = scheduleDescription.isFeasible() ? CHECK_CIRCLE.create() : CLOSE_CIRCLE.create();
            var leftComponent = new LeftClickableComponent(scheduleDescription.getName(), icon, __ -> UI.getCurrent().navigate("schedule/" + scheduleDescription.getId()));
            leftComponent.addClassName(scheduleDescription.isFeasible() ? "left-menu-feasible-schedule" : "left-menu-not-feasible-schedule");
            leftComponent.setTitle((scheduleDescription.isFeasible() ? "Valid: " : "Invalid: ") + scheduleDescription.getName());
            return leftComponent;
        };
    }

}


