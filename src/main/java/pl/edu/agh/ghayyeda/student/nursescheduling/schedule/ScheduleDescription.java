package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.UUID;

public class ScheduleDescription {

    private final UUID id;
    private final String name;
    private final boolean isFeasible;

    ScheduleDescription(UUID id, String name, boolean isFeasible) {
        this.id = id;
        this.name = name;
        this.isFeasible = isFeasible;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isFeasible() {
        return isFeasible;
    }
}
