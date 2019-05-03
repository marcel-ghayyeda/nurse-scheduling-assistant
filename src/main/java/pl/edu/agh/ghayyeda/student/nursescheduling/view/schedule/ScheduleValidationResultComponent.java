package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;

class ScheduleValidationResultComponent extends HorizontalLayout {

    ScheduleValidationResultComponent(ScheduleWrapper schedule) {
        var details = new Details("Validation results", detailsContent(schedule));
        details.setOpened(false);
        addClassName("details");
        add(details);
    }

    private Div detailsContent(ScheduleWrapper schedule) {
        var detailsContent = new Div();
        schedule.getValidationDescriptions()
                .stream()
                .map(this::buildDescriptionComponent)
                .forEach(detailsContent::add);
        return detailsContent;
    }

    private Component buildDescriptionComponent(String validationDescription) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Icon icon = VaadinIcon.INFO_CIRCLE.create();
        icon.setSize("16px");
        horizontalLayout.add(icon);
        horizontalLayout.add(new Div(new Span(validationDescription)));
        horizontalLayout.addClassName("validation-description");
        return horizontalLayout;
    }


}
