package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR_USER;
import static com.vaadin.flow.component.icon.VaadinIcon.COG;

@Component
public class NavigationComponents {

    private LeftNavigationComponent schedules;
    private LeftNavigationComponent settings;

    public synchronized void setActiveSchedules() {
        if (schedules != null)
            this.schedules.setActive();
    }

    public synchronized Collection<? extends com.vaadin.flow.component.Component> getNavigationComponents() {
        this.schedules = new LeftNavigationComponent("Schedules", CALENDAR_USER.create(), SchedulesLayout.class);
        this.settings = new LeftNavigationComponent("Settings", COG.create(), SettingsLayout.class);
        return List.of(schedules, settings);
    }

}
