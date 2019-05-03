package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.List;
import java.util.UUID;

public class ScheduleDescription {

    private final UUID id;
    private final String name;
    private final boolean isFeasible;
    private final List<String> validationDescriptions;

    ScheduleDescription(UUID id, String name, boolean isFeasible, List<String> validationDescriptions) {
        this.id = id;
        this.name = name;
        this.isFeasible = isFeasible;
        this.validationDescriptions = validationDescriptions;
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

    public List<String> getValidationDescriptions() {
        return validationDescriptions;
    }
}
