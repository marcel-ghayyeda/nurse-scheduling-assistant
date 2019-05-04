package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;

import java.time.Month;
import java.time.Year;
import java.util.Collection;
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

    public Collection<ConstraintViolationsDescription> getConstraintViolationsDescriptions() {
        return scheduleDescription.getConstraintViolationsDescriptions();
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
