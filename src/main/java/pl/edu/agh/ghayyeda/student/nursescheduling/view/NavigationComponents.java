package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule.ScheduleNewLayout;
import pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule.SchedulesLayout;

import java.util.Collection;
import java.util.List;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Component
public class NavigationComponents {

    private LeftNavigationComponent schedules;
    private LeftNavigationComponent newSchedule;
    private LeftNavigationComponent compareSchedules;

    public synchronized void setActiveSchedules() {
        if (schedules != null)
            this.schedules.setActive();
    }

    public synchronized Collection<? extends com.vaadin.flow.component.Component> getNavigationComponents() {
        this.schedules = new LeftNavigationComponent("Schedules", CALENDAR_USER.create(), SchedulesLayout.class);
        this.compareSchedules = new LeftNavigationComponent("Compare schedules", PLUS_MINUS.create(), CompareSchedulesListLayout.class);
        this.newSchedule = new LeftNavigationComponent("New Schedule", PLUS.create(), ScheduleNewLayout.class);
        return List.of(schedules, compareSchedules, newSchedule);
    }

}
