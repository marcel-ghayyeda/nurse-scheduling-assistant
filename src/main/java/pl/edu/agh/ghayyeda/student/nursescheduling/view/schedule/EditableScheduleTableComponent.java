package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.ValueProvider;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.LocalDate;

import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.DAY_OFF;

class EditableScheduleTableComponent extends ScheduleTableComponent {

    EditableScheduleTableComponent(Schedule initialSchedule) {
        super(initialSchedule);
    }

    @Override
    protected ValueProvider<ScheduleLayoutRow, Div> createShiftComponentColumn(LocalDate date) {
        return scheduleLayoutRow -> {
            var column = super.createShiftComponentColumn(date).apply(scheduleLayoutRow);
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget((Component) column);
            contextMenu.addItem("Set day off", e -> {
                scheduleLayoutRow.shifts.put(date, DAY_OFF);
                getDataProvider().refreshItem(scheduleLayoutRow);
            });
            return column;
        };
    }
}
