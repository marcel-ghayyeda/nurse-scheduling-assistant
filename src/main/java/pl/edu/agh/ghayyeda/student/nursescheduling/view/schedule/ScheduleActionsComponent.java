package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleWrapper;

import static com.vaadin.flow.component.icon.VaadinIcon.EDIT;
import static com.vaadin.flow.component.icon.VaadinIcon.TOOLS;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.centered;

class ScheduleActionsComponent extends HorizontalLayout {


    ScheduleActionsComponent(ScheduleFacade scheduleFacade, ScheduleWrapper schedule) {
        var actions = new Details("Actions", actionsContent(scheduleFacade, schedule));
        actions.setOpened(true);
        addClassName("schedule-actions");
        add(actions);
    }

    private HorizontalLayout actionsContent(ScheduleFacade scheduleFacade, ScheduleWrapper schedule) {
        var actionsContent = new HorizontalLayout();
        actionsContent.setWidthFull();
        actionsContent.add(fixButton(scheduleFacade, schedule));
        actionsContent.add(editButton(schedule));
        return actionsContent;
    }

    private Component editButton(ScheduleWrapper schedule) {
        var editButton = new Button("Edit");
        editButton.addClassNames("base-active-button", "button-with-icon");
        editButton.setIcon(EDIT.create());
        editButton.addClickListener(e -> UI.getCurrent().navigate("schedule-edit/" + schedule.getId()));
        editButton.setWidthFull();
        return editButton;
    }

    private Component fixButton(ScheduleFacade scheduleFacade, ScheduleWrapper schedule) {
        var statusButton = new Button("Fix");
        statusButton.setEnabled(true);
        statusButton.setIcon(TOOLS.create());
        statusButton.addClassNames("base-active-button", "button-with-icon");
        statusButton.setWidthFull();
        statusButton.addClickListener(event -> {

            Dialog dialog = new Dialog();

            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            var dialogMessage = new Label("Your schedule is currently being processed. It will take a few minutes.");
            ProgressBar progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);


            dialog.add(progressBar);
            dialog.add(dialogMessage);
            String newScheduleName = schedule.getName() + " fixed";
            var futureSchedule = scheduleFacade.fixAsync(schedule.getSchedule(), newScheduleName)
                    .handle((foundScheduleId, exception) -> {
                        getUI().get().access(() -> {
                            dialog.close();
                            if (foundScheduleId != null) {
                                var successMessage = new Label(String.format("New schedule has been saved as \"%s\"", newScheduleName));
                                Dialog successDialog = new Dialog(successMessage);
                                Button okButton = new Button("OK", clickEvent -> successDialog.close());
                                successDialog.add(centered(okButton));
                                successDialog.open();
                            } else {
                                var failureMessage = new Label("Ooops, something went wrong. Could not fix this schedule.");
                                Dialog failureDialog = new Dialog(failureMessage);
                                Button okButton = new Button("OK", clickEvent -> failureDialog.close());
                                failureDialog.add(centered(okButton));
                                failureDialog.open();
                            }

                        });
                        return null;
                    });

            var cancelButton = new Button("Cancel", cancelEvent -> {
                futureSchedule.cancel(true);
                dialog.close();
            });
            dialog.add(centered(cancelButton));
            dialog.open();
        });

        return statusButton;
    }

}
