package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Month;
import java.time.Year;

public class ScheduleWrapper {
    private final ScheduleDescription scheduleDescription;
    private final boolean isFeasible;
    private final Schedule schedule;

    ScheduleWrapper(ScheduleDescription scheduleDescription, boolean isFeasible, Schedule schedule) {
        this.scheduleDescription = scheduleDescription;
        this.isFeasible = isFeasible;
        this.schedule = schedule;
    }

    public String getName() {
        return scheduleDescription.getName();
    }

    public boolean isFeasible() {
        return isFeasible;
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
