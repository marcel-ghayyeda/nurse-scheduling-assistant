package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;

import static java.time.format.TextStyle.FULL;
import static java.util.Locale.US;

class ScheduleDetailsComponent extends HorizontalLayout {

    ScheduleDetailsComponent(ScheduleWrapper schedule) {
        var details = new Details("Schedule details", detailsContent(schedule));
        details.setOpened(true);
        addClassName("schedule-details");
        add(details);
    }

    private Div detailsContent(ScheduleWrapper schedule) {
        var detailsContent = new Div();
        detailsContent.add(statusButton(schedule));
        detailsContent.add(nameButton(schedule));
        detailsContent.add(numberOfChildrenButton(schedule));
        detailsContent.add(monthButton(schedule));
        return detailsContent;
    }

    private Component statusButton(ScheduleWrapper schedule) {
        var status = schedule.isFeasible() ? "valid" : "invalid";
        var statusButton = new Button("Status: " + status);
        statusButton.setEnabled(false);
        statusButton.addClassName("status-button");
        statusButton.addClassName(schedule.isFeasible() ? "feasible-status-button" : "not-feasible-status-button");
        return statusButton;
    }

    private Component nameButton(ScheduleWrapper schedule) {
        var nameButton = new Button(schedule.getName());
        nameButton.setEnabled(false);
        nameButton.addClassName("name-button");
        return nameButton;
    }

    private Component numberOfChildrenButton(ScheduleWrapper schedule) {
        var numberOfChildrenButton = new Button("Number of children: " + schedule.getNumberOfChildren());
        numberOfChildrenButton.setEnabled(false);
        numberOfChildrenButton.addClassName("number-of-children-button");
        return numberOfChildrenButton;
    }

    private Component monthButton(ScheduleWrapper schedule) {
        var monthButton = new Button(schedule.getMonth().getDisplayName(FULL, US) + " " + schedule.getYear().getValue());
        monthButton.setEnabled(false);
        monthButton.addClassName("month-button");
        return monthButton;
    }
}
