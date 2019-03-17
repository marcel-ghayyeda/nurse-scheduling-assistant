package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.LocalDateTime;

public interface ScheduleConstraintValidationFacade {
    ScheduleConstraintValidationResult validate(Schedule schedule);

    ScheduleConstraintValidationResult validate(Schedule schedule, LocalDateTime validationStartTime, LocalDateTime validationEndTime);
}
