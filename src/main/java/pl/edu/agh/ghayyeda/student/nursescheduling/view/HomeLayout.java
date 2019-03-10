package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class HomeLayout extends VerticalLayout {
    public HomeLayout() {
        add(new Label("Home Page"));
    }
}
