package pl.edu.agh.ghayyeda.student.nursescheduling.view;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleFacade;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.vaadin.flow.component.icon.VaadinIcon.EDIT;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS_MINUS;
import static pl.edu.agh.ghayyeda.student.nursescheduling.view.util.ComponentUtil.*;

@Route(value = "compare-schedules-list", layout = MainLayout.class)
@StyleSheet("frontend://css/schedule.css")
public class CompareSchedulesListLayout extends VerticalLayout {

    public CompareSchedulesListLayout(ScheduleFacade scheduleFacade) {
        List<ScheduleDescription> scheduleDescriptions = scheduleFacade.getLatestScheduleDescriptions();


        ListBox<ScheduleDescription> firstScheduleList = new ListBox<>();
        firstScheduleList.setItems(scheduleDescriptions);
        firstScheduleList.setRenderer(scheduleDescriptionRenderer());
        setCssClass("compare-schedule-list", firstScheduleList);

        ListBox<ScheduleDescription> secondScheduleList = new ListBox<>();
        secondScheduleList.setItems(scheduleDescriptions);
        secondScheduleList.setRenderer(scheduleDescriptionRenderer());
        setCssClass("compare-schedule-list", secondScheduleList);


        firstScheduleList.addValueChangeListener(valueChangeListener(secondScheduleList));
        secondScheduleList.addValueChangeListener(valueChangeListener(firstScheduleList));

        firstScheduleList.setItemEnabledProvider(scheduleDescription -> secondScheduleList.getOptionalValue().map(ScheduleDescription::getId).map(id -> !id.equals(scheduleDescription.getId())).orElse(true));
        secondScheduleList.setItemEnabledProvider(scheduleDescription -> firstScheduleList.getOptionalValue().map(ScheduleDescription::getId).map(id -> !id.equals(scheduleDescription.getId())).orElse(true));

        var horizontalLayout = new HorizontalLayout();

        horizontalLayout.add(firstScheduleList);
        horizontalLayout.add(secondScheduleList);
        horizontalLayout.setWidthFull();

        Button compareButton = new Button("Compare selected schedules");
        compareButton.addClassNames("base-active-button");
        compareButton.setIcon(EDIT.create());

        compareButton.addClickListener(event -> {
            var queryParameters = new QueryParameters(Map.of("firstSchedule", List.of(firstScheduleList.getValue().getId().toString()), "secondSchedule", List.of(secondScheduleList.getValue().getId().toString())));
            UI.getCurrent().navigate("compare-schedules", queryParameters);
        });

        Label hint = new Label("Select two schedules to compare");
        HorizontalLayout hintLayout = new HorizontalLayout();
        hintLayout.add(PLUS_MINUS.create());
        hintLayout.add(hint);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(hintLayout);
        verticalLayout.add(horizontalLayout);
        verticalLayout.add(centered(compareButton));

        add(verticalLayout);
    }

    private HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ListBox<ScheduleDescription>, ScheduleDescription>> valueChangeListener(ListBox<ScheduleDescription> anotherList) {
        return event -> {
            if (Objects.equals(event.getOldValue(), event.getValue())) {
                return;
            }
            if (event.getValue() != null) {
                anotherList.getDataProvider().refreshItem(event.getValue());

            }
            if (event.getOldValue() != null) {
                anotherList.getDataProvider().refreshItem(event.getOldValue());
            }
        };
    }

    private ComponentRenderer<HorizontalLayout, ScheduleDescription> scheduleDescriptionRenderer() {
        return new ComponentRenderer<>(scheduleDescription -> {
            var name = new Div(new Label("Schedule: " + scheduleDescription.getName()));
            name.setWidth("70%");
            var statusButton = new Button(scheduleDescription.isFeasible() ? "valid" : "invalid");
            statusButton.setEnabled(false);
            statusButton.addClassName("status-button");
            statusButton.addClassName("status-button-fixed-width");
            statusButton.addClassName(scheduleDescription.isFeasible() ? "feasible-status-button" : "not-feasible-status-button");

            var statusButtonDiv = new Div(right(statusButton));
            statusButtonDiv.setWidth("30%");
            var horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(name);
            horizontalLayout.add(statusButtonDiv);
            horizontalLayout.setWidthFull();

            horizontalLayout.getStyle().set("display", "flex")
                    .set("alignItems", "center");

            return horizontalLayout;
        });
    }


}
