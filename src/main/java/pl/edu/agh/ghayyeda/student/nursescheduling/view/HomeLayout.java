package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.centered;

@Route(value = "", layout = MainLayout.class)
@StyleSheet("frontend://css/main.css")
public class HomeLayout extends VerticalLayout {
    public HomeLayout() {
        Image nurseImage = new Image("frontend/nurse.png", "Pretty nurse");
        nurseImage.setHeight("700px");
        Label welcomeMessage = new Label("Welcome to the Nurse Scheduling Assistant!");
        welcomeMessage.addClassName("welcome-message");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(centered(nurseImage));
        verticalLayout.add(centered(welcomeMessage));
        add(verticalLayout);
    }
}
