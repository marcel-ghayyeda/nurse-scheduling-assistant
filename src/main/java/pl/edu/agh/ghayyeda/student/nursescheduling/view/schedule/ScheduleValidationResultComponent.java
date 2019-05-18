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

import static com.vaadin.flow.component.icon.VaadinIcon.CHECK_SQUARE_O;
import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE;
import static java.util.stream.Collectors.toList;
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

        var violationsDescriptions = schedule.getConstraintViolationsDescriptions();
        ListBox<ConstraintViolationsDescription> violationDescriptionsListBox = setCssClass("constraint-violations-list", new ListBox<>());
        violationDescriptionsListBox.setItems(violationsDescriptions);
        violationDescriptionsListBox.setRenderer(constraintViolationRenderer());
        violationDescriptionsListBox.addValueChangeListener(valueChangeListener(violationDescriptionsListBox));

        var highlightAllButton = new Button("Highlight all");
        highlightAllButton.setEnabled(true);
        highlightAllButton.setIcon(CHECK_SQUARE_O.create());
        highlightAllButton.addClassNames("constraint-violations-button", "button-with-icon");
        highlightAllButton.addClickListener(event -> {
            violationDescriptionsListBox.setEnabled(false);
            highlightAllButton.setEnabled(false);
            highlightAllButton.addClassName("disabled-button");
            clickCallback.accept(violationsDescriptions.stream().map(ConstraintViolationsDescription::getEmployeeDateViolations).flatMap(Collection::stream).collect(toList()));
        });

        var resetButton = new Button("Reset");
        resetButton.setEnabled(true);
        resetButton.setIcon(CLOSE.create());
        resetButton.addClassNames("constraint-violations-button", "button-with-icon", "margin-left-10px");
        resetButton.addClickListener(event -> {
            highlightAllButton.setEnabled(true);
            highlightAllButton.removeClassName("disabled-button");
            violationDescriptionsListBox.setEnabled(true);
            unclickCallback.run();
        });


        detailsContent.add(clickHintLayout);
        detailsContent.add(violationDescriptionsListBox);
        detailsContent.add(highlightAllButton);
        detailsContent.add(resetButton);
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
