package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.failfast;

import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.util.ScheduleValidationUtils;

import java.time.LocalDateTime;

@Component
public class FailFastScheduleConstraintValidationFacade implements ScheduleConstraintValidationFacade {

    private final FailFastScheduleConstraintFactory failFastScheduleConstraintFactory;

    public FailFastScheduleConstraintValidationFacade(FailFastScheduleConstraintFactory failFastScheduleConstraintFactory) {
        this.failFastScheduleConstraintFactory = failFastScheduleConstraintFactory;
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var validationStartTime = ScheduleValidationUtils.getStandardValidationStartTime(schedule);
        var validationEndTime = ScheduleValidationUtils.getStandardValidationEndTime(schedule);
        return validate(schedule, validationStartTime, validationEndTime);
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule, LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        var scheduleConstraints = failFastScheduleConstraintFactory.get(validationStartTime, validationEndTime, schedule.getNumberOfChildren());

        for (ScheduleConstraint scheduleConstraint : scheduleConstraints) {
            var validationResult = scheduleConstraint.validate(schedule);
            if (!validationResult.isFeasible()) {
                System.out.println(String.format("Not feasible due to %s constraint", scheduleConstraint.getClass()));
                return ScheduleConstraintValidationResult.notFeasibleConstraintValidationResult();
            }
        }
        return ScheduleConstraintValidationResult.feasibleConstraintValidationResult();
    }

}
