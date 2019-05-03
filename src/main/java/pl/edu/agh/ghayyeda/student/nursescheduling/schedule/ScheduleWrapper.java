package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.UUID;

public class ScheduleWrapper {
    private final ScheduleDescription scheduleDescription;
    private final Schedule schedule;

    ScheduleWrapper(ScheduleDescription scheduleDescription, Schedule schedule) {
        this.scheduleDescription = scheduleDescription;
        this.schedule = schedule;
    }

    public UUID getId() {
        return scheduleDescription.getId();
    }

    public String getName() {
        return scheduleDescription.getName();
    }

    public boolean isFeasible() {
        return scheduleDescription.isFeasible();
    }

    public List<String> getValidationDescriptions() {
        return scheduleDescription.getValidationDescriptions();
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public int getNumberOfChildren() {
        return getSchedule().getNumberOfChildren();
    }

    public Month getMonth() {
        return getSchedule().getMonth();
    }

    public Year getYear() {
        return getSchedule().getYear();
    }
}
