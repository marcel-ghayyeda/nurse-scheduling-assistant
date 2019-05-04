package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;

import java.util.UUID;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES;
import static com.vaadin.flow.component.icon.VaadinIcon.EDIT;
import static com.vaadin.flow.component.icon.VaadinIcon.EXPAND_SQUARE;
import static java.util.stream.Collectors.toList;

@Route(value = "schedules", layout = MainLayout.class)
@StyleSheet("frontend://css/schedule.css")
public class SchedulesLayout extends VerticalLayout {

    public SchedulesLayout(ScheduleFacade scheduleFacade) {
        var scheduleDescriptions = scheduleFacade.getLatestScheduleDescriptions().stream()
                .map(x -> new ScheduleRow(x.getId(), x.getName(), x.isFeasible()))
                .collect(toList());
        Grid<ScheduleRow> grid = new Grid<>();
        grid.setItems(scheduleDescriptions);
        grid.addColumn(ScheduleRow::getName).setHeader("Schedule");
        grid.addComponentColumn(this::statusColumn).setHeader("Status");
        grid.addComponentColumn(this::actionColumn).setHeader("Actions");
        grid.addThemeVariants(LUMO_ROW_STRIPES);

        add(grid);
    }

    private Component statusColumn(ScheduleRow scheduleRow) {
        var status = scheduleRow.isFeasible() ? "valid" : "invalid";
        var statusButton = new Button(status);
        statusButton.setEnabled(false);
        statusButton.addClassName("status-button");
        statusButton.addClassName("button-80px");
        statusButton.addClassName(scheduleRow.isFeasible() ? "feasible-status-button" : "not-feasible-status-button");
        return statusButton;
    }

    private Component actionColumn(ScheduleRow scheduleRow) {
        Div parentDiv = new Div();
        Button showButton = new Button("Show");
        showButton.addClassNames("base-active-button");
        showButton.setIcon(EXPAND_SQUARE.create());
        showButton.addClickListener(e -> UI.getCurrent().navigate("schedule/" + scheduleRow.getId()));

        Button editButton = new Button("Edit");
        editButton.addClassNames("base-active-button");
        editButton.setIcon(EDIT.create());
        editButton.addClickListener(e -> UI.getCurrent().navigate("schedule-edit/" + scheduleRow.getId()));

        parentDiv.add(showButton, editButton);
        return parentDiv;
    }

    private static class ScheduleRow {
        private final UUID id;
        private final String name;
        private final boolean feasible;

        private ScheduleRow(UUID id, String name, boolean feasible) {
            this.id = id;
            this.name = name;
            this.feasible = feasible;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isFeasible() {
            return feasible;
        }
    }
}
