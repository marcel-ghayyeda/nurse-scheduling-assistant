package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

public interface ScheduleConstraint {

    ConstraintValidationResult validate(Schedule schedule);
}
