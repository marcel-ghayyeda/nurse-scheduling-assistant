package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.List;

public class Neighbourhood {

    private final List<Schedule> schedules;

    public Neighbourhood(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

}