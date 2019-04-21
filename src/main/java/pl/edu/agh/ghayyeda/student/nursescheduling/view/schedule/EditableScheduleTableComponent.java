package pl.edu.agh.ghayyeda.student.nursescheduling.view.schedule;

import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.ValueProvider;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.LocalDate;

import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.SICK_LEAVE;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.VACATION;

class EditableScheduleTableComponent extends ScheduleTableComponent {

    EditableScheduleTableComponent(Schedule initialSchedule) {
        super(initialSchedule);
    }

    @Override
    protected ValueProvider<ScheduleLayoutRow, Div> createShiftComponentColumn(LocalDate date) {
        return scheduleLayoutRow -> {
            var column = super.createShiftComponentColumn(date).apply(scheduleLayoutRow);
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(column);
            contextMenu.addItem("Set vacation", e -> {
                scheduleLayoutRow.shifts.put(date, VACATION);
                getDataProvider().refreshItem(scheduleLayoutRow);
            });
            contextMenu.addItem("Set sick leave", e -> {
                scheduleLayoutRow.shifts.put(date, SICK_LEAVE);
                getDataProvider().refreshItem(scheduleLayoutRow);
            });
            return column;
        };
    }
}
