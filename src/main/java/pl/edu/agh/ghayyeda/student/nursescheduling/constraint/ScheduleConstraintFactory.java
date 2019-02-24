package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ScheduleConstraintFactory {

    Collection<ScheduleConstraint> get(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren);
}
