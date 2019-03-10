package pl.edu.agh.ghayyeda.student.nursescheduling.view.util;

import com.vaadin.flow.component.Component;

public class ComponentUtil {

    public static <T extends Component> T withCssClass(String cssClass, T component) {
        component.getElement().getClassList().add(cssClass);
        return component;
    }
}
