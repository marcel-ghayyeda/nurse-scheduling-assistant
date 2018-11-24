package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.OptionalInt;

public interface ScheduleConstraintValidationResult {

    boolean isFeasible();

    OptionalInt getPenalty();

}
