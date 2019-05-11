package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.LocalDateTime;

public interface ScheduleConstraintValidationFacade {
    ConstraintValidationResult validate(Schedule schedule);

    ConstraintValidationResult validate(Schedule schedule, LocalDateTime validationStartTime, LocalDateTime validationEndTime);
}
