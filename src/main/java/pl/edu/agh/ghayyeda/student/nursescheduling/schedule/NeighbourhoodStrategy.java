package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;

public interface NeighbourhoodStrategy {

    Neighbourhood createNeighbourhood(Schedule schedule, ConstraintValidationResult constraintValidationResult);

}
