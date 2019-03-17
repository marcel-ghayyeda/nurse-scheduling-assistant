package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
public class PenaltyAwareScheduleConstraintFactory implements ScheduleConstraintFactory {

    @Override
    public Collection<ScheduleConstraint> get(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        var minimumRestTimeAfterShift = new PenaltyAwareMinimumRestTimeAfterShift();
        var requiredNumberOfBabySitters = PenaltyAwareRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, numberOfChildren);
        return List.of(minimumRestTimeAfterShift, requiredNumberOfBabySitters);
    }

}
