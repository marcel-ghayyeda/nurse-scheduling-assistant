package pl.edu.agh.ghayyeda.student.nursescheduling.view.util;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

public class ComponentUtil {

    public static <T extends Component> T setCssClass(String cssClass, T component) {
        component.getElement().getClassList().clear();
        return withCssClass(cssClass, component);
    }

    public static <T extends Component> T withCssClass(String cssClass, T component) {
        component.getElement().getClassList().add(cssClass);
        return component;
    }

    public static <X extends AbstractField<X, T>, T> X setValue(AbstractField<X, T> field, T value) {
        field.setValue(value);
        return (X) field;
    }

    public static VerticalLayout centered(Component component) {
        var verticalLayout = new VerticalLayout(component);
        verticalLayout.setHorizontalComponentAlignment(CENTER, component);
        return verticalLayout;
    }

    public static VerticalLayout left(Component component) {
        var verticalLayout = new VerticalLayout(component);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, component);
        return verticalLayout;
    }

    public static VerticalLayout right(Component component) {
        var verticalLayout = new VerticalLayout(component);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, component);
        return verticalLayout;
    }

    public static Details opened(Details details) {
        details.setOpened(true);
        return details;
    }

}
