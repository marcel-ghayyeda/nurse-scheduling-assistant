package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.LocalDateTime;

public class ScheduleConstraintValidationFacade {

    private final StaticScheduleConstraintFactory staticScheduleConstraintFactory;

    public ScheduleConstraintValidationFacade(StaticScheduleConstraintFactory staticScheduleConstraintFactory) {
        this.staticScheduleConstraintFactory = staticScheduleConstraintFactory;
    }

    public ScheduleConstraintValidationResult validate(Schedule schedule, LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        var scheduleConstraints = staticScheduleConstraintFactory.get(validationStartTime, validationEndTime, numberOfChildren);

        for (ScheduleConstraint scheduleConstraint : scheduleConstraints) {
            var validationResult = scheduleConstraint.validate(schedule);
            if (!validationResult.isFeasible()) {
                System.out.println(String.format("Not feasible due to %s constraint", scheduleConstraint.getClass()));
                return HardConstraintValidationResult.notFeasibleConstraintValidationResult();
            }
        }
        return HardConstraintValidationResult.feasibleConstraintValidationResult();
    }

}
