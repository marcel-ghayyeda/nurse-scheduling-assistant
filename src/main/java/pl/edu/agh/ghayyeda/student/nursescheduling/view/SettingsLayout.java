package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "settings", layout = MainLayout.class)
public class SettingsLayout extends VerticalLayout {
    public SettingsLayout() {
        add(new Label("Settings view"));
    }
}
