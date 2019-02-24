package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class StaticScheduleConstraintFactory implements ScheduleConstraintFactory {

    @Override
    public Collection<ScheduleConstraint> get(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        var alwaysAtLeastOneNurse = AlwaysAtLeastOneNurse.between(validationStartTime, validationEndTime);
        var minimumRestTimeAfterShift = new MinimumRestTimeAfterShift();
        var requiredNumberOfBabySitters = RequiredNumberOfBabySitters.between(validationStartTime, validationEndTime, numberOfChildren);
        return List.of(alwaysAtLeastOneNurse, minimumRestTimeAfterShift, requiredNumberOfBabySitters);
    }

}
