package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.stream.Stream;

public class StaticScheduleConstraintRepositoryImpl implements ScheduleConstraintRepository {

    @Override
    public Stream<ScheduleConstraint> findAll() {
        return Stream.empty();
    }

}
