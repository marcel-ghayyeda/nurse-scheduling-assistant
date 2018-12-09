package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.stream.Stream;

public interface ScheduleConstraintRepository {

    Stream<ScheduleConstraint> findAll();

}
