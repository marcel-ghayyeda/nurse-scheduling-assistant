package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;

import java.util.stream.Stream;

public interface NeighbourhoodStrategy {

    Stream<Schedule> createNeighbourhood(Schedule schedule, ConstraintValidationResult constraintValidationResult);

}
