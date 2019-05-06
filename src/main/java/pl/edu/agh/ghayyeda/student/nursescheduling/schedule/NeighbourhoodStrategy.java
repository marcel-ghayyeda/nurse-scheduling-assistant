package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;

public interface NeighbourhoodStrategy {

    Neighbourhood createNeighbourhood(Schedule schedule, ScheduleConstraintValidationResult constraintValidationResult);

}
