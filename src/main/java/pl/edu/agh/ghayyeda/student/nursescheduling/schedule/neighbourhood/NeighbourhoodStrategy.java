package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood;

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.stream.Stream;

public interface NeighbourhoodStrategy {

    Stream<Schedule> createNeighbourhood(Schedule schedule, ConstraintValidationResult constraintValidationResult);

}
