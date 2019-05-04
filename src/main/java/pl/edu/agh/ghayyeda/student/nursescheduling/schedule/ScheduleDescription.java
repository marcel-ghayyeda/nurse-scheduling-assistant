package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;

import java.util.Collection;
import java.util.UUID;

public class ScheduleDescription {

    private final UUID id;
    private final String name;
    private final boolean isFeasible;
    private final Collection<ConstraintViolationsDescription> constraintViolationsDescriptions;

    ScheduleDescription(UUID id, String name, boolean isFeasible, Collection<ConstraintViolationsDescription> constraintViolationsDescriptions) {
        this.id = id;
        this.name = name;
        this.isFeasible = isFeasible;
        this.constraintViolationsDescriptions = constraintViolationsDescriptions;
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

    public Collection<ConstraintViolationsDescription> getConstraintViolationsDescriptions() {
        return constraintViolationsDescriptions;
    }
}
