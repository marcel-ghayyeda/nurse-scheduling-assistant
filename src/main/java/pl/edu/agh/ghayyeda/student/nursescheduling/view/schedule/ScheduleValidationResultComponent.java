package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;

import java.util.Collection;
import java.util.function.Consumer;

import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.setCssClass;

class ScheduleValidationResultComponent extends HorizontalLayout {

    private final Consumer<Collection<EmployeeDateViolation>> clickCallback;
    private final Runnable unclickCallback;

    ScheduleValidationResultComponent(ScheduleWrapper schedule, Consumer<Collection<EmployeeDateViolation>> clickCallback, Runnable unclickCallback) {
        var details = new Details("Validation results", detailsContent(schedule));
        details.setOpened(false);
        addClassName("details");
        add(details);
        this.clickCallback = clickCallback;
        this.unclickCallback = unclickCallback;
    }

    private Div detailsContent(ScheduleWrapper schedule) {
        var detailsContent = new Div();
        Label clickHint = new Label("Click to highlight affected employees and dates");
        HorizontalLayout clickHintLayout = new HorizontalLayout();
        clickHintLayout.add(VaadinIcon.QUESTION_CIRCLE.create());
        clickHintLayout.add(clickHint);

        ListBox<ConstraintViolationsDescription> violationDescriptions = setCssClass("constraint-violations-list", new ListBox<>());
        violationDescriptions.setItems(schedule.getConstraintViolationsDescriptions());
        violationDescriptions.setRenderer(constraintViolationRenderer());
        violationDescriptions.addValueChangeListener(valueChangeListener(violationDescriptions));

        var deselectButton = new Button("Deselect");
        deselectButton.setEnabled(true);
        deselectButton.setIcon(CLOSE.create());
        deselectButton.addClassNames("constraint-violations-deselect-button", "button-with-icon");
        deselectButton.addClickListener(event -> unclickCallback.run());

        detailsContent.add(clickHintLayout);
        detailsContent.add(violationDescriptions);
        detailsContent.add(deselectButton);
        return detailsContent;
    }

    private ComponentRenderer<HorizontalLayout, ConstraintViolationsDescription> constraintViolationRenderer() {
        return new ComponentRenderer<>(constraintViolationsDescription -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Div div = new Div(new Span(constraintViolationsDescription.getDescription()));
            horizontalLayout.add(div);
            horizontalLayout.addClassName("validation-description");
            return horizontalLayout;
        });
    }

    private HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ListBox<ConstraintViolationsDescription>, ConstraintViolationsDescription>> valueChangeListener(ListBox<ConstraintViolationsDescription> violationDescriptions) {
        return event -> {
            if (event.getValue() != null) {
                clickCallback.accept(event.getValue().getEmployeeDateViolations());
            }
        };
    }


}
